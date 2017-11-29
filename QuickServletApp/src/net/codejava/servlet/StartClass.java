package net.codejava.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StartClass extends HttpServlet {

	/**
	 * this life-cycle method is invoked when this servlet is first accessed
	 * by the client
	 */
	public void init(ServletConfig config) {
		System.out.println("StartClass is being initialized");
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
		String cNum = request.getParameter("callNumber");
		String limit = request.getParameter("time");
		String cPIN = request.getParameter("classPin");
		
		Course c = new Course(5555);
		
		ClassSession sesh = c.makeNewSession(Integer.parseInt(cPIN), Integer.parseInt(limit));
		
		Calendar time = sesh.getDate();
		
		GoogleSheets.getDate();
		GoogleSheets.setPin(cPIN);
		
		writer.println("<html>Time limit:" + time.getTime() + "<br/> PIN:" + cPIN + "</html>");
		writer.flush();
	}

	/**
	 * this life-cycle method is invoked when the application or the server
	 * is shutting down
	 */
	public void destroy() {
		System.out.println("StartClass is being destroyed");
	}
}