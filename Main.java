import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sms_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Shreyash@19";

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("--- JDBC connection ready. ---");
        } catch (ClassNotFoundException e) {
            System.err.println("Uh oh, MySQL driver not found.");
            return;
        }

        while (true) {
            System.out.println("\n--- My Simple Student Roster ---");
            System.out.println("1. Add a new student");
            System.out.println("2. See all students");
            System.out.println("3. Change a student's contact info");
            System.out.println("4. Remove a student (by ID)");
            System.out.println("5. Quit the application");
            System.out.print("What do you want to do? (1-5): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addNewStudent();
                        break;
                    case 2:
                        viewAllStudents();
                        break;
                    case 3:
                        updateStudent();
                        break;
                    case 4:
                        deleteStudent();
                        break;
                    case 5:
                        System.out.println("Closing the tool. See you next time!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Not a valid option. Please choose a number from 1 to 5.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter a number for your choice.");
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private static void addNewStudent() {
        System.out.println("\n--- Adding a Student ---");
        try (Connection conn = getConnection()) {
            System.out.print("First Name: ");
            String firstName = scanner.nextLine();
            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();
            System.out.print("Email (needs to be unique): ");
            String email = scanner.nextLine();
            System.out.print("Phone Number: ");
            String phone = scanner.nextLine();

            String enrollmentDate = LocalDate.now().toString();

            String sql = "INSERT INTO students (first_name, last_name, email, phone, enrollment_date) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, phone);
                pstmt.setString(5, enrollmentDate);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Success! Student added.");
                } else {
                    System.out.println("Failed to add student.");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                System.err.println("Can't add. That email is already taken or missing a required field. Details: " + e.getMessage());
            } else {
                System.err.println("Oops, database error: " + e.getMessage());
            }
        }
    }

    private static void viewAllStudents() {
        System.out.println("\n--- Current Student Roster ---");
        String sql = "SELECT * FROM students ORDER BY student_id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("| %-4s | %-15s | %-15s | %-30s | %-15s | %-15s |\n",
                    "ID", "First Name", "Last Name", "Email", "Phone", "Joined Date");
            System.out.println("-------------------------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("student_id");
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                Date enrollDate = rs.getDate("enrollment_date");

                System.out.printf("| %-4d | %-15s | %-15s | %-30s | %-15s | %-15s |\n",
                        id, first, last, email, phone, enrollDate);
            }
            if (!found) {
                System.out.println("The database seems empty. Try adding a student first!");
            }

        } catch (SQLException e) {
            System.err.println("Oops, error loading the student list: " + e.getMessage());
        }
    }

    private static void updateStudent() {
        System.out.println("\n--- Update Contact Info ---");
        System.out.print("Enter the Student ID you want to change: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            // Check if they exist first
            if (!checkStudentExists(id)) {
                System.out.println("I couldn't find a student with ID " + id + ".");
                return;
            }

            System.out.print("Enter the NEW Email: ");
            String newEmail = scanner.nextLine();
            System.out.print("Enter the NEW Phone Number: ");
            String newPhone = scanner.nextLine();

            String sql = "UPDATE students SET email = ?, phone = ? WHERE student_id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, newEmail);
                pstmt.setString(2, newPhone);
                pstmt.setInt(3, id);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("✅ ID " + id + "'s contact info updated successfully!");
                } else {
                    System.out.println("Update didn't work (0 rows changed).");
                }

            }
            catch (SQLException e) {
                if (e.getSQLState().startsWith("23")) {
                    System.err.println("That new email is already used by another student. Details: " + e.getMessage());
                } else {
                    System.err.println("Database problem during update: " + e.getMessage());
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("Please enter a valid number for the Student ID.");
        } catch (SQLException e) {
            System.err.println("Connection error while trying to find the student: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        System.out.println("\n--- Remove a Student ---");
        System.out.print("Enter the Student ID you want to remove: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());
            String sql = "DELETE FROM students WHERE student_id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Student ID " + id + " has been successfully removed.");
                } else {
                    System.out.println("I couldn't find a student with ID " + id + " to remove.");
                }

            }
        } catch (NumberFormatException e) {
            System.err.println("Please enter a valid number for the Student ID.");
        } catch (SQLException e) {
            System.err.println("Database problem during deletion: " + e.getMessage());
        }
    }

    private static boolean checkStudentExists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
