package edu.virginia.cs;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// URL: github.com/uva-cs-3140-sp23/hw6-busroute-mlk9wng-1
// Code: Many of the main database functions (creating, deleting tables, adding data to tables, connect and disconnect)
public class DatabaseManagerImpl implements DatabaseManager {
    public static String databaseName = "Reviews.sqlite3";
    Connection connection;

    public Boolean getConnectionOpen() {
        return connectionOpen;
    }

    Boolean connectionOpen = false;

    public static void main(String[] args) throws IOException {
        DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        databaseManager.connect();
        databaseManager.clear();
        List<Student> studentList = List.of(new Student("johnSmith", "wahoowa"), new Student("oliviaMartin", "cavaliers"),
                new Student("emmaWatson", "hermione"), new Student("tedLasso", "football"));
        List<Course> courseList = List.of(new Course("CS", 2100), new Course("APMA", 2130),
                new Course("KPLA", 1607), new Course("CS", 3140), new Course("CS", 3240));
        for (Student student : studentList) {
            databaseManager.addStudent(student);
        }
        for (Course course : courseList) {
            databaseManager.addCourse(course);
        }
        databaseManager.addReview(new Review(studentList.get(0), courseList.get(0), "This course was hard but rewarding.", 4));
        databaseManager.addReview(new Review(studentList.get(0), courseList.get(4), "The professor was good at teaching", 5));
        databaseManager.addReview(new Review(studentList.get(3), courseList.get(0), "This course had hard exams", 3));
        databaseManager.addReview(new Review(studentList.get(3), courseList.get(2), "This class was so much fun", 5));
        databaseManager.addReview(new Review(studentList.get(1), courseList.get(3), "I struggled to understand the material", 2));
        databaseManager.addReview(new Review(studentList.get(2), courseList.get(0), "I learned nothing.", 1));
        databaseManager.disconnect();
    }

