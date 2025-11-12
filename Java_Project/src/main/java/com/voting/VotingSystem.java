package com.voting;

import java.sql.*;
import java.util.Scanner;

public class VotingSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/voting_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private Connection connection;
    private Scanner scanner;
    private int currentUserId;
    private String currentUserRole;
    
    public VotingSystem() {
        scanner = new Scanner(System.in);
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void start() {
        while (true) {
            System.out.println("\n=== ONLINE VOTING SYSTEM ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("Thank you for using Voting System!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }
    
    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            String sql = "SELECT id, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                currentUserId = rs.getInt("id");
                currentUserRole = rs.getString("role");
                System.out.println("Login successful! Welcome " + username);
                
                if ("ADMIN".equals(currentUserRole)) {
                    adminMenu();
                } else {
                    voterMenu();
                }
            } else {
                System.out.println("Invalid credentials!");
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
    }
    
    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        try {
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
            System.out.println("Registration successful! Please login.");
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }
    
    private void adminMenu() {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Add Candidate");
            System.out.println("2. View Results");
            System.out.println("3. View All Voters");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    addCandidate();
                    break;
                case 2:
                    viewResults();
                    break;
                case 3:
                    viewVoters();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }
    
    private void voterMenu() {
        while (true) {
            System.out.println("\n=== VOTER MENU ===");
            System.out.println("1. Vote");
            System.out.println("2. View Candidates");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    vote();
                    break;
                case 2:
                    viewCandidates();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }
    
    private void addCandidate() {
        System.out.print("Candidate Name: ");
        String name = scanner.nextLine();
        System.out.print("Party: ");
        String party = scanner.nextLine();
        System.out.print("Position: ");
        String position = scanner.nextLine();
        
        try {
            String sql = "INSERT INTO candidates (name, party, position) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, party);
            stmt.setString(3, position);
            stmt.executeUpdate();
            System.out.println("Candidate added successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to add candidate: " + e.getMessage());
        }
    }
    
    private void viewResults() {
        try {
            String sql = "SELECT name, party, position, votes FROM candidates ORDER BY position, votes DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\n=== ELECTION RESULTS ===");
            while (rs.next()) {
                System.out.printf("%s (%s) - %s: %d votes%n",
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getString("position"),
                    rs.getInt("votes"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing results: " + e.getMessage());
        }
    }
    
    private void viewVoters() {
        try {
            String sql = "SELECT username, email, has_voted FROM users WHERE role = 'VOTER'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\n=== REGISTERED VOTERS ===");
            while (rs.next()) {
                String votedStatus = rs.getBoolean("has_voted") ? "Voted" : "Not Voted";
                System.out.printf("%s (%s) - %s%n",
                    rs.getString("username"),
                    rs.getString("email"),
                    votedStatus);
            }
        } catch (SQLException e) {
            System.err.println("Error viewing voters: " + e.getMessage());
        }
    }
    
    private void viewCandidates() {
        try {
            String sql = "SELECT id, name, party, position FROM candidates ORDER BY position";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\n=== CANDIDATES ===");
            while (rs.next()) {
                System.out.printf("%d. %s (%s) - %s%n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getString("position"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing candidates: " + e.getMessage());
        }
    }
    
    private void vote() {
        // Check if user has already voted
        try {
            String checkSql = "SELECT has_voted FROM users WHERE id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, currentUserId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getBoolean("has_voted")) {
                System.out.println("You have already voted!");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error checking vote status: " + e.getMessage());
            return;
        }
        
        viewCandidates();
        System.out.print("Enter candidate ID to vote: ");
        int candidateId = scanner.nextInt();
        scanner.nextLine();
        
        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            // Record the vote
            String voteSql = "INSERT INTO votes (user_id, candidate_id, position) " +
                           "SELECT ?, id, position FROM candidates WHERE id = ?";
            PreparedStatement voteStmt = connection.prepareStatement(voteSql);
            voteStmt.setInt(1, currentUserId);
            voteStmt.setInt(2, candidateId);
            voteStmt.executeUpdate();
            
            // Update candidate vote count
            String updateSql = "UPDATE candidates SET votes = votes + 1 WHERE id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setInt(1, candidateId);
            updateStmt.executeUpdate();
            
            // Mark user as voted
            String userSql = "UPDATE users SET has_voted = TRUE WHERE id = ?";
            PreparedStatement userStmt = connection.prepareStatement(userSql);
            userStmt.setInt(1, currentUserId);
            userStmt.executeUpdate();
            
            connection.commit();
            System.out.println("Vote recorded successfully!");
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            System.err.println("Voting failed: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        VotingSystem system = new VotingSystem();
        system.start();
    }
}