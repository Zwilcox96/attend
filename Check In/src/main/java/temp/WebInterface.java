package temp;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.*;

import net.codejava.servlet.ClassSession;
import net.codejava.servlet.Course;
import net.codejava.servlet.Student;

import java.util.Calendar;

public class WebInterface{
	
    public static String markAttend(int sid, int pin) throws IOException
    {
    	String out = ""; 
    	Calendar time = Calendar.getInstance();
    	Student student = new Student(sid);
    	out = student.getName();
    	
        Course c = new Course(5555);

        ClassSession sesh = c.getClassSession();
        sesh.attend(student, time, pin);
    	
        out += " we have recieved your attendance for today's class. <br/> A confirmation e-mail has been sent as proof.";
        
    	return out;
    }
    
    public static String instructRegister() throws IOException
    {
    	return "temp";
    }
}