// Katlego Nkwe
// 4252994
// Feedback App

package mysqldemo;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GUI implements ActionListener {

    private static JFrame loginFrame;
    private static JLabel userLabel;
    private static JLabel heading;
    private static JTextField userText;
    private static JLabel passwordLabel;
    private static JPasswordField passwordText;
    private static JButton loginButton;
    private static JButton registerButton;
    private static JLabel loggedIn;
    private static HashMap<String, String> userAccounts;
    private static JFrame gameReviewFrame;
    private static JLabel gameLabel;
    private static JTextField gameText;
    private static JLabel reviewLabel;
    private static JTextArea reviewTextArea;
    private static JButton submitButton;

    private Connection connection;
    private String jdbcUrl;
    private String dbUser;
    private String dbPassword;

    public GUI() {
        userAccounts = new HashMap<>();
        jdbcUrl = "jdbc:mysql://localhost:3307/myDB";
        dbUser = "personal";
        dbPassword = "mysqlustegius";

        try {
            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setupLoginUI();
    }

    private void setupLoginUI() {
        loginFrame = new JFrame("Rockstar Games Review");
        loginFrame.setSize(1366, 768);
        loginFrame.setResizable(false);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        loginFrame.add(panel);
        panel.setLayout(null);

        heading = new JLabel("CREATING || LOGIN ACCOUNT");
        heading.setBounds(75, 206, 931, 91);
        heading.setFont(new Font("Arial", Font.PLAIN, 60));
        heading.setForeground(Color.blue);
        panel.add(heading);

        userLabel = new JLabel("USERNAME:");
        userLabel.setBounds(75, 327, 356, 68);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        panel.add(userLabel);

        userText = new JTextField();
        userText.setFont(new Font("Arial", Font.PLAIN, 50));
        userText.setBounds(490, 339, 300, 45);
        panel.add(userText);

        passwordLabel = new JLabel("PASSWORD:");
        passwordLabel.setBounds(75, 421, 460, 68);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        panel.add(passwordLabel);

        passwordText = new JPasswordField();
        passwordText.setFont(new Font("Arial", Font.PLAIN, 50));
        passwordText.setBounds(490, 432, 300, 45);
        panel.add(passwordText);

        loginButton = new JButton("LOGIN");
        loginButton.setBounds(524, 553, 232, 77);
        loginButton.setBackground(Color.red);
        loginButton.setForeground(Color.white);
        loginButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent arg) {
        		try {
        			Class.forName("com.mysql.jdbc.Driver");
        			Connection con = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
        			Statement stmt = con.createStatement();
        			String sql= "Select* from account where Username='"+userText.getText()+"' and Password='"+passwordText.getText().toString()+"'";
        			ResultSet rs = stmt.executeQuery(sql);
        			if(rs.next()) {
        				loggedIn.setText("Login successful for " + userText.getText());
                        openGameReviewWindow(userText.getText());
        			}else {
        				JOptionPane.showMessageDialog(null, "Incorect username or password");
        			}
        			con.close();
        		}catch(Exception f) {System.out.print(f);}
        	}
        });
        panel.add(loginButton);
        

        registerButton = new JButton("REGISTER");
        registerButton.setBounds(250, 553, 232, 77);
        registerButton.setBackground(Color.RED);
        registerButton.setForeground(Color.white);
        registerButton.addActionListener(e -> handleRegistration());
        panel.add(registerButton);

        loggedIn = new JLabel("");
        loggedIn.setBounds(10, 110, 300, 25);
        panel.add(loggedIn);

        loginFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userText.getText();
        String password = new String(passwordText.getPassword());

        try {
            if (userAccounts.containsKey(username)) {
                String storedPassword = userAccounts.get(username);
                if (storedPassword.equals(password)) {
                    loggedIn.setText("Login successful for " + username);
                    openGameReviewWindow(username);
                } else {
                    loggedIn.setText("Login failed. Incorrect password.");
                }
            } else {
                loggedIn.setText("Login failed. User not found.");
            }
        } catch (Exception ex) {
            loggedIn.setText("Error: " + ex.getMessage());
        }
    }

    private void handleRegistration() {
        String username = userText.getText();
        String password = new String(passwordText.getPassword());

        if (!username.isEmpty() && !password.isEmpty()) {
            if (registerUserInDatabase(username, password)) {
                loggedIn.setText("Account created for " + username);
                userAccounts.put(username, password);
            } else {
                loggedIn.setBounds(75, 50, 1500, 100);
                loggedIn.setFont(new Font("Arial", Font.PLAIN, 46));
                loggedIn.setText("Registration failed. Please try again.");
            }
        } else {
            loggedIn.setBounds(75, 50, 1500, 100);
            loggedIn.setFont(new Font("Arial", Font.PLAIN, 46));
            loggedIn.setText("Please enter both username and password.");
        }
    }

    private boolean registerUserInDatabase(String username, String password) {
        try {
            String query = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openGameReviewWindow(String username) {
        gameReviewFrame = new JFrame("Game Review");
        gameReviewFrame.setSize(1366, 768);
        gameReviewFrame.setResizable (false);
        gameReviewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(null);

        gameLabel = new JLabel("Game to review:");
        gameLabel.setBounds(48, 140, 536, 68);
        gameLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        reviewPanel.add(gameLabel);

        gameText = new JTextField();
        gameText.setFont(new Font("Arial", Font.PLAIN, 50));
        gameText.setBounds(560, 152, 300, 45);
        reviewPanel.add(gameText);

        reviewLabel = new JLabel("Write Feedback:");
        reviewLabel.setBounds(48, 302, 536, 68);
        reviewLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        reviewPanel.add(reviewLabel);

        reviewTextArea = new JTextArea();
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setFont(new Font("Arial", Font.PLAIN, 50));
        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        scrollPane.setBounds(555, 325, 795, 285);
        reviewPanel.add(scrollPane);

        submitButton = new JButton("PUBLISH FEEDBACK");
        submitButton.setBounds(567, 626, 232, 77);
        submitButton.setBackground(Color.red);
        submitButton.setForeground(Color.white);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = gameText.getText();
                String review = reviewTextArea.getText();
                saveReviewToDatabase(username, title, review);
                
            }
        });
        reviewPanel.add(submitButton);

        gameReviewFrame.add(reviewPanel);
        gameReviewFrame.setVisible(true);

        // Close the login window
        loginFrame.dispose();
    }

    private void saveReviewToDatabase(String username, String title, String review) {
        String query = "INSERT INTO reviews (username, title, review) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement1 = connection.prepareStatement(query)) {
            preparedStatement1.setString(1, username);
            preparedStatement1.setString(2, title);
            preparedStatement1.setString(3, review);

            int rowsInserted = preparedStatement1.executeUpdate();
            if (rowsInserted > 0) {
                // Review successfully saved to the database
                JOptionPane.showMessageDialog(gameReviewFrame, "Review submitted:\nTitle: " + title + "\nReview: " + review);
            } else {
                // Handle the case where the review wasn't saved
                JOptionPane.showMessageDialog(gameReviewFrame, "Failed to save the review. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related exceptions
            JOptionPane.showMessageDialog(gameReviewFrame, "An error occurred while saving the review.");
        }
    }
		

		
    }
