package main.java.com.example.studentmanagement.service;

import main.java.com.example.studentmanagement.model.Student;
import main.java.com.example.studentmanagement.model.StudentFilterCriteria;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Predicate;

public class StudentFilterService {

    private final double excellentGradeThreshold;

    public StudentFilterService() {
        this(4.5);
    }

    public StudentFilterService(double excellentGradeThreshold) {
        this.excellentGradeThreshold = excellentGradeThreshold;
    }

    public Predicate<Student> buildFilterPredicate(StudentFilterCriteria criteria) {
        return student -> {
            if (criteria == null) {
                return true;
            }

            if (criteria.isShowOnlyExcellentStudents() && !isExcellentStudent(student)) {
                return false;
            }

            if (criteria.isShowOnlyStudentsWithDebt() && !student.hasDebt()) {
                return false;
            }

            String fullNameContains = normalize(criteria.getFullNameContains());
            if (!fullNameContains.isEmpty()) {
                String normalizedFullName = normalize(student.getFullName());
                if (!normalizedFullName.contains(fullNameContains)) {
                    return false;
                }
            }

            String groupNameContains = normalize(criteria.getGroupNameContains());
            if (!groupNameContains.isEmpty()) {
                String normalizedGroupName = normalize(student.getGroupName());
                if (!normalizedGroupName.contains(groupNameContains)) {
                    return false;
                }
            }

            Integer courseEquals = criteria.getCourseEquals();
            if (courseEquals != null && courseEquals > 0) {
                if (student.getCourse() != courseEquals) {
                    return false;
                }
            }

            return true;
        };
    }

    public boolean isExcellentStudent(Student student) {
        return !student.hasDebt()
                && student.getAverageGrade() >= excellentGradeThreshold;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
