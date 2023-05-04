package edu.virginia.cs;
import java.io.IOException;
import java.util.*;


public class MainMenu {
    private static final DatabaseManagerImpl database = new DatabaseManagerImpl();

    static Student student; // need to know how to use the database and the student from the login
    public MainMenu(Scanner scanner, Student student) throws IOException {
        MainMenu.student = student;
        database.connect();
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("You have now reached the main menu");
        System.out.println("Please choose one of the following options:");
        System.out.println("To write a review for a course, please enter \"r\"");
        System.out.println("To view the reviews for a course, please enter \"s\"");
        System.out.println("To log out, please enter \"o\"");
        System.out.println("To exit the program, please enter \"exit\"");
        String option = scanner.nextLine();
        noOptionGiven(option, scanner);
        switch (option) {
            case "r" -> {
                //call writing a review
                //System.out.println("Reviewing");
                submitReview(scanner);
            }
            case "s" -> {
                //call seeing reviews
                //System.out.println("Viewing");
                seeReviewsForCourse(scanner);
            }
            case "o" -> {
                System.out.println("logging out");
                database.disconnect();
                new Login();
            }
            case "exit" -> {
                System.out.println("Goodbye");
                database.disconnect();
                System.exit(0);
            }
            default -> {
                System.out.println("Please enter one of the options listed");
                database.disconnect();
                new MainMenu(scanner, student);
            }
        }
    }

    private static void seeReviewsForCourse(Scanner scanner) throws IOException {
        System.out.println("Please enter the course you would like to see the review for by typing the capitalized department followed by a space and then the course number.");
        System.out.println("ex. CS 3140");
        String course = scanner.nextLine();
        String[] words = course.split(" ");
        if(words.length!=2){
            System.out.println("Course entered is invalid. Department must be 4 letters or fewer and catalog number must be 4 digits.");
            database.disconnect();
            new MainMenu(scanner, student);
        }
        String department = words[0];
        int catalog_Number;
        try {
            catalog_Number = Integer.parseInt(words[1]);
        }
        catch (NumberFormatException e){
            System.out.println("Course Number is not a valid number.");
            database.disconnect();
            new MainMenu(scanner, student);
            return;
        }
        if (!validCourse(department, catalog_Number)) {
            System.out.println("Course is invalid. Department must be 4 letters or fewer and catalog number must be 4 digits.");
        } else {
            Course courseToSee = new Course(department, catalog_Number);
            if (!database.courseInCoursesTable(courseToSee)) {
                System.out.println("Course entered has not been reviewed yet.");
                database.disconnect();
                new MainMenu(scanner, student);
            }
            List<Review> courseReviews = database.getReviewsForCourse(courseToSee);
            if (courseReviews.size() == 0) {
                System.out.println("This course does not have any reviews yet");
                database.disconnect();
                new MainMenu(scanner, student);
            }
            int average = 0;
            List<String> messages = new ArrayList<>();
            for (Review courseReview : courseReviews) {
                average = average + courseReview.getRating();
                messages.add(courseReview.getMessage());
            }
            System.out.println("Reviews: ");
            for (String message : messages) {
                System.out.println(message);
            }
            double averageScore = (double) average / courseReviews.size();
            System.out.println("Course Average Rating: " + averageScore + "/5");
            System.out.println();
            System.out.println();
        }
        database.disconnect();
        new MainMenu(scanner, student);
    }

    private static void noOptionGiven(String option, Scanner scanner) throws IOException {
        if (option.length() < 1) {
            System.out.println("Please enter a valid menu option");
            database.disconnect();
            new MainMenu(scanner, student);
        }
    }


    public void submitReview(Scanner scanner) throws IOException {
        System.out.println("Please enter the course you would like to write a review for");
            System.out.println("ex. CS 3140");

            String course = scanner.nextLine();

            String[] words = course.split(" ");
            if(words.length!=2){
                System.out.println("Course entered is invalid. Department must be 4 letters or fewer and catalog number must be 4 digits.");
                database.disconnect();
                new MainMenu(scanner, student);
            }
            String dep = words[0];
            int num;
            // if the course is invalid it needs to take the user back to the main menu
            try {
                num = Integer.parseInt(words[1]);
            }
            catch (NumberFormatException e){
                System.out.println("Course Number is not a valid number.");
                database.disconnect();
                new MainMenu(scanner, student);
                return;
            }

            if(!validCourse(dep,num)){
                System.out.println("Course entered is invalid");
                database.disconnect();
                new MainMenu(scanner, student);
                //take back to main menu
            }


            Course course1 = new Course(dep, num);
            //need to check if the course is in the database
            if(!database.courseInCoursesTable(course1)) {
                database.addCourse(course1);
            }

            if(database.studentHasAlreadyReviewedCourse(student, course1)){
                System.out.println("Student has already reviewed this course");
                database.disconnect();
                new MainMenu(scanner, student);
                //take back to the main menu
            }

        // Submit review
        System.out.print("Enter your review message: ");
        String message = scanner.nextLine();
        String rating_num;
        int rating = 0;
        boolean rated = false;
        while(!rated) {
            System.out.println("Please enter your rating of the course from 1 to 5: ");
            rating_num = scanner.nextLine();
            try {
                rating = Integer.parseInt(rating_num);
//            rating = Integer.parseInt(rating_num);
                if (1 <= rating && rating <= 5) {
                    rated = true;
                }
            }
            catch (NumberFormatException e){
            }

//            if(scanner.hasNextInt()) {
//                rating = scanner.nextInt();
////            rating = Integer.parseInt(rating_num);
//                if (1 <= rating && rating <= 5) {
//                    rated = true;
//                }
//            }
//            else{
//            scanner.next();}
        }

        // Adds review to table
        Review newReview = new Review(student, course1, message, rating);
        database.addReview(newReview);
        System.out.println("Review submitted successfully.");
        database.disconnect();
        new MainMenu(scanner, student);
    }



    static boolean validCourse(String dep, int num) {
        if (!dep.equals(dep.toUpperCase())) {
            return false;
        }
        if (dep.length() > 4) {
            return false;
        }
        String numString = Integer.toString(num);
        int numDigits = numString.length();
        return numDigits == 4;

    }
}




