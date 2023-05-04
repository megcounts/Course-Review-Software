package edu.virginia.cs;

import java.util.List;

public class Course {
    private String department;
    private int catalogNumber;
    private List<Review> reviewList;

    public Course(String department, int catalogNumber){
        if(department.length()>4){
            throw new IllegalArgumentException("Invalid department");
        }
        if(String.valueOf(catalogNumber).length()!=4){
            throw new IllegalArgumentException("Invalid catalog number");
        }
        this.department = department;
        this.catalogNumber = catalogNumber;
    }
    public int getCatalogNumber() {
        return catalogNumber;
    }
    public String getDepartment(){
        return department;
    }
    public void addReview(Review review){
        if(!reviewList.contains(review)){
            reviewList.add(review);
        }
    }
}
