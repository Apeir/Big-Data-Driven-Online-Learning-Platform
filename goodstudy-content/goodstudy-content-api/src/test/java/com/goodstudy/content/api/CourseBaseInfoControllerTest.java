package com.goodstudy.content.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodstudy.content.model.dto.CourseBaseInfoDto;
import com.goodstudy.content.model.dto.EditCourseDto;
import com.goodstudy.content.model.dto.QueryCourseParamsDto;
import com.goodstudy.content.service.CourseBaseInfoService;
import com.goodstudy.content.service.CoursePublishService;
import com.goodstudy.content.service.TeachplanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 覆盖 CourseBaseInfoController 所有接口，目标 100% 方法覆盖率
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CourseBaseInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseBaseInfoService courseBaseInfoService;

    @MockBean
    private CoursePublishService coursePublishService;

    @MockBean
    private TeachplanService teachplanService; //

    /**
     * 测试 1：查询课程
     */
    @Test
    public void testGetCourseBaseById() throws Exception {
        Long courseId = 40L;
        CourseBaseInfoDto mockDto = new CourseBaseInfoDto();
        mockDto.setId(courseId);
        mockDto.setName("测试课程");
        when(courseBaseInfoService.getCourseBaseInfo(courseId)).thenReturn(mockDto);

        mockMvc.perform(get("/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId));
    }

//    /**
//     * 测试 2：新增课程
//     */
//    @Test
//    public void testCreateCourse() throws Exception {
//        AddCourseDto dto = new AddCourseDto();
//        dto.setName("测试课程100");
//        dto.setMt("1-1");
//        dto.setSt("1-1-1");
//        dto.setGrade("200002");
//        dto.setTeachmode("200002");
//        dto.setUsers("测试用户群体");
//        dto.setCharge("201001");
//        dto.setPrice(88.8f);
//        dto.setOriginalPrice(188.8f);
//        dto.setQq("123456");
//
//        CourseBaseInfoDto mockDto = new CourseBaseInfoDto();
//        mockDto.setName(dto.getName());
//
//        final Long mockCompanyId = 123214124L;
//        when(courseBaseInfoService.createCourseBase(eq(mockCompanyId), any(AddCourseDto.class)))
//                .thenReturn(mockDto);
//
//        mockMvc.perform(post("/course")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("测试课程100"));
//    }

    /**
     * 测试 3：修改课程
     */
    @Test
    public void testModifyCourse() throws Exception {
        EditCourseDto dto = new EditCourseDto();
        dto.setId(40L);
        dto.setName("修改后的课程");
        dto.setMt("1-1");
        dto.setSt("1-1-1");
        dto.setGrade("200002");
        dto.setTeachmode("200002");
        dto.setUsers("测试用户群体");
        dto.setCharge("201001");
        dto.setPrice(99.9f);
        dto.setOriginalPrice(199.9f);
        dto.setQq("123456");

        CourseBaseInfoDto mockDto = new CourseBaseInfoDto();
        mockDto.setId(40L);
        mockDto.setName("修改后的课程");

        when(courseBaseInfoService.updateCourseBase(anyLong(), any(EditCourseDto.class)))
                .thenReturn(mockDto); // 注意这里
        mockMvc.perform(put("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("修改后的课程"));
    }

    /**
     * 测试 4：分页查询课程列表
     */
    @Test
    public void testListCourses() throws Exception {
        QueryCourseParamsDto paramsDto = new QueryCourseParamsDto();
        paramsDto.setCourseName("");
        paramsDto.setAuditStatus("");
        paramsDto.setPublishStatus("");

        // 这里不需要返回完整分页数据结构，只要确保不报错
        when(courseBaseInfoService.queryCourseBaseList(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new com.goodstudy.base.model.PageResult<>(Collections.emptyList(), 0, 1, 5));

        mockMvc.perform(post("/course/list")
                        .param("pageNo", "1")
                        .param("pageSize", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").exists());
    }
}
