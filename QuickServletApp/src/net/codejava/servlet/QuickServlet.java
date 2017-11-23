package net.codejava.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import temp.SheetsQuickstart;
import temp.GoogleSheets;

public class QuickServlet extends HttpServlet {

	/**
	 * this life-cycle method is invoked when this servlet is first accessed
	 * by the client
	 */
	public void init(ServletConfig config) {
		System.out.println("Servlet is being initialized");
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
	}

	/**
	 * handles HTTP GET request
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		PrintWriter writer = response.getWriter();
		/*System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));*/
		SheetsQuickstart.updateSheets();
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
		if (pin.equals(GoogleSheets.getPin()) ) {
        	int row = GoogleSheets.checkStudent(yourID);
            GoogleSheets.markAttendance(row, yourID);
		}
		//request.getParameter("");
		writer.println("<html>message sent successfully</html>");
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