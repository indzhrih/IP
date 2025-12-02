package main.java.com.example.studentmanagement.model;

import java.util.Objects;

public class Student {

    private long sequenceNumber;
    private String gradeBookNumber;
    private String fullName;
    private int course;
    private String groupName;
    private String specialization;
    private double averageGrade;
    private int debtCount;

    public Student() {
    }

    public Student(
            long sequenceNumber,
            String gradeBookNumber,
            String fullName,
            int course,
            String groupName,
            String specialization,
            double averageGrade,
            int debtCount
    ) {
        this.sequenceNumber = sequenceNumber;
        this.gradeBookNumber = gradeBookNumber;
        this.fullName = fullName;
        this.course = course;
        this.groupName = groupName;
        this.specialization = specialization;
        this.averageGrade = averageGrade;
        this.debtCount = debtCount;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getGradeBookNumber() {
        return gradeBookNumber;
    }

    public void setGradeBookNumber(String gradeBookNumber) {
        this.gradeBookNumber = gradeBookNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public int getDebtCount() {
        return debtCount;
    }

    public void setDebtCount(int debtCount) {
        this.debtCount = debtCount;
    }

    public boolean hasDebt() {
        return debtCount > 0;
    }

    @Override
    public String toString() {
        return "Student{" +
                "sequenceNumber=" + sequenceNumber +
                ", gradeBookNumber='" + gradeBookNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", course=" + course +
                ", groupName='" + groupName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", averageGrade=" + averageGrade +
                ", debtCount=" + debtCount +
                '}';
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Student otherStudent)) {
            return false;
        }
        return sequenceNumber == otherStudent.sequenceNumber &&
                Objects.equals(gradeBookNumber, otherStudent.gradeBookNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceNumber, gradeBookNumber);
    }
}
