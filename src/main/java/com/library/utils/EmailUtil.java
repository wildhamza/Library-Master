package com.library.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmailUtil {
    
    // This is a simplified email utility for the LMS
    // In a real application, this would connect to an email service
    
    public static boolean sendOverdueReminder(String email, String userName, String bookTitle, 
                                              LocalDate dueDate, int daysOverdue) {
        // Simulate sending an email
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String dueDateFormatted = dueDate.format(formatter);
        double fine = daysOverdue * 0.5; // $0.50 per day
        
        System.out.println("======= OVERDUE BOOK REMINDER =======");
        System.out.println("To: " + email);
        System.out.println("Subject: Overdue Library Book Reminder");
        System.out.println("Body:");
        System.out.println("Dear " + userName + ",");
        System.out.println("");
        System.out.println("This is a reminder that the book \"" + bookTitle + "\" was due on " + dueDateFormatted + ".");
        System.out.println("The book is now " + daysOverdue + " days overdue, with an accumulated fine of $" + String.format("%.2f", fine) + ".");
        System.out.println("");
        System.out.println("Please return the book as soon as possible to avoid additional charges.");
        System.out.println("");
        System.out.println("Regards,");
        System.out.println("Library Management System");
        System.out.println("=====================================");
        
        // In a real application, this would use JavaMail or similar
        // For this example, we'll just return true to simulate success
        return true;
    }
    
    public static boolean sendRegistrationConfirmation(String email, String userName) {
        // Simulate sending a confirmation email
        System.out.println("======= REGISTRATION CONFIRMATION =======");
        System.out.println("To: " + email);
        System.out.println("Subject: Library Account Registration");
        System.out.println("Body:");
        System.out.println("Dear " + userName + ",");
        System.out.println("");
        System.out.println("Thank you for registering with our Library Management System.");
        System.out.println("Your account has been successfully created.");
        System.out.println("");
        System.out.println("You can now log in and start borrowing books.");
        System.out.println("");
        System.out.println("Regards,");
        System.out.println("Library Management System");
        System.out.println("=========================================");
        
        return true;
    }
}
