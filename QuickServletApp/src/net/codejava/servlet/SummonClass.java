package net.codejava.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SummonClass extends HttpServlet {

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
		String cName = request.getParameter("className");
		String cNum = request.getParameter("classNumber");
		
		//Course c = new Course(Integer.parseInt(cNum),cName, ## );
		
		writer.println("<html>Class Name:" + cName + "<br/> Class Number:" + cNum +" <br/>Class successfully created. </html>");
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