    @Override
    public void connect() throws IOException {
        if (connectionOpen) {
            throw new IllegalStateException("Manager is already connected");
        }
        String databaseURL = "jdbc:sqlite:" + databaseName;
//        try{
//            Class.forName("org.sqlite.JDBC");
//            connection = DriverManager.getConnection(databaseURL);
//            connectionOpen = true;
//            connection.setAutoCommit(false);
        try {
            File file = new File(databaseName);
            if (!file.exists()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(databaseURL);
                connectionOpen = true;
                connection.setAutoCommit(false);
                createTables();
            } else {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(databaseURL);
                connectionOpen = true;
                connection.setAutoCommit(false);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void createTables() {
        if (!connectionOpen) {
            throw new IllegalStateException("Manager has not been connected yet");
        }
        if (studentsTableExists() || coursesTableExists() || reviewsTableExists()) {
            throw new IllegalStateException("At least one of the tables already exists in the database");
        }
        String studentString = "create table Students" +
                "(ID INTEGER PRIMARY KEY NOT NULL , userName VARCHAR(50) NOT NULL, password VARCHAR(50) NOT NULL)";
        try (Statement studentStatement = connection.createStatement()) {
            studentStatement.executeUpdate(studentString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String busLinesString = "create table Courses" +
                "( ID INTEGER PRIMARY KEY NOT NULL, department VARCHAR(4) NOT NULL, catalog_Number INT NOT NULL)";
        try (Statement busLinesStatement = connection.createStatement()) {
            busLinesStatement.executeUpdate(busLinesString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String routesString = "create table Reviews" +
                "(ID INTEGER PRIMARY KEY NOT NULL, studentID INT NOT NULL, courseID INT NOT NULL, message VARCHAR(1000) NOT NULL, rating INT NOT NULL, " +
                "FOREIGN KEY (studentID) REFERENCES Students(ID) ON DELETE CASCADE , FOREIGN KEY(courseID) REFERENCES Courses(ID) ON DELETE CASCADE)";
        try (Statement routesStatement = connection.createStatement()) {
            routesStatement.executeUpdate(routesString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean reviewsTableExists() {
        String sql = "select * from sqlite_master WHERE type = 'table' AND name = 'Reviews'";
        return checkIfInMaster(sql);
    }

    private boolean coursesTableExists() {
        String sql = "select * from sqlite_master WHERE type = 'table' AND name = 'Courses'";
        return checkIfInMaster(sql);
    }

    private boolean studentsTableExists() {
        String sql = "select * from sqlite_master WHERE type = 'table' AND name = 'Students'";
        return checkIfInMaster(sql);
    }

    private boolean checkIfInMaster(String sql) {
        int count = 0;
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count != 0;
    }

    @Override
    public void clear() {
        if (!connectionOpen) {
            throw new IllegalStateException("Manager has not been connected yet");
        }
        if (!studentsTableExists() || !reviewsTableExists() || !coursesTableExists()) {
            throw new IllegalStateException("At least one of the tables does not exist in the database");
        }
        String clearStudents = "DELETE FROM Students";
        String clearCourses = "DELETE FROM Courses";
        String clearReviews = "DELETE FROM Reviews";
        try (Statement studentStatement = connection.createStatement(); Statement coursesStatement = connection.createStatement(); Statement reviewsStatement = connection.createStatement()) {
            studentStatement.executeUpdate(clearStudents);
            coursesStatement.executeUpdate(clearCourses);
            reviewsStatement.executeUpdate(clearReviews);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTables() {
        if (!connectionOpen) {
            throw new IllegalStateException("Manager has not been connected yet");
        }
        if (!studentsTableExists() || !reviewsTableExists() || !coursesTableExists()) {
            throw new IllegalStateException("At least one of the tables does not exist in the database");
        }
        String deleteStudents = "DROP TABLE Students";
        String deleteCourses = "DROP TABLE Courses";
        String deleteReviews = "DROP TABLE Reviews";
        try (Statement studentStatement = connection.createStatement(); Statement coursesStatement = connection.createStatement(); Statement reviewsStatement = connection.createStatement()) {
            studentStatement.executeUpdate(deleteStudents);
            coursesStatement.executeUpdate(deleteCourses);
            reviewsStatement.executeUpdate(deleteReviews);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addStudent(Student student) {
        if (!connectionOpen) {
            throw new IllegalStateException("Manager has not been connected yet");
        }
        if (!studentsTableExists()) {
            throw new IllegalStateException("The students table does not exist in the database");
        }
        try {
            if (!userNameAlreadyTaken(student.getUserName())) {
                String addStudent = generateInsertQuery(student);
                Statement statement = connection.createStatement();
                statement.executeUpdate(addStudent);
                statement.close();
            } else {
                throw new IllegalArgumentException("Username is not available");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateInsertQuery(Student student) {
        return String.format("""
                 insert into Students
                 (userName, password)
                 values("%s", "%s");
                """, student.getUserName(), student.getPassword());
    }

    @Override
    public void addCourse(Course course) {
        if (!connectionOpen) {
            throw new IllegalStateException("Manager has not been connected yet");
        }
        if (!coursesTableExists()) {
            throw new IllegalStateException("The Courses table does not exist in the database");
        }
        try {
            String addCourse = generateCourseInsertQuery(course);
            Statement statement = connection.createStatement();
            statement.executeUpdate(addCourse);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateCourseInsertQuery(Course course) {
        return String.format("""
                insert into Courses
                (department, catalog_Number)
                values("%s","%d");
                """, course.getDepartment(), course.getCatalogNumber());
    }

    @Override
    public void addReview(Review review) {
        if (!connectionOpen) {
            throw new IllegalStateException("Manager has not been connected yet");
        }
        if (!coursesTableExists()) {
            throw new IllegalStateException("The Courses table does not exist in the database");
        }
        try {
            String addReview = generateReviewInsertQuery(review);
            Statement statement = connection.createStatement();
            statement.executeUpdate(addReview);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        if (!connectionOpen) {
            throw new IllegalStateException("Manager hasn't been connected yet.");
        }
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        connectionOpen = false;
    }

    private String generateReviewInsertQuery(Review review) {
        return String.format("""
                        insert into Reviews
                        (studentID, courseID, message, rating)
                        values("%d", "%d", "%s", "%d");""",
                getStudentIDByName(review.getStudent().getUserName()), getCourseIDByCourse(review.getCourse()), review.getMessage(), review.getRating());
    }

    public int getStudentIDByName(String userName) {
        String sql = "Select ID from Students where userName ='" + userName + "'";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return rs.getInt("ID");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCourseIDByCourse(Course course) {
        String sql = "Select ID from Courses where department= '" + course.getDepartment() + "' AND catalog_Number =  " + course.getCatalogNumber();
        try (Statement newStatement = connection.createStatement()) {
            ResultSet rs = newStatement.executeQuery(sql);
            while (rs.next()) {
                return rs.getInt("ID");
            }
            throw new RuntimeException("course not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean courseInCoursesTable(Course course) {
        String sql = "Select * From Courses WHERE department = '" + course.getDepartment() + "' AND catalog_Number = " + course.getCatalogNumber();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Review> getReviewsForCourse(Course course) {
        int courseID = getCourseIDByCourse(course);
        List<Review> reviewList = new ArrayList<>();
        String sql = "Select * from Reviews where CourseID = " + courseID;
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                reviewList.add(new Review(rs.getString("message"), rs.getInt("rating")));
            }
            return reviewList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean userNameAndPasswordMatch(String userName, String password) {
        String sql = "Select * from Students WHERE userName = '" + userName + "' AND password = '" + password + "'";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean studentHasAlreadyReviewedCourse(Student student, Course course) {
        int courseID = getCourseIDByCourse(course);
        int studentID = getStudentIDByName(student.getUserName());
        String sql = "Select * from Reviews where studentID = " + studentID + " AND courseID = " + courseID;
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean userNameAlreadyTaken(String userName) {
        String sql = "Select * from Students where userName = '" + userName + "'";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}


