package org.example.xmlparser.model;

public class BookFilterCriteria {

    private String authorContains;
    private Integer yearEquals;
    private String categoryContains;

    public String getAuthorContains() {
        return authorContains;
    }

    public void setAuthorContains(String authorContains) {
        this.authorContains = authorContains;
    }

    public Integer getYearEquals() {
        return yearEquals;
    }

    public void setYearEquals(Integer yearEquals) {
        this.yearEquals = yearEquals;
    }

    public String getCategoryContains() {
        return categoryContains;
    }

    public void setCategoryContains(String categoryContains) {
        this.categoryContains = categoryContains;
    }
}
