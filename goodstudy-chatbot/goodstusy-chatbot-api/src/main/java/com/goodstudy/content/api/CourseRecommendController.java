package com.goodstudy.content.api;

import com.goodstudy.dto.CourseRecommendRequestDTO;
import com.goodstudy.dto.CourseRecommendResponseDTO;
import com.goodstudy.service.CourseRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
public class CourseRecommendController {

    @Autowired
    private CourseRecommendService courseRecommendService;

    @PostMapping("/courses")
    public CourseRecommendResponseDTO recommendCourses(@RequestBody CourseRecommendRequestDTO request) {
        return courseRecommendService.recommendCourses(request);
    }
}