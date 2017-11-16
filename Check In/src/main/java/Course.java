/*
 * This class creates and maintains courses. 
 * Todo: create methods as described in the class diagram
 */

import java.sql.*;


public class Course {
	private static int callNumber;
	private static String courseName;
	private static Instructor instructor;
	//SQL values
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/attend?autoReconnect=true&useSSL=false";
	static final String USER = "root";
	static final String PASS = "password";
	
	/**
	 * This constructor makes a new Course and stores it in our database.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param cNumber The call number of a course.
	 * @param cName The name of a course.
	 * @param cInstructor The IID of the instructor teaching a course.
	 */
	Course(int cNumber, String cName, int cInstructor){
		callNumber = cNumber;
		
		Connection conn = null;
		Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "INSERT INTO course VALUES('"+ cNumber +"', '" + cName +"', '"+ cInstructor + "');";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	
	/**
	 * This constructor loads a course using our database.
	 * @param cNumber The call number of a course.
	 */
	Course(int cNumber){
		callNumber = cNumber;
		Connection conn = null;
		Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT CallNumber, CourseName, Instructor FROM course " + "WHERE CallNumber = "  + cNumber;
		      ResultSet rs = stmt.executeQuery(sql);

		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         courseName = rs.getString("CourseName");
		         instructor = new Instructor(rs.getInt("Instructor"));
		         //Display values
		         System.out.print("Call Number: " + callNumber);
		         System.out.print(", Course Name: " + courseName);
		         System.out.println(", Instructor: " + instructor.getName());
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   //System.out.println("Goodbye!");
	}
	
	/**
	 * This method enrolls a student in a course.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param S The SID of a student.
	 */
	public static void addStudent(Student S){

		Connection conn = null;
		Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "INSERT INTO enrolled VALUES('"+ S.getSID() + "', '"+ callNumber + "');";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	
	/**
	 * This method returns the Instructor of a course.
	 * @return The course Instructor.
	 */
	public static Instructor getIntsructor(){
		return instructor;
	}
	
	/**
	 * This method returns the call number of a course.
	 * @return The call number of a course.
	 */
	public static int getCallNumber(){
		return callNumber;
	}
	
	/**
	 * This method returns the name of a course.
	 * @return The name of a course.
	 */
	public static String getCourseName(){
		return courseName;
	}
}



