package edu.virginia.cs;

import java.util.List;

public class Student {
    private int id;
    private String userName;
    private String password;
    private List<Course> courseList;

    public Student(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    public void addCourse(Course course){
        if(!courseList.contains(course)){
            courseList.add(course);
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
