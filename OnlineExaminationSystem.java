import java.util.*;
import java.util.concurrent.*;

public class OnlineExaminationSystem {

    private static Map<String, String> users = new HashMap<>(); // username -> password
    private static Map<String, UserProfile> profiles = new HashMap<>(); // username -> UserProfile

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initDummyData();

        System.out.println("Welcome to the Online Examination System!");

        // Login
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (authenticate(username, password)) {
            System.out.println("Login successful!");
            UserProfile user = profiles.get(username);

            boolean exit = false;
            while (!exit) {
                System.out.println("\n1. Update Profile");
                System.out.println("2. Update Password");
                System.out.println("3. Start Exam");
                System.out.println("4. Logout");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        updateProfile(user, scanner);
                        break;
                    case 2:
                        updatePassword(username, scanner);
                        break;
                    case 3:
                        startExam(scanner);
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Logged out successfully!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } else {
            System.out.println("Invalid username or password.");
        }

        scanner.close();
    }

    private static void initDummyData() {
        // Dummy user data
        users.put("user1", "password1");
        profiles.put("user1", new UserProfile("User One", "user1@example.com"));
    }

    private static boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    private static void updateProfile(UserProfile user, Scanner scanner) {
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();

        user.setName(newName);
        user.setEmail(newEmail);

        System.out.println("Profile updated successfully!");
    }

    private static void updatePassword(String username, Scanner scanner) {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        if (users.get(username).equals(currentPassword)) {
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();
            users.put(username, newPassword);
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Incorrect current password.");
        }
    }

    private static void startExam(Scanner scanner) {
        String[] questions = {
            "What is the capital of France?",
            "What is 2 + 2?",
            "What is the capital of Japan?"
        };
        String[][] options = {
            {"1. Berlin", "2. Madrid", "3. Paris", "4. Rome"},
            {"1. 3", "2. 4", "3. 5", "4. 6"},
            {"1. Beijing", "2. Seoul", "3. Tokyo", "4. Bangkok"}
        };
        int[] correctAnswers = {3, 2, 3};
        int[] userAnswers = new int[questions.length];

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Future<?> future = executor.schedule(() -> {
            System.out.println("\nTime's up! Submitting your answers...");
            displayResults(questions, options, correctAnswers, userAnswers);
        }, 5, TimeUnit.MINUTES); // 5-minute timer

        for (int i = 0; i < questions.length; i++) {
            System.out.println("\n" + questions[i]);
            for (String option : options[i]) {
                System.out.println(option);
            }
            System.out.print("Enter your answer (1-4): ");
            userAnswers[i] = scanner.nextInt();
        }

        future.cancel(true); // Cancel the timer if user finishes early
        executor.shutdown();

        displayResults(questions, options, correctAnswers, userAnswers);
    }

    private static void displayResults(String[] questions, String[][] options, int[] correctAnswers, int[] userAnswers) {
        int score = 0;
        System.out.println("\nExam Results:");
        for (int i = 0; i < questions.length; i++) {
            System.out.println("\n" + questions[i]);
            for (String option : options[i]) {
                System.out.println(option);
            }
            System.out.println("Correct answer: " + correctAnswers[i]);
            System.out.println("Your answer: " + userAnswers[i]);
            if (correctAnswers[i] == userAnswers[i]) {
                score++;
            }
        }
        System.out.println("Your score: " + score + "/" + questions.length);
    }

    static class UserProfile {
        private String name;
        private String email;

        public UserProfile(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
