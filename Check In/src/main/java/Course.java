/**
 * This class is used to create and manipulate instructors.
 * @author Zack Wilcox
 * @since 11-15-2017
 */

import java.sql.*;
import java.util.Calendar;

public class Course {
	private static int callNumber;
	private static String courseName;
	private static Instructor instructor;
	private static ClassSession currentSession;
	//SQL values
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://attend.ctddylfx9obm.us-west-1.rds.amazonaws.com:3306/attend?autoReconnect"
			+ "=true&useSSL=false";
	static final String USER = "letsstore";
	static final String PASS = "iloveherky";
	
	/**
	 * This constructor makes a new Course and stores it in our database.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param cNumber The call number of a course.
	 * @param cName The name of a course.
	 * @param cInstructor The IID of the instructor teaching a course.
	 */
	Course(int cNumber, String cName, int cInstructor){
		System.out.println("Creating new Course");
		callNumber = cNumber;
		
		Connection conn = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...CNC");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...CNC");
		      PreparedStatement stmt = conn.prepareStatement("INSERT INTO course VALUES(?, ?, ?);");
		      stmt.setInt(1, cNumber);
		      stmt.setString(2, cName);
		      stmt.setInt(3, cInstructor);
		      stmt.executeUpdate();
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
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
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement... And fetching existing Course");
		      PreparedStatement stmt = conn.prepareStatement("SELECT CallNumber, CourseName, Instructor FROM course " 
		      + "WHERE CallNumber = ?");
		      stmt.setInt(1, callNumber);
		      ResultSet rs = stmt.executeQuery();

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
	public static void addStudent(Student newStudent){

		Connection conn = null;
		//Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database... in course and adding student");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...in course and adding student");
		      PreparedStatement stmt = conn.prepareStatement("INSERT INTO enrolled VALUES(?, ?);");
		      //System.out.println(sql);
		      stmt.setInt(1, newStudent.getSID());
		      stmt.setInt(2, callNumber);
		      stmt.executeUpdate();
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		      System.out.println("You are already Enrolled.");
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	
	/**
	 * This method is used to create a Class session of a particular course.
	 * @param pin The desired pin for this class.
	 * @param time How long the class should stay open.
	 * @return The created session.
	 */
	public static ClassSession makeNewSession(int pin, int time){
		Course c = new Course(callNumber);
		Calendar time3 = Calendar.getInstance();
		time3.add(Calendar.MINUTE, time);
		ClassSession csesh = new ClassSession(c, time3, pin);
		currentSession = csesh;
		return csesh;
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
	
	/**
	 * This method finds the last session of a given class.
	 * @return The last created class session.
	 */
	public static ClassSession getClassSession(){
		Connection conn = null;
		//Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database... to find class session");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      //System.out.println("Creating statement... and fin");
		      PreparedStatement stmt = conn.prepareStatement("SELECT SessionID FROM classsession " + "WHERE CourseNumber = ?");
		      stmt.setInt(1, callNumber);
		      ResultSet rs = stmt.executeQuery();

		      //STEP 5: Extract data from result set
		      rs.last();
		      ClassSession classSession = new ClassSession(rs.getInt("SessionID"));
		      return classSession;
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      /*try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do*/
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		return null;
	}
}



