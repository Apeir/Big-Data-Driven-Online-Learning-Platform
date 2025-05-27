package com.goodstudy.content.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FreemarkerController.class)
@AutoConfigureMockMvc(addFilters = false) // 关闭 Security 过滤器链
public class FreemarkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFreemarkerEndpoint() throws Exception {
        mockMvc.perform(get("/testfreemarker"))
                .andExpect(status().isOk())
                .andExpect(view().name("test"))
                .andExpect(model().attribute("name", is("小明")));
    }
}
