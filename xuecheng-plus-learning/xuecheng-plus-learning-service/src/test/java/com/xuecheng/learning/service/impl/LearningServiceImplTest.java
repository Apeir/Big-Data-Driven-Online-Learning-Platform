package com.xuecheng.learning.service.impl;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.MyCourseTablesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 使用 MockitoExtension 替代 openMocks
class LearningServiceImplTest {

    @InjectMocks
    private LearningServiceImpl learningService;

    @Mock
    private MyCourseTablesService myCourseTablesService;

    @Mock
    private ContentServiceClient contentServiceClient;

    @Mock
    private MediaServiceClient mediaServiceClient;

    // setUp 方法可以省略，因为 MockitoExtension 会自动初始化
    // 如果你需要额外的初始化逻辑，可以保留这个方法
    @BeforeEach
    void setUp() {
        // 这里可以添加其他初始化逻辑（如果需要）
    }

    @Test
    void testGetVideo_CourseNotFound() {
        // Arrange
        when(contentServiceClient.getCoursepublish(100L)).thenReturn(null);

        // Act
        RestResponse<String> response = learningService.getVideo(null, 100L, 1000L, "media123");

        // Assert
        assertNotNull(response);
        assertTrue(!response.isSuccessful());
        assertEquals("课程不存在", response.getMsg());
    }

    @Test
    void testGetVideo_FreeCourse_ReturnPlayUrl() {
        // Arrange
        CoursePublish coursePublish = new CoursePublish();
        coursePublish.setId(100L);
        coursePublish.setCharge("201000"); // 免费课程

        when(contentServiceClient.getCoursepublish(100L)).thenReturn(coursePublish);
        when(mediaServiceClient.getPlayUrlByMediaId("media123")).thenReturn(RestResponse.success("http://video.com/play"));

        // Act
        RestResponse<String> response = learningService.getVideo(null, 100L, 1000L, "media123");

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertEquals("http://video.com/play", response.getResult());
    }

    @Test
    void testGetVideo_PaidCourse_NotLoggedIn() {
        // Arrange
        CoursePublish coursePublish = new CoursePublish();
        coursePublish.setId(100L);
        coursePublish.setCharge("202000"); // 付费课程

        when(contentServiceClient.getCoursepublish(100L)).thenReturn(coursePublish);

        // Act
        RestResponse<String> response = learningService.getVideo(null, 100L, 1000L, "media123");

        // Assert
        assertNotNull(response);
        assertTrue(!response.isSuccessful());
        assertEquals("课程需要购买", response.getMsg());
    }

    @Test
    void testGetVideo_UserLoggedIn_NoEnrollment() {
        // Arrange
        XcCourseTablesDto dto = new XcCourseTablesDto();
        dto.setLearnStatus("702002"); // 没有选课或没有支付

        when(myCourseTablesService.getLearningStatus("user123", 100L)).thenReturn(dto);
        when(contentServiceClient.getCoursepublish(100L)).thenReturn(new CoursePublish());

        // Act
        RestResponse<String> response = learningService.getVideo("user123", 100L, 1000L, "media123");

        // Assert
        assertNotNull(response);
        assertTrue(!response.isSuccessful());
        assertEquals("无法学习，因为没有选课或选课后没有支付", response.getMsg());
    }

    @Test
    void testGetVideo_UserLoggedIn_Expired() {
        // Arrange
        XcCourseTablesDto dto = new XcCourseTablesDto();
        dto.setLearnStatus("702003"); // 已过期

        when(myCourseTablesService.getLearningStatus("user123", 100L)).thenReturn(dto);
        when(contentServiceClient.getCoursepublish(100L)).thenReturn(new CoursePublish());

        // Act
        RestResponse<String> response = learningService.getVideo("user123", 100L, 1000L, "media123");

        // Assert
        assertNotNull(response);
        assertTrue(!response.isSuccessful());
        assertEquals("已过期需要申请续期或重新支付", response.getMsg());
    }

    @Test
    void testGetVideo_UserLoggedIn_AllowedToLearn() {
        // Arrange
        XcCourseTablesDto dto = new XcCourseTablesDto();
        dto.setLearnStatus("702001"); // 可以正常学习

        CoursePublish coursePublish = new CoursePublish();
        coursePublish.setId(100L);
        coursePublish.setCharge("201000"); // 免费课程

        when(myCourseTablesService.getLearningStatus("user123", 100L)).thenReturn(dto);
        when(contentServiceClient.getCoursepublish(100L)).thenReturn(coursePublish);
        when(mediaServiceClient.getPlayUrlByMediaId("media123")).thenReturn(RestResponse.success("http://video.com/play"));

        // Act
        RestResponse<String> response = learningService.getVideo("user123", 100L, 1000L, "media123");

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertEquals("http://video.com/play", response.getResult());
    }
}