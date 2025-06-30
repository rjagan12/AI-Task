package com.example.bank;

import java.util.*;
import java.sql.*;
import java.io.*;

/**
 * This is a really bad bank account class with lots of problems
 * Don't use this in production!
 */
public class BankAccount {
    // Bad: public fields - security issue
    public String accountNumber;
    public double balance;
    public String password;
    
    // Bad: static variables that can cause issues
    private static int totalAccounts = 0;
    private static List<BankAccount> allAccounts = new ArrayList<>();
    
    // Bad: hardcoded database credentials - security issue
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "password123";
    
    // Bad: global connection - resource leak potential
    private static Connection dbConnection;
    
    public BankAccount() {
        this.accountNumber = generateAccountNumber();
        this.balance = 0.0;
        this.password = "default123";
        totalAccounts++;
        allAccounts.add(this);
    }
    
    // Bad: overly complex method with multiple responsibilities
    public void processTransaction(String transactionType, double amount, String description, 
                                 String fromAccount, String toAccount, String currency, 
                                 boolean isUrgent, String approvalCode, Date transactionDate) {
        try {
            // Bad: SQL injection vulnerability
            String sql = "INSERT INTO transactions (type, amount, description, from_account, to_account, currency, urgent, approval_code, date) VALUES ('" + 
                        transactionType + "', " + amount + ", '" + description + "', '" + fromAccount + "', '" + 
                        toAccount + "', '" + currency + "', " + isUrgent + ", '" + approvalCode + "', '" + transactionDate + "')";
            
            Statement stmt = dbConnection.createStatement();
            stmt.execute(sql);
            
            // Bad: complex nested conditions
            if (transactionType.equals("DEPOSIT")) {
                if (amount > 0) {
                    if (amount <= 10000) {
                        balance += amount;
                        System.out.println("Deposit successful: " + amount);
                    } else {
                        if (isUrgent) {
                            if (approvalCode != null && approvalCode.length() > 0) {
                                balance += amount;
                                System.out.println("Large urgent deposit approved: " + amount);
                            } else {
                                System.out.println("Large urgent deposit requires approval code");
                            }
                        } else {
                            System.out.println("Large deposit requires urgent flag");
                        }
                    }
                } else {
                    System.out.println("Invalid deposit amount");
                }
            } else if (transactionType.equals("WITHDRAWAL")) {
                if (amount > 0) {
                    if (amount <= balance) {
                        if (amount <= 1000) {
                            balance -= amount;
                            System.out.println("Withdrawal successful: " + amount);
                        } else {
                            if (isUrgent) {
                                if (approvalCode != null && approvalCode.length() > 0) {
                                    balance -= amount;
                                    System.out.println("Large urgent withdrawal approved: " + amount);
                                } else {
                                    System.out.println("Large urgent withdrawal requires approval code");
                                }
                            } else {
                                System.out.println("Large withdrawal requires urgent flag");
                            }
                        }
                    } else {
                        System.out.println("Insufficient funds");
                    }
                } else {
                    System.out.println("Invalid withdrawal amount");
                }
            } else if (transactionType.equals("TRANSFER")) {
                if (amount > 0) {
                    if (amount <= balance) {
                        if (amount <= 5000) {
                            balance -= amount;
                            // Bad: hardcoded account lookup - performance issue
                            for (BankAccount acc : allAccounts) {
                                if (acc.accountNumber.equals(toAccount)) {
                                    acc.balance += amount;
                                    break;
                                }
                            }
                            System.out.println("Transfer successful: " + amount);
                        } else {
                            if (isUrgent) {
                                if (approvalCode != null && approvalCode.length() > 0) {
                                    balance -= amount;
                                    for (BankAccount acc : allAccounts) {
                                        if (acc.accountNumber.equals(toAccount)) {
                                            acc.balance += amount;
                                            break;
                                        }
                                    }
                                    System.out.println("Large urgent transfer approved: " + amount);
                                } else {
                                    System.out.println("Large urgent transfer requires approval code");
                                }
                            } else {
                                System.out.println("Large transfer requires urgent flag");
                            }
                        }
                    } else {
                        System.out.println("Insufficient funds for transfer");
                    }
                } else {
                    System.out.println("Invalid transfer amount");
                }
            }
            
            // Bad: resource not properly closed
            stmt.close();
            
        } catch (Exception e) {
            // Bad: generic exception handling
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // Bad: method with too many parameters
    public void createAccount(String firstName, String lastName, String email, String phone, 
                            String address, String city, String state, String zipCode, 
                            String country, String accountType, String initialDeposit, 
                            String socialSecurityNumber, String dateOfBirth, String occupation) {
        try {
            // Bad: SQL injection vulnerability
            String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, city, state, zip_code, country, account_type, initial_deposit, ssn, dob, occupation) VALUES ('" + 
                        firstName + "', '" + lastName + "', '" + email + "', '" + phone + "', '" + 
                        address + "', '" + city + "', '" + state + "', '" + zipCode + "', '" + 
                        country + "', '" + accountType + "', " + initialDeposit + ", '" + 
                        socialSecurityNumber + "', '" + dateOfBirth + "', '" + occupation + "')";
            
            Statement stmt = dbConnection.createStatement();
            stmt.execute(sql);
            stmt.close();
            
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }
    
    // Bad: method that does too many things
    public void generateReport(String reportType, String startDate, String endDate, 
                             String format, String email, boolean includeCharts, 
                             String sortBy, String filterBy) {
        try {
            // Bad: complex nested logic
            if (reportType.equals("TRANSACTION")) {
                if (format.equals("PDF")) {
                    if (includeCharts) {
                        // Bad: hardcoded file path - security issue
                        File file = new File("/tmp/report.pdf");
                        FileWriter writer = new FileWriter(file);
                        writer.write("Transaction Report\n");
                        writer.write("Account: " + accountNumber + "\n");
                        writer.write("Balance: " + balance + "\n");
                        writer.close();
                    } else {
                        File file = new File("/tmp/report.pdf");
                        FileWriter writer = new FileWriter(file);
                        writer.write("Transaction Report\n");
                        writer.write("Account: " + accountNumber + "\n");
                        writer.write("Balance: " + balance + "\n");
                        writer.close();
                    }
                } else if (format.equals("CSV")) {
                    File file = new File("/tmp/report.csv");
                    FileWriter writer = new FileWriter(file);
                    writer.write("Account,Transaction,Amount,Date\n");
                    writer.write(accountNumber + ",DEPOSIT," + balance + "," + new Date() + "\n");
                    writer.close();
                } else if (format.equals("EXCEL")) {
                    File file = new File("/tmp/report.xlsx");
                    FileWriter writer = new FileWriter(file);
                    writer.write("Transaction Report\n");
                    writer.write("Account: " + accountNumber + "\n");
                    writer.write("Balance: " + balance + "\n");
                    writer.close();
                }
                
                // Bad: email sending without validation
                if (email != null && email.length() > 0) {
                    // Bad: command injection vulnerability
                    String command = "mail -s 'Report' " + email + " < /tmp/report.pdf";
                    Runtime.getRuntime().exec(command);
                }
            } else if (reportType.equals("BALANCE")) {
                // Similar complex logic for balance report
                File file = new File("/tmp/balance_report.pdf");
                FileWriter writer = new FileWriter(file);
                writer.write("Balance Report\n");
                writer.write("Account: " + accountNumber + "\n");
                writer.write("Balance: " + balance + "\n");
                writer.close();
            }
            
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
    
    // Bad: inefficient account number generation
    private String generateAccountNumber() {
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            result += random.nextInt(10);
        }
        return result;
    }
    
    // Bad: getter that exposes internal state
    public double getBalance() {
        return balance;
    }
    
    // Bad: setter that allows direct balance manipulation
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    // Bad: method that violates encapsulation
    public String getPassword() {
        return password;
    }
    
    // Bad: static method that can cause issues
    public static int getTotalAccounts() {
        return totalAccounts;
    }
    
    // Bad: method that returns internal collection
    public static List<BankAccount> getAllAccounts() {
        return allAccounts;
    }
} 