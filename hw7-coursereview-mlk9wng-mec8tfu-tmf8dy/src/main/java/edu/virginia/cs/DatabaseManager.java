package edu.virginia.cs;

import java.io.IOException;
import java.util.List;

public interface DatabaseManager {
    void connect() throws IOException;
    void createTables();
    void clear();
    void deleteTables();
    void addStudent(Student student);
    void addCourse(Course course);
    void addReview(Review review);
    void disconnect();
    boolean courseInCoursesTable(Course course);
    List<Review> getReviewsForCourse(Course course);
    boolean userNameAndPasswordMatch(String userName, String password);
    boolean studentHasAlreadyReviewedCourse(Student student, Course course);
    boolean userNameAlreadyTaken(String userName);

}
