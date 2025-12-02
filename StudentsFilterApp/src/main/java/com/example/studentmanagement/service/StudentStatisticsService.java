package main.java.com.example.studentmanagement.service;

import main.java.com.example.studentmanagement.model.Student;
import main.java.com.example.studentmanagement.model.StudentStatistics;

import java.util.List;

public class StudentStatisticsService {

    private final StudentFilterService studentFilterService;

    public StudentStatisticsService(StudentFilterService studentFilterService) {
        this.studentFilterService = studentFilterService;
    }

    public StudentStatistics calculateStatistics(List<Student> students) {
        long total = students.size();
        long excellent = students.stream()
                .filter(studentFilterService::isExcellentStudent)
                .count();
        long withDebt = students.stream()
                .filter(Student::hasDebt)
                .count();

        double averageGrade = students.stream()
                .mapToDouble(Student::getAverageGrade)
                .average()
                .orElse(0.0);

        return new StudentStatistics(total, excellent, withDebt, averageGrade);
    }
}