package main.java.com.example.studentmanagement.model;

public class StudentFilterCriteria {

    private boolean showOnlyExcellentStudents;
    private boolean showOnlyStudentsWithDebt;
    private String fullNameContains;
    private String groupNameContains;
    private Integer courseEquals;

    public boolean isShowOnlyExcellentStudents() {
        return showOnlyExcellentStudents;
    }

    public void setShowOnlyExcellentStudents(boolean showOnlyExcellentStudents) {
        this.showOnlyExcellentStudents = showOnlyExcellentStudents;
    }

    public boolean isShowOnlyStudentsWithDebt() {
        return showOnlyStudentsWithDebt;
    }

    public void setShowOnlyStudentsWithDebt(boolean showOnlyStudentsWithDebt) {
        this.showOnlyStudentsWithDebt = showOnlyStudentsWithDebt;
    }

    public String getFullNameContains() {
        return fullNameContains;
    }

    public void setFullNameContains(String fullNameContains) {
        this.fullNameContains = fullNameContains;
    }

    public String getGroupNameContains() {
        return groupNameContains;
    }

    public void setGroupNameContains(String groupNameContains) {
        this.groupNameContains = groupNameContains;
    }

    public Integer getCourseEquals() {
        return courseEquals;
    }

    public void setCourseEquals(Integer courseEquals) {
        this.courseEquals = courseEquals;
    }
}
