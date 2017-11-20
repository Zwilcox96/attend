// This is SheetsQuickstart. This program is used as a demo to demostrate an update to a fix cell location on a Google sheet
// Todos: Please be sure to update the location of your client_secret.json file & the Googlesheet id before running your program.
// Author: Doan Nguyen
// Date: 6/1/17

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.*;


public class SheetsQuickstart extends GoogleSheets{
	
    
    
    /**
     * This code was borrowed from https://stackoverflow.com/questions/37495808/java-send-email-via-gmail
     * @param email
     * @param name
     */
    public static void sendReceipt(String email, String subject, String realMessage) {
    	String to = email;  

    	//Get the session object  
    	Properties props = new Properties();
    	props.put("mail.smtp.host", "smtp.gmail.com");
    	props.put("mail.smtp.socketFactory.port", "465");
    	props.put("mail.smtp.socketFactory.class",
    			"javax.net.ssl.SSLSocketFactory");
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.port", "465");

    	Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
    			protected PasswordAuthentication getPasswordAuthentication() {
    				return new PasswordAuthentication("agonz08@gmail.com", "extzhdzabitmjmhm");//change accordingly  
    			}
    		});
    	//compose message  
    	try {
    		MimeMessage message = new MimeMessage(session);
    		message.setFrom(new InternetAddress("ecs@ecs.edu"));//change accordingly  
    		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    		message.setSubject(subject);
    		message.setText(realMessage);
    		//send message  
    		Transport.send(message);
    		System.out.println("message sent successfully");
    	} catch (MessagingException e) {
    		throw new RuntimeException(e);
    	}
    }
            
   
    
    public static void main(String[] args) throws IOException {
       
        Scanner kb = new Scanner(System.in);
        System.out.println("Please enter your name:");
        String name = kb.nextLine();
        
        if (name.equals("Professor")) {
        	setPin();
        } else if(name.equals("reg")) {
        	Register.createInstructor(1234, "test@test.edu", "Tester Testersmith", "hi mom");
        	Instructor i = new Instructor(1234);
        	String def = i.getEmail();
        	System.out.println(def);
        	/*
        	Student s = new Student(1234);
        	int abc = s.getSID();
        	System.out.println(abc);
        	
        	Course c = new Course(5555);
        	Student s = new Student(1234);
        	Instructor hi = new Instructor(1234);
        	Course[] sc = s.getCourses();
        	Course[] hic = s.getCourses();
        	System.out.println(sc[0].getCourseName());
        	System.out.println(hic[0].getCourseName());
        	*/
        } else {
            System.out.println("Please enter the PIN for todays class:");
            String studentPin = kb.nextLine();
            if (studentPin.equals(getPin()) ) {
            	int row = checkStudent(name);
                markAttendance(row, name);
            } else {
            	System.out.println("You have entered an incorrect PIN!");
            }
        }   
        kb.close();
    }
}