package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourseBaseInfoServiceImplTest {

    @InjectMocks
    private CourseBaseInfoServiceImpl courseBaseInfoService;

    @Mock
    private CourseBaseMapper courseBaseMapper;

    @Mock
    private CourseMarketMapper courseMarketMapper;

    @Mock
    private CourseCategoryMapper courseCategoryMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryCourseBaseList() {
        PageParams pageParams = new PageParams(1L, 10L);
        QueryCourseParamsDto query = new QueryCourseParamsDto();
        query.setCourseName("Java");

        Page<CourseBase> mockPage = new Page<>();
        CourseBase base = new CourseBase();
        base.setName("Java基础");
        mockPage.setRecords(Collections.singletonList(base));
        mockPage.setTotal(1);

        when(courseBaseMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        assertEquals(1, courseBaseInfoService.queryCourseBaseList(123L, pageParams, query).getCounts());
    }

    @Test
    public void testCreateCourseBaseSuccess() {
        AddCourseDto dto = new AddCourseDto();
        dto.setName("新课程");
        dto.setMt("1");
        dto.setSt("1-1");
        dto.setGrade("初级");
        dto.setTeachmode("200002");
        dto.setUsers("程序员");
        dto.setCharge("201001");
        dto.setPrice(100f);

        final CourseBase savedBase = new CourseBase();
        savedBase.setId(1L);
        savedBase.setMt("1");
        savedBase.setSt("1-1");

        CourseCategory mt = new CourseCategory();
        mt.setName("开发");

        CourseCategory st = new CourseCategory();
        st.setName("后端");

        when(courseBaseMapper.insert(any(CourseBase.class))).thenAnswer(invocation -> {
            CourseBase cb = invocation.getArgument(0);
            cb.setId(1L); // 模拟数据库生成ID
            return 1;
        });

        when(courseMarketMapper.selectById(1L)).thenReturn(null);
        when(courseMarketMapper.insert(any(CourseMarket.class))).thenReturn(1);
        when(courseBaseMapper.selectById(1L)).thenReturn(savedBase);
        when(courseCategoryMapper.selectById("1")).thenReturn(mt);
        when(courseCategoryMapper.selectById("1-1")).thenReturn(st);

        CourseBaseInfoDto result = courseBaseInfoService.createCourseBase(123L, dto);
        assertNotNull(result);
        assertEquals("开发", result.getMtName());
        assertEquals("后端", result.getStName());
    }

    @Test
    public void testCreateCourseBase_throwsOnMissingPrice() {
        AddCourseDto dto = new AddCourseDto();
        dto.setName("免费课程");
        dto.setMt("1");
        dto.setSt("1-1");
        dto.setGrade("初级");
        dto.setTeachmode("200002");
        dto.setUsers("学生");
        dto.setCharge("201001"); // 表示收费
        dto.setPrice(0f); // 不合法价格

        // 修复点：Mock insert 成功，避免进入错误分支
        when(courseBaseMapper.insert(any(CourseBase.class))).thenReturn(1);

        RuntimeException exception = assertThrows(RuntimeException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() throws Throwable {
                courseBaseInfoService.createCourseBase(123L, dto);
            }
        });

        assertEquals("课程的价格不能为空并且必须大于0", exception.getMessage());
    }
}
