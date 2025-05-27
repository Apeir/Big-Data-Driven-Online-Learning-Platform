package com.goodstudy.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.goodstudy.ucenter.mapper.XcUserMapper;
import com.goodstudy.ucenter.mapper.XcUserRoleMapper;
import com.goodstudy.ucenter.model.dto.AuthParamsDto;
import com.goodstudy.ucenter.model.dto.XcUserExt;
import com.goodstudy.ucenter.model.po.XcUser;
import com.goodstudy.ucenter.model.po.XcUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WxAuthServiceImplTest {

    @InjectMocks
    private WxAuthServiceImpl wxAuthService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private XcUserMapper xcUserMapper;

    @Mock
    private XcUserRoleMapper xcUserRoleMapper;

    @BeforeEach
    void setUp() {
        //MockitoAnnotations.openMocks(this);
        // 设置当前代理为自己（用于测试 addWxUser 的事务调用）
        wxAuthService.currentPorxy = wxAuthService;
    }

    // 1. 测试 getAccess_token 成功
    @Test
    void testGetAccess_token_Success_ReturnsTokenMap() {
        String mockResponse = "{\"access_token\":\"mock_token\",\"expires_in\":7200,\"openid\":\"mock_openid\"}";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        Map<String, String> result = wxAuthService.getAccess_token("valid_code");

        assertNotNull(result);
        assertEquals("mock_token", result.get("access_token"));
        assertEquals("mock_openid", result.get("openid"));
    }

    // 2. 测试 getAccess_token 失败
    //@Test
    //void testGetAccess_token_InvalidCode_ThrowsException() {
    //    String mockErrorResponse = "{\"errcode\":40029,\"errmsg\":\"invalid code\"}";
    //    when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
    //            .thenReturn(ResponseEntity.ok(mockErrorResponse));
    //
    //    assertThrows(RuntimeException.class, () -> {
    //        wxAuthService.getAccess_token("invalid_code");
    //    });
    //}

    // 3. 测试 addWxUser 新增用户
    @Test
    void testAddWxUser_NewUser_CreatesInDatabase() {
        Map<String, String> userInfo = new HashMap<>();
        String unionid = UUID.randomUUID().toString();
        String nickname = "张三";
        userInfo.put("unionid", unionid);
        userInfo.put("nickname", nickname);

        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(xcUserMapper.insert(any(XcUser.class))).thenReturn(1);
        when(xcUserRoleMapper.insert(any(XcUserRole.class))).thenReturn(1);

        XcUser result = wxAuthService.addWxUser(userInfo);

        assertNotNull(result);
        assertEquals(unionid, result.getUsername());
        assertEquals(nickname, result.getNickname());
        verify(xcUserMapper, times(1)).insert(any(XcUser.class));
        verify(xcUserRoleMapper, times(1)).insert(any(XcUserRole.class));
    }

    // 4. 测试 addWxUser 已有用户
    @Test
    void testAddWxUser_ExistingUser_ReturnsExisting() {
        String unionid = UUID.randomUUID().toString();
        XcUser existingUser = new XcUser();
        existingUser.setWxUnionid(unionid);
        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("unionid", unionid);

        XcUser result = wxAuthService.addWxUser(userInfo);

        assertNotNull(result);
        assertEquals(existingUser, result);
        verify(xcUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(xcUserMapper, never()).insert(any(XcUser.class));
    }

    // 5. 测试 execute 方法 - 用户存在
    @Test
    void testExecute_UserExists_ReturnsXcUserExt() {
        String username = "user123";
        AuthParamsDto dto = new AuthParamsDto();
        dto.setUsername(username);

        XcUser user = new XcUser();
        user.setUsername(username);
        user.setPassword("123456");
        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        XcUserExt result = wxAuthService.execute(dto);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    // 6. 测试 execute 方法 - 用户不存在
    @Test
    void testExecute_UserNotExists_ThrowsException() {
        AuthParamsDto dto = new AuthParamsDto();
        dto.setUsername("notexist");

        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            wxAuthService.execute(dto);
        });
    }

    @Test
    void testGetUserinfo_Success_ReturnsUserInfoMap() {
        String mockResponse = "{\"openid\":\"mock_openid\",\"nickname\":\"张三\",\"unionid\":\"mock_unionid\"}";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(new String(mockResponse.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)));

        Map<String, String> result = wxAuthService.getUserinfo("mock_token", "mock_openid");

        assertNotNull(result);
        assertEquals("mock_openid", result.get("openid"));
        assertEquals("张三", result.get("nickname"));
        assertEquals("mock_unionid", result.get("unionid"));
    }

    //@Test
    //void testGetUserinfo_WithError_ReturnsErrorMap() {
    //    String mockErrorResponse = "{\"errcode\":40003,\"errmsg\":\"invalid openid\"}";
    //    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
    //            .thenReturn(ResponseEntity.ok(mockErrorResponse));
    //
    //    Map<String, String> result = wxAuthService.getUserinfo("mock_token", "invalid_openid");
    //
    //    assertNotNull(result);
    //    assertTrue(result.containsKey("errcode"));
    //    assertEquals("40003", result.get("errcode"));
    //}

    //@Test
    //void testWxAuth_NewUser_CreatesAndReturnsUser() {
    //    // 模拟 getAccess_token 返回
    //    Map<String, String> tokenMap = new HashMap<>();
    //    tokenMap.put("access_token", "mock_token");
    //    tokenMap.put("openid", "mock_openid");
    //
    //    when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
    //            .thenReturn(ResponseEntity.ok(JSON.toJSONString(tokenMap)));
    //
    //    // 模拟 getUserinfo 返回
    //    Map<String, String> userInfo = new HashMap<>();
    //    userInfo.put("unionid", "u123456");
    //    userInfo.put("nickname", "李四");
    //
    //    // 使用 toString() 替代 contains()
    //    when(restTemplate.exchange(
    //            argThat(uri -> uri.toString().contains("sns/userinfo")),
    //            eq(HttpMethod.GET),
    //            any(),
    //            eq(String.class)))
    //            .thenReturn(ResponseEntity.ok(JSON.toJSONString(userInfo)));
    //
    //    // 模拟用户不存在
    //    when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
    //
    //    // 模拟插入成功
    //    when(xcUserMapper.insert(any(XcUser.class))).thenReturn(1);
    //    when(xcUserRoleMapper.insert(any(XcUserRole.class))).thenReturn(1);
    //
    //    XcUser result = wxAuthService.wxAuth("valid_code");
    //
    //    assertNotNull(result);
    //    assertEquals("u123456", result.getWxUnionid());
    //    assertEquals("李四", result.getNickname());
    //}
    @Test
    void testWxAuth_ExistingUser_ReturnsExistingUser() {
        // 模拟 getAccess_token 返回
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access_token", "mock_token");
        tokenMap.put("openid", "mock_openid");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(JSON.toJSONString(tokenMap)));

        // 模拟 getUserinfo 返回
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("unionid", "u123456");
        userInfo.put("nickname", "王五");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(JSON.toJSONString(userInfo)));

        // 模拟用户已存在
        XcUser existingUser = new XcUser();
        existingUser.setWxUnionid("u123456");
        when(xcUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);

        XcUser result = wxAuthService.wxAuth("valid_code");

        assertNotNull(result);
        assertEquals(existingUser, result);
        verify(xcUserMapper, never()).insert(any(XcUser.class));
    }
}