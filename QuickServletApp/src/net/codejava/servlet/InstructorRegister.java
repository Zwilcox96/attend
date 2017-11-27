package net.codejava.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InstructorRegister extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2030;

	/**
	 * this life-cycle method is invoked when this servlet is first accessed
	 * by the client
	 */
	public void init(ServletConfig config) {
		System.out.println("InstructorRegister is being initialized");
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
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		PrintWriter writer = response.getWriter();
		String yourID = request.getParameter("professor_id"),
			   name = request.getParameter("professor_name"),
			   email = request.getParameter("professor_email"),
			   password = request.getParameter("professor_password");

		writer.println("<html>It worked.</html>");
		writer.println("<html>");
		writer.println("student_ID: " + yourID);
		writer.println("student name: "+ name);
		writer.println("email: " + email);
		writer.println("password: " + password);
		writer.println("</html>");
		writer.flush();
	}

	/**
	 * this life-cycle method is invoked when the application or the server
	 * is shutting down
	 */
	public void destroy() {
		System.out.println("InstructorRegister is being destroyed");
	}
}