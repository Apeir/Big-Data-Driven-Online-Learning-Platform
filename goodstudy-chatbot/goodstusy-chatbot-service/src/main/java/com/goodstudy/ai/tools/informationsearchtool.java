package com.goodstudy.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Learning Platform Information Search Tool
 */
@Slf4j
public class InformationSearchTool {

    @Tool(name = "studentInfoSearch", value = """
            Search for student information. Input student ID or name, returns basic info and learning progress.
            Use when the user asks about a student's status.
            """
    )
    public String searchStudentInfo(@P(value = "Student ID or Name") String studentId) {
        // Simulate actual data retrieval
        return "Student Info:\nName: " + studentId + "\nProgress: 80%\nRecent Course: Java Basics";
    }

    @Tool(name = "courseInfoSearch", value = """
            Search for course information. Input course name or ID, returns course introduction, teacher, and progress.
            Use when the user asks about a course.
            """
    )
    public String searchCourseInfo(@P(value = "Course Name or ID") String courseName) {
        // Simulate actual data retrieval
        return "Course Info:\nCourse: " + courseName + "\nTeacher: Mr. Zhang\nProgress: 60%";
    }

    @Tool(name = "platformAnnouncementSearch", value = """
            Search for the latest platform announcements. Use when the user asks about platform notifications.
            """
    )
    public String searchPlatformAnnouncement() {
        // Simulate actual data retrieval
        return "Platform Announcement: System maintenance this Saturday. Please save your progress in advance.";
    }

    @Tool(name = "studentScoreSearch", value = """
            Search for student scores. Input student ID or name, returns recent exam scores and ranking.
            Use when the user asks about a student's grades.
            """
    )
    public String searchStudentScore(@P(value = "Student ID or Name") String studentId) {
        // Simulate actual data retrieval
        return "Score Info:\nName: " + studentId + "\nLatest Exam: 92\nClass Ranking: 5";
    }

    @Tool(name = "courseScheduleSearch", value = """
            Search for course schedule. Input course name or ID, returns upcoming class times and locations.
            Use when the user asks about course schedule.
            """
    )
    public String searchCourseSchedule(@P(value = "Course Name or ID") String courseName) {
        // Simulate actual data retrieval
        return "Course Schedule:\nCourse: " + courseName + "\nNext Class: " + LocalDate.now().plusDays(2) + " 14:00\nLocation: Room 301";
    }

    @Tool(name = "teacherInfoSearch", value = """
            Search for teacher information. Input teacher name or ID, returns teacher profile and courses taught.
            Use when the user asks about a teacher.
            """
    )
    public String searchTeacherInfo(@P(value = "Teacher Name or ID") String teacherName) {
        // Simulate actual data retrieval
        List<String> courses = Arrays.asList("Java Basics", "Spring Boot Advanced");
        return "Teacher Info:\nName: " + teacherName + "\nCourses: " + String.join(", ", courses);
    }