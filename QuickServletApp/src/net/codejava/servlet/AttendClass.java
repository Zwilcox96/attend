package net.codejava.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AttendClass extends HttpServlet {

	/**
	 * this life-cycle method is invoked when this servlet is first accessed
	 * by the client
	 */
	public void init(ServletConfig config) {
		System.out.println("Servlet is being initialized");
		/*System.out.println("Working Directory = " +
        System.getProperty("user.dir"));*/
	}

	/**
	 * handles HTTP GET request
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		PrintWriter writer = response.getWriter();
		writer.println("<html>Hello, I am a Java servlet!</html>");
		writer.flush();
	}

	/**
	 * handles HTTP POST request
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		PrintWriter writer = response.getWriter();
		String yourID = request.getParameter("student_id");
		String pin = request.getParameter("classPin");
		
    	Calendar time = Calendar.getInstance();
		//Date time = now.getTime();
    	Student student = new Student(Integer.parseInt(yourID));
    	String msg = "Hello " + student.getName() + ", <br/>";
    	//msg = msg + " we have recieved your attendance for today's class. <br/> A confirmation e-mail has been sent as proof.";
        Course c = new Course(5555);

        ClassSession sesh = c.getClassSession();
        msg = msg + sesh.attend(student, time, Integer.parseInt(pin));
		
        if (pin.equals(GoogleSheets.getPin()) ) {
        	int row = GoogleSheets.checkStudent(Integer.parseInt(yourID));
            GoogleSheets.markAttendance(row, yourID);
		}
        
		//GoogleSheets.checkStudent(yourID);
		//WebInterface.markAttend(Integer.parseInt(yourID), Integer.parseInt(pin));
		//String msg = "temp";
		//String msg = WebInterface.markAttend(Integer.parseInt(yourID), Integer.parseInt(pin));
		//WebInterface.instructRegister();
		writer.println("<html>" + msg + "</html>");
		writer.flush();
	}

	/**
	 * this life-cycle method is invoked when the application or the server
	 * is shutting down
	 */
	public void destroy() {
		System.out.println("Servlet is being destroyed");
	}
}