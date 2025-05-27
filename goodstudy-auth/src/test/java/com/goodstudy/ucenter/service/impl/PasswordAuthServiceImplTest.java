package com.goodstudy.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.goodstudy.ucenter.feignclient.CheckCodeClient;
import com.goodstudy.ucenter.mapper.XcUserMapper;
import com.goodstudy.ucenter.model.dto.AuthParamsDto;
import com.goodstudy.ucenter.model.dto.XcUserExt;
import com.goodstudy.ucenter.model.po.XcUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordAuthServiceImplTest {

    @Mock
    private XcUserMapper xcUserMapper;

    @Mock
    private CheckCodeClient checkCodeClient;

    @InjectMocks
    private PasswordAuthServiceImpl passwordAuthService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setUp() {
        // 注入自定义的 passwordEncoder
        passwordAuthService.passwordEncoder = passwordEncoder;
    }

    @Test
    public void testExecute_UserNotFound_ThrowsException() {
        AuthParamsDto dto = new AuthParamsDto();
        dto.setUsername("nonexistent");
        dto.setPassword("password");

        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            passwordAuthService.execute(dto);
        });

        assertEquals("账号不存在", exception.getMessage());
        verify(xcUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    public void testExecute_PasswordMismatch_ThrowsException() {
        AuthParamsDto dto = new AuthParamsDto();
        dto.setUsername("user123");
        dto.setPassword("wrongpassword");

        XcUser user = new XcUser();
        user.setUsername("user123");
        user.setPassword(passwordEncoder.encode("correctpassword")); // 正确密码

        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            passwordAuthService.execute(dto);
        });

        assertEquals("账号或密码错误", exception.getMessage());
    }

    @Test
    public void testExecute_ValidCredentials_ReturnsXcUserExt() {
        AuthParamsDto dto = new AuthParamsDto();
        dto.setUsername("user123");
        dto.setPassword("correctpassword");

        XcUser user = new XcUser();
        user.setUsername("user123");
        user.setPassword(passwordEncoder.encode("correctpassword"));
        user.setId("u1");
        Date date = new Date();
        user.setCreateTime(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        //user.setCreateTime(new Date());

        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        XcUserExt result = passwordAuthService.execute(dto);

        assertNotNull(result);
        assertEquals("user123", result.getUsername());
        assertEquals("u1", result.getId());
    }

    @Test
    public void testExecute_VerifyCodeDisabled() {
        // 验证码部分被注释掉，默认不启用
        AuthParamsDto dto = new AuthParamsDto();
        dto.setUsername("user123");
        dto.setPassword("correctpassword");

        XcUser user = new XcUser();
        user.setUsername("user123");
        user.setPassword(passwordEncoder.encode("correctpassword"));

        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        XcUserExt result = passwordAuthService.execute(dto);

        assertNotNull(result);
    }
}