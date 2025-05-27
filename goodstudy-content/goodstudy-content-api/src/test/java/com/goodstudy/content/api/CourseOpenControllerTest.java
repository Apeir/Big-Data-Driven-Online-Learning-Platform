package com.goodstudy.content.api;

import com.goodstudy.content.model.dto.CoursePreviewDto;
import com.goodstudy.content.service.CourseBaseInfoService;
import com.goodstudy.content.service.CoursePublishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseOpenController.class)
@AutoConfigureMockMvc(addFilters = false) // 关闭 Security 过滤器链
public class CourseOpenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseBaseInfoService courseBaseInfoService;

    @MockBean
    private CoursePublishService coursePublishService;

    @BeforeEach
    public void setup() {
        // mock 返回一个假的 CoursePreviewDto，避免 NullPointerException
        CoursePreviewDto mockPreviewDto = new CoursePreviewDto();
        when(coursePublishService.getCoursePreviewInfo(anyLong())).thenReturn(mockPreviewDto);
    }

    @Test
    public void testGetPreviewInfo() throws Exception {
        mockMvc.perform(get("/open/course/whole/123"))
                .andExpect(status().isOk());
    }
}
