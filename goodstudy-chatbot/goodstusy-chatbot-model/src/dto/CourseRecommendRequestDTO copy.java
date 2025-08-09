package com.goodstudy.dto;

import java.util.List;

public class CourseRecommendResponseDTO {
    private List<String> recommendedCourses;

    public List<String> getRecommendedCourses() { return recommendedCourses; }
    public void setRecommendedCourses(List<String> recommendedCourses) { this.recommendedCourses = recommendedCourses; }
}