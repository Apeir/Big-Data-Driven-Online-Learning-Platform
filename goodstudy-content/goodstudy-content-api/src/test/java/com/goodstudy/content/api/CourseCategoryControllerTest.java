package com.goodstudy.content.api;

import com.goodstudy.content.model.dto.CourseCategoryTreeDto;
import com.goodstudy.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseCategoryController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false) // ⛔ 禁用Security过滤器
public class CourseCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseCategoryService courseCategoryService;

    @Test
    void testQueryTreeNodes() throws Exception {

        CourseCategoryTreeDto mockNode = new CourseCategoryTreeDto();
        mockNode.setId("1");
        mockNode.setName("Test Category");
        List<CourseCategoryTreeDto> mockList = Collections.singletonList(mockNode);

        // mock service
        Mockito.when(courseCategoryService.queryTreeNodes(anyString())).thenReturn(mockList);

        // execute request
        mockMvc.perform(get("/course-category/tree-nodes")
                        .param("id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }
}
