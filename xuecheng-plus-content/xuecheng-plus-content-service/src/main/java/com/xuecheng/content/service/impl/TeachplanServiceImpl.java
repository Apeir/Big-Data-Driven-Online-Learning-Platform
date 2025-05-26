package com.xuecheng.content.service.impl;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeachplanServiceImplTest {

    @InjectMocks
    private TeachplanServiceImpl teachplanService;

    @Mock
    private TeachplanMapper teachplanMapper;

    @Mock
    private TeachplanMediaMapper teachplanMediaMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindTeachplanTree() {
        Long courseId = 1L;
        TeachplanDto dto = new TeachplanDto();
        when(teachplanMapper.selectTreeNodes(courseId)).thenReturn(Collections.singletonList(dto));

        List<TeachplanDto> result = teachplanService.findTeachplanTree(courseId);
        assertEquals(1, result.size());
        verify(teachplanMapper, times(1)).selectTreeNodes(courseId);
    }

    @Test
    void testSaveTeachplan_New() {
        SaveTeachplanDto dto = new SaveTeachplanDto();
        dto.setCourseId(1L);
        dto.setParentid(0L);
        dto.setId(null);

        when(teachplanMapper.selectCount(any())).thenReturn(2);

        teachplanService.saveTeachplan(dto);
        verify(teachplanMapper).insert(any(Teachplan.class));
    }

    @Test
    void testSaveTeachplan_Update() {
        SaveTeachplanDto dto = new SaveTeachplanDto();
        dto.setId(1L);
        dto.setName("Updated Name");

        Teachplan teachplan = new Teachplan();
        teachplan.setId(1L);

        when(teachplanMapper.selectById(1L)).thenReturn(teachplan);

        teachplanService.saveTeachplan(dto);
        verify(teachplanMapper).updateById(any(Teachplan.class));
    }

    @Test
    void testAssociationMedia_Success() {
        BindTeachplanMediaDto dto = new BindTeachplanMediaDto();
        dto.setTeachplanId(100L);
        dto.setMediaId("media123");
        dto.setFileName("file.mp4");

        Teachplan teachplan = new Teachplan();
        teachplan.setId(100L);
        teachplan.setCourseId(10L);

        when(teachplanMapper.selectById(100L)).thenReturn(teachplan);

        teachplanService.associationMedia(dto);

        verify(teachplanMediaMapper).delete(any());
        verify(teachplanMediaMapper).insert(any(TeachplanMedia.class));
    }

    @Test
    void testAssociationMedia_TeachplanNotExist() {
        BindTeachplanMediaDto dto = new BindTeachplanMediaDto();
        dto.setTeachplanId(999L);

        when(teachplanMapper.selectById(999L)).thenReturn(null);

        assertThrows(XueChengPlusException.class, () -> teachplanService.associationMedia(dto));
        verify(teachplanMediaMapper, never()).insert(any());
    }
}
