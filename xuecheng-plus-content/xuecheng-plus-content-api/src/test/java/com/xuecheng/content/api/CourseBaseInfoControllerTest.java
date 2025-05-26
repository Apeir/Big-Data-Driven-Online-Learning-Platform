package com.xuecheng.content.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    /**
     * 测试 1：查询课程
     */
    @Test
    public void testGetCourseBaseById() throws Exception {
        Long courseId = 40L; // 确保这个 ID 在数据库中存在
        mockMvc.perform(get("/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId));
    }

    /**
     * 测试 2：新增课程
     */
    @Test
    public void testCreateCourse() throws Exception {
        AddCourseDto dto = new AddCourseDto();
        dto.setName("测试课程100");
        dto.setMt("1-1");
        dto.setSt("1-1-1");
        dto.setGrade("200002");
        dto.setTeachmode("200002");
        dto.setUsers("测试用户群体");
        dto.setCharge("201001");
        dto.setPrice(88.8f);
        dto.setOriginalPrice(188.8f);
        dto.setQq("123456");

        mockMvc.perform(post("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试课程100"));
    }

    /**
     * 测试 3：修改课程
     */
    @Test
    public void testModifyCourse() throws Exception {
        EditCourseDto dto = new EditCourseDto();
        dto.setId(40L); // 确保这个 ID 在数据库中存在
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
        paramsDto.setCourseName("");  // 可根据你的需求设置查询条件
        paramsDto.setAuditStatus("");
        paramsDto.setPublishStatus("");

        mockMvc.perform(post("/course/list")
                        .param("pageNo", "1")
                        .param("pageSize", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").exists());  // 确认返回分页结构
    }
}
