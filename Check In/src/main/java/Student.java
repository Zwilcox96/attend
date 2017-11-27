/**
 * This class is used to create and manipulate students
 * @author Zack Wilcox
 * @since 11-15-2017
 */

import java.sql.*;
public class Student {
	private static int studentID;
	private static String name;
	private static String email;
	
	//These variables are for the database connection
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://attend.ctddylfx9obm.us-west-1.rds.amazonaws.com:3306/attend?autoReconnect=true&useSSL=false";
	static final String USER = "letsstore";
	static final String PASS = "iloveherky";
	
	
	/**
	 * This constructor takes a Student's ID number and creates a Student with info from our database.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param sID The ID number of the Student.
	 */
	Student(int sID){
		studentID = sID;
		 Connection conn = null;
		 //Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      //System.out.println("Creating statement...");
		      PreparedStatement stmt = conn.prepareStatement("SELECT SID, SName, SEmail FROM student " + "WHERE SID = ?");
		      stmt.setInt(1, sID);
		      ResultSet rs = stmt.executeQuery();

		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         name = rs.getString("SName");
		         email = rs.getString("SEmail");

		         //Display values
		         System.out.print("SID: " + sID);
		         System.out.print(", Name: " + name);
		         System.out.println(", Email: " + email);
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
		   //System.out.println("Goodbye!");
		}//end main
		
	/**
	 * This method finds all of the courses s student is taking 
	 * and returns them in an array.
	 * @return An array of courses that this student takes.
	 */
	public static Course[] getCourses(){
		Connection conn = null;
		 //Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      //System.out.println("Creating statement...");
		      PreparedStatement stmt = conn.prepareStatement("SELECT CallNumber FROM enrolled " + "WHERE SID = ?");
		      stmt.setInt(1, studentID);
		      ResultSet rs = stmt.executeQuery();

		      //STEP 5: Extract data from result set and place in an array
		      //Find the size of the required array
		      int size=0;
		      while (rs.next()) {
		          size++;
		      }
		      //Create array for courses and reset the result list pointer.
		      Course[] courses = new Course[size];
		      rs.first();		  
		      //Fill Array with courses
		      int counter = 0;
		      while(rs.next()){
		         //Retrieve by column name
		         Course c = new Course(rs.getInt("CallNumber"));
		         courses[counter] = c;
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		      return courses;
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
	/**
	 * This method finds if a student in enrolled in a course.
	 * @param course The course which has enrollment status checked.
	 * @return A boolean value stating the enrollment status of a student.
	 */
	public static boolean isEnrolled(Course course){
		Connection conn = null;
		//Statement stmt = null;
		
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement... And fetching existing Course");
		      PreparedStatement stmt = conn.prepareStatement("SELECT CallNumber FROM enrolled " + "WHERE SID = ?");
		      stmt.setInt(1, studentID);
		      ResultSet rs = stmt.executeQuery();

		      //STEP 5: Extract data from result set
		      boolean enrolledInClass = false;
		      while(rs.next()){
		         if(rs.getInt("CallNumber")== course.getCallNumber()){
		        	 enrolledInClass = true;
		         }
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		      return enrolledInClass;
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
		      }// nothing we can do
		      */
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   //System.out.println("Goodbye!");
		return true;
	}
	
	
	/**
	 * This method will give you the email of a Student.
	 * @return The email of a Student.
	 */
	public static String getEmail(){
		return email;
	}
	
	/**
	 * This method will give you the name of a student
	 * @return The name of a Student.
	 */
	public static String getName(){
		return name;
	}
	
	/**
	 * This method will give you the SID of a Student.
	 * @return The SID of a Student.
	 */
	public static int getSID(){
		return studentID;
	}
}
