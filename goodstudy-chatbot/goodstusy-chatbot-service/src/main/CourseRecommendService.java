package com.goodstudy.service;

import com.goodstudy.dto.CourseRecommendRequestDTO;
import com.goodstudy.dto.CourseRecommendResponseDTO;
import com.goodstudy.po.CoursePO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseRecommendService {

    // 模拟所有课程
    private static final List<CoursePO> ALL_COURSES = Arrays.asList(
            new CoursePO() {{ setCourseId("1"); setCourseName("Java Basics"); }},
            new CoursePO() {{ setCourseId("2"); setCourseName("Spring Boot Advanced"); }},
            new CoursePO() {{ setCourseId("3"); setCourseName("Data Structures"); }},
            new CoursePO() {{ setCourseId("4"); setCourseName("Machine Learning"); }}
    );

    // 模拟学生已学课程
    private List<String> getLearnedCourses(String studentId) {
        if ("studentA".equals(studentId)) {
            return Arrays.asList("Java Basics", "Data Structures");
        }
        return Arrays.asList("Java Basics");
    }

    // 推荐算法：推荐未学过的课程
    public CourseRecommendResponseDTO recommendCourses(CourseRecommendRequestDTO request) {
        List<String> learned = getLearnedCourses(request.getStudentId());
        List<String> recommended = ALL_COURSES.stream()
                .map(CoursePO::getCourseName)
                .filter(course -> !learned.contains(course))
                .collect(Collectors.toList());
        CourseRecommendResponseDTO response = new CourseRecommendResponseDTO();
        response.setRecommendedCourses(recommended);
        return response;
    }
}