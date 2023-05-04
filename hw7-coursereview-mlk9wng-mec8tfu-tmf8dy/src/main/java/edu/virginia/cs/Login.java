package edu.virginia.cs;

import com.sun.tools.javac.Main;

import java.io.IOException;
import java.util.List;
import java.util.*;

public class Login {

    private static DatabaseManagerImpl database = new DatabaseManagerImpl();
    private static List<Student> students;
    private Student student;

    public Login() throws IOException {
        database.connect();
        Scanner scanner = new Scanner(System.in);
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("Welcome to the Course Review System!");
        System.out.println("Type \"l\" to login or \"c\" to create a new account, then press enter");
        System.out.println("Type \"exit\" to exit the program");
        String loginType = scanner.nextLine();
        restartIfNoLetterTyped(loginType);
        if (loginType.equals("l")) { //meaning that the user is logging into an existing account
            logInToExistingAccount(scanner);
        } else if (loginType.equals("c")) { //meaning that the user is creating a new account
            createNewUserAccount(scanner);
        }
         else if(loginType.equals("exit")) {
            System.out.println("Goodbye");
            database.disconnect();
            System.exit(0);
        }
        else {
            System.out.println("Please enter a valid letter");
            database.disconnect();
            new Login();
        }
        database.disconnect();
        new MainMenu(scanner, this.student);
    }

    private void createNewUserAccount(Scanner scanner) throws IOException {
        System.out.print("Enter a username: ");
        String username = scanner.nextLine();
        if(username.length()<1){
            System.out.println("Please enter a username, at least one character.");
            database.disconnect();
            new Login();
        }
        if (database.userNameAlreadyTaken(username)) { //is the new username already taken
            //unsure if exceptions need to be thrown here or not
            System.out.println("Username is not available, please enter a different one.");
            database.disconnect();
            new Login();
        }
        //if username is invalid report why here and retry
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine();
        if(password.length()<1){
            System.out.println("Please enter a valid password, at least one character.");
            database.disconnect();
            new Login();
        }
            if (!password.equals(confirmPassword)) { // do the passwords match
                //might need to throw error
                System.out.println("Passwords do not match.");
                database.disconnect();
                new Login();
            } else {
                Student student = new Student(username, password);
                database.addStudent(student);
                this.student = student;
            }
            System.out.println("New user created!");
    }

    private void logInToExistingAccount(Scanner scanner) throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (database.userNameAlreadyTaken(username)) { //sees if the username is in the database or not
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        //need to know if this username is in the database or not

            if (database.userNameAndPasswordMatch(username, password)) { //checks userame and password are valid
                this.student = new Student(username, password);
            } else {
                //could throw exception if that is what is needed
                System.out.println("Incorrect password.");
                database.disconnect();
                new Login();
            }
        } else {
            //could also throw exception here
            System.out.println("Invalid Username, please try again or create an account.");
            database.disconnect();
            new Login();
        }
    }

    private static void restartIfNoLetterTyped(String loginType) throws IOException {
        if (loginType.length() < 1) {
            System.out.println("Please enter an argument");
            database.disconnect();
            new Login();
        }
    }

    public Student getStudent() {
        return student;
    }


}