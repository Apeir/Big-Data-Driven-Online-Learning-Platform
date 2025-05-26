package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private XcUserMapper xcUserMapper;

    @Mock
    private XcMenuMapper xcMenuMapper;

    @Mock
    private ApplicationContext applicationContext;

    // 模拟的用户ID
    private static final String USER_ID = "1";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password";

    @BeforeEach
    void setUp() {
        // 可以在这里设置默认行为
    }

    @Test
    void testLoadUserByUsername_WithValidAuthType_ShouldReturnUserDetails() {
        AuthParamsDto dto = new AuthParamsDto();
        dto.setAuthType("password");
        String inputJson = JSON.toJSONString(dto);

        AuthService mockService = mock(AuthService.class);
        when(applicationContext.getBean("password_authservice", AuthService.class)).thenReturn(mockService);

        XcUserExt mockUserExt = new XcUserExt();
        mockUserExt.setId("1");
        mockUserExt.setPassword("123456");

        when(mockService.execute(dto)).thenReturn(mockUserExt);

        UserDetails userDetails = userService.loadUserByUsername(inputJson);
        assertNotNull(userDetails);
    }

    @Test
    void testLoadUserByUsername_WithInvalidJson_ShouldThrowException() {
        String invalidJson = "{invalid: json}";

        assertThrows(RuntimeException.class, () -> {
            userService.loadUserByUsername(invalidJson);
        });
    }


    // ❌ 对应的 AuthService Bean 不存在
    @Test
    void testLoadUserByUsername_WhenAuthServiceNotFound_ShouldThrowException() {
        AuthParamsDto dto = new AuthParamsDto();
        dto.setAuthType("wx");
        String inputJson = JSON.toJSONString(dto);

        when(applicationContext.getBean("wx_authservice", AuthService.class))
                .thenThrow(new RuntimeException("No such bean"));

        assertThrows(RuntimeException.class, () -> {
            userService.loadUserByUsername(inputJson);
        });
    }

    // ❌ AuthService 执行失败
    @Test
    void testLoadUserByUsername_WhenAuthServiceThrowsException_ShouldPropagate() {
        AuthParamsDto dto = new AuthParamsDto();
        dto.setAuthType("password");
        String inputJson = JSON.toJSONString(dto);

        AuthService mockService = mock(AuthService.class);
        when(applicationContext.getBean("password_authservice", AuthService.class)).thenReturn(mockService);
        when(mockService.execute(dto)).thenThrow(new RuntimeException("认证执行失败"));

        assertThrows(RuntimeException.class, () -> {
            userService.loadUserByUsername(inputJson);
        });
    }

    @Test
    void testGetUserPrincipal_WithValidPermissions_ShouldUseThem() {
        // 准备用户信息
        XcUserExt xcUser = new XcUserExt();
        xcUser.setId("u1");
        xcUser.setPassword("123456");

        // 准备权限数据
        List<XcMenu> menus = new ArrayList<>();
        menus.add(createMenu("user:read"));
        menus.add(createMenu("user:write"));

        when(xcMenuMapper.selectPermissionByUserId("u1")).thenReturn(menus);

        // 调用方法
        UserDetails userDetails = userService.getUserPrincipal(xcUser);

        // 验证权限
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("user:read")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("user:write")));
    }

    // ✅ 默认情况：没有权限数据
    @Test
    void testGetUserPrincipal_WithNoPermissions_ShouldUseDefault() {
        XcUserExt xcUser = new XcUserExt();
        xcUser.setId("u1");
        xcUser.setPassword("123456");

        when(xcMenuMapper.selectPermissionByUserId("u1")).thenReturn(new ArrayList<>());

        UserDetails userDetails = userService.getUserPrincipal(xcUser);

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("test")));
    }

    // ❌ 异常情况：xcUser 为 null
    @Test
    void testGetUserPrincipal_WithNullXcUser_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            userService.getUserPrincipal(null);
        });
    }

    // 辅助方法创建菜单对象
    private XcMenu createMenu(String code) {
        XcMenu menu = new XcMenu();
        menu.setCode(code);
        return menu;
    }
}