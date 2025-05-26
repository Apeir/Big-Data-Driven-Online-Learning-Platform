package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CourseCategoryServiceImplTest {

    @InjectMocks
    private CourseCategoryServiceImpl courseCategoryService;

    @Mock
    private CourseCategoryMapper courseCategoryMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testQueryTreeNodes_returnChildrenCorrectly() {
        // 模拟树形结构
        CourseCategoryTreeDto root = new CourseCategoryTreeDto();
        root.setId("1");
        root.setParentid("0");
        root.setName("根节点");

        CourseCategoryTreeDto child1 = new CourseCategoryTreeDto();
        child1.setId("1-1");
        child1.setParentid("1");
        child1.setName("子节点1");

        CourseCategoryTreeDto child2 = new CourseCategoryTreeDto();
        child2.setId("1-2");
        child2.setParentid("1");
        child2.setName("子节点2");

        CourseCategoryTreeDto grandchild = new CourseCategoryTreeDto();
        grandchild.setId("1-1-1");
        grandchild.setParentid("1-1");
        grandchild.setName("孙节点");

        List<CourseCategoryTreeDto> mockList = Arrays.asList(root, child1, child2, grandchild);

        when(courseCategoryMapper.selectTreeNodes("1")).thenReturn(mockList);

        List<CourseCategoryTreeDto> result = courseCategoryService.queryTreeNodes("1");

        assertEquals(2, result.size()); // 1-1 和 1-2
        assertEquals("1-1", result.get(0).getId());
        assertEquals("1-2", result.get(1).getId());

        // 校验子节点绑定正确
        assertNotNull(result.get(0).getChildrenTreeNodes());
        assertEquals(1, result.get(0).getChildrenTreeNodes().size());
        assertEquals("1-1-1", result.get(0).getChildrenTreeNodes().get(0).getId());
    }

    @Test
    void testQueryTreeNodes_emptyResult() {
        when(courseCategoryMapper.selectTreeNodes("999")).thenReturn(Collections.emptyList());

        List<CourseCategoryTreeDto> result = courseCategoryService.queryTreeNodes("999");

        assertTrue(result.isEmpty());
    }
}
