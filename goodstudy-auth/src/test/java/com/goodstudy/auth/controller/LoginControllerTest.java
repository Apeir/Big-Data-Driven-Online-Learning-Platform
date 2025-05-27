package com.goodstudy.auth.controller;

import com.goodstudy.auth.config.DaoAuthenticationProviderCustom;
import com.goodstudy.ucenter.mapper.XcUserMapper;
import com.goodstudy.ucenter.model.po.XcUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginController.class) // 只加载 LoginController
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private XcUserMapper userMapper; // 模拟 Mapper

    private XcUser testUser;

    @MockBean
    private DaoAuthenticationProviderCustom daoAuthenticationProviderCustom;

    @BeforeEach
    public void setUp() {
        testUser = new XcUser();
        testUser.setId("123");
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setCreateTime(LocalDateTime.now());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        mockMvc.perform(get("/login-success"))
                .andExpect(status().isOk())
                .andExpect(content().string("登录成功"));
    }

    @Test
    public void testGetUser_ReturnsUser() throws Exception {
        when(userMapper.selectById("123")).thenReturn(testUser);

        mockMvc.perform(get("/user/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    //@Test
    //public void testGetUser_UserNotFound() throws Exception {
    //    when(userMapper.selectById("9999")).thenReturn(null);
    //
    //    mockMvc.perform(get("/user/9999"))
    //            .andExpect(status().isNotFound()); // 假设返回404
    //}
    //
    //@Test
    //public void testR1Access() throws Exception {
    //    mockMvc.perform(get("/r/r1"))
    //            .andExpect(status().isOk())
    //            .andExpect(content().string("访问r1资源"));
    //}
    //
    //@Test
    //public void testR2Access() throws Exception {
    //    mockMvc.perform(get("/r/r2"))
    //            .andExpect(status().isOk())
    //            .andExpect(content().string("访问r2资源"));
    //}

    // 拥有 p1 权限的用户可以访问 /r/r1
    @WithMockUser(username = "user1", authorities = {"p1"})
    @Test
    public void testR1Access_WithP1() throws Exception {
        mockMvc.perform(get("/r/r1"))
                .andExpect(status().isOk())
                .andExpect(content().string("访问r1资源"));
    }

    // 没有 p1 权限的用户不能访问 /r/r1
    //@WithMockUser(username = "user2", authorities = {"p2"})
    //@Test
    //public void testR1Access_WithoutP1() throws Exception {
    //    mockMvc.perform(get("/r/r1"))
    //            .andExpect(status().isForbidden());
    //}

    // 拥有 p2 权限的用户可以访问 /r/r2
    @WithMockUser(username = "user3", authorities = {"p2"})
    @Test
    public void testR2Access_WithP2() throws Exception {
        mockMvc.perform(get("/r/r2"))
                .andExpect(status().isOk())
                .andExpect(content().string("访问r2资源"));
    }

    //// 没有 p2 权限的用户不能访问 /r/r2
    //@WithMockUser(username = "user4", authorities = {"p1"})
    //@Test
    //public void testR2Access_WithoutP2() throws Exception {
    //    mockMvc.perform(get("/r/r2"))
    //            .andExpect(status().isForbidden());
    //}
}
