package main.java.com.example.studentmanagement.model;

public class StudentStatistics {

    private final long totalStudents;
    private final long excellentStudents;
    private final long studentsWithDebt;
    private final double averageGrade;

    public StudentStatistics(
            long totalStudents,
            long excellentStudents,
            long studentsWithDebt,
            double averageGrade
    ) {
        this.totalStudents = totalStudents;
        this.excellentStudents = excellentStudents;
        this.studentsWithDebt = studentsWithDebt;
        this.averageGrade = averageGrade;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public long getExcellentStudents() {
        return excellentStudents;
    }

    public long getStudentsWithDebt() {
        return studentsWithDebt;
    }

    public double getAverageGrade() {
        return averageGrade;
    }
}