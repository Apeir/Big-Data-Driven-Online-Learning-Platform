package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CoursePublishServiceImplTest {

    @InjectMocks
    private CoursePublishServiceImpl coursePublishService;

    @Mock private CourseBaseInfoService courseBaseInfoService;
    @Mock private TeachplanService teachplanService;
    @Mock private CourseBaseMapper courseBaseMapper;
    @Mock private CourseMarketMapper courseMarketMapper;
    @Mock private CoursePublishPreMapper coursePublishPreMapper;
    @Mock private CoursePublishMapper coursePublishMapper;
    @Mock private MqMessageService mqMessageService;
    @Mock private MediaServiceClient mediaServiceClient;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private RedissonClient redissonClient;

    @Mock private ValueOperations<String, Object> valueOperations;
    @Mock private RLock rLock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetCoursePreviewInfo() {
        Long courseId = 1L;
        CourseBaseInfoDto baseInfoDto = new CourseBaseInfoDto();
        List<TeachplanDto> planList = Collections.singletonList(new TeachplanDto());

        when(courseBaseInfoService.getCourseBaseInfo(courseId)).thenReturn(baseInfoDto);
        when(teachplanService.findTeachplanTree(courseId)).thenReturn(planList);

        CoursePreviewDto result = coursePublishService.getCoursePreviewInfo(courseId);

        assertNotNull(result);
        assertEquals(baseInfoDto, result.getCourseBase());
        assertEquals(1, result.getTeachplans().size());
    }

    @Test
    void testCommitAuditSuccess() {
        Long courseId = 1L;
        Long companyId = 123L;
        CourseBaseInfoDto baseInfoDto = new CourseBaseInfoDto();
        baseInfoDto.setAuditStatus("202002");
        baseInfoDto.setPic("pic.jpg");

        when(courseBaseInfoService.getCourseBaseInfo(courseId)).thenReturn(baseInfoDto);
        when(teachplanService.findTeachplanTree(courseId)).thenReturn(Collections.singletonList(new TeachplanDto()));
        when(courseMarketMapper.selectById(courseId)).thenReturn(new CourseMarket());
        when(coursePublishPreMapper.selectById(courseId)).thenReturn(null);
        when(courseBaseMapper.selectById(courseId)).thenReturn(new CourseBase());

        coursePublishService.commitAudit(companyId, courseId);

        verify(coursePublishPreMapper, times(1)).insert(any());
        verify(courseBaseMapper, times(1)).updateById(any());
    }

    @Test
    void testPublishCourse() {
        Long courseId = 1L;
        Long companyId = 123L;
        CoursePublishPre pre = new CoursePublishPre();
        pre.setStatus("202004");

        when(coursePublishPreMapper.selectById(courseId)).thenReturn(pre);
        when(coursePublishMapper.selectById(courseId)).thenReturn(null);
        when(mqMessageService.addMessage(any(), any(), any(), any())).thenReturn(new MqMessage());

        coursePublishService.publish(companyId, courseId);

        verify(coursePublishMapper).insert(any());
        verify(coursePublishPreMapper).deleteById(courseId);
    }

    @Test
    void testGetCoursePublishCache_HitCache() {
        Long courseId = 1L;
        CoursePublish expected = new CoursePublish();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("course:" + courseId)).thenReturn(JSON.toJSONString(expected));

        CoursePublish result = coursePublishService.getCoursePublishCache(courseId);
        assertNotNull(result);
    }

    @Test
    void testGetCoursePublishCache_MissCache() {
        Long courseId = 1L;
        CoursePublish expected = new CoursePublish();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("course:" + courseId)).thenReturn(null);
        when(redissonClient.getLock(any())).thenReturn(rLock);
        when(coursePublishMapper.selectById(courseId)).thenReturn(expected);

        doNothing().when(rLock).lock();
        doNothing().when(rLock).unlock();

        CoursePublish result = coursePublishService.getCoursePublishCache(courseId);
        assertNotNull(result);
    }
}
