import java.util.Objects;

public class Student {
    private long num;
    private String name;
    private int group;
    private double grade;

    public Student(long num, String name, int group, double grade) {
        this.num = num;
        this.name = name;
        this.group = group;
        this.grade = grade;
    }

    public long getNum() { return num; }
    public String getName() { return name; }
    public int getGroup() { return group; }
    public double getGrade() { return grade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return num == student.num &&
                group == student.group &&
                Double.compare(student.grade, grade) == 0 &&
                Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, name, group, grade);
    }

    @Override
    public String toString() {
        return num + "; " + name + "; " + group + "; " + grade;
    }

    public String toFileString() {
        return num + "; " + name + "; " + group + "; " + grade;
    }
}