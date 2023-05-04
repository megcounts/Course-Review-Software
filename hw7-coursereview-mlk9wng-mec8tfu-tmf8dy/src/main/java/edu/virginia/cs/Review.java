package edu.virginia.cs;

public class Review {
    private Student student;
    private Course course;
    private String message;
    private int rating;
    public Review(Student student, Course course, String message, int rating){
        this.course = course;
        this.student = student;
        this.message = message;
        if(0<=rating&& rating<=5){
            this.rating = rating;
        }
    }
    public Review(String message, int rating){
        this.message = message;
        if(0<=rating&& rating<=5){
            this.rating = rating;
        }
    }
    public Course getCourse() {
        return course;
    }
    public int getRating() {
        return rating;
    }
    public String getMessage() {
        return message;
    }
    public Student getStudent() {
        return student;
    }
    public void addReviewToCourse(){
        course.addReview(this);
    }
}
