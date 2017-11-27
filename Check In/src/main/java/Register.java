/**
 * This class adds students and professors to the MySQL database.
 * @author Zack Wilcox
 * @since 11-15-2017
 */
import java.sql.*;

public class Register {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://attend.ctddylfx9obm.us-west-1.rds.amazonaws.com:3306/attend?autoReconnect=true&useSSL=false";
	static final String USER = "letsstore";
	static final String PASS = "iloveherky";
	
	/**
	 * This method creates a new student and adds them to a database.
	 * The code then email's the student to confirm registration.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param sid The student's ID number
	 * @param name The student's name.
	 * @param email The student's Email address.
	 * @param password The password the student will use to log in.
	 */
	public static void createStudent(int sid, String name, String email, String password){
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
		      sql = "INSERT INTO student VALUES('"+ sid+"', '" + name +"', '"+ email + "', '"+ password+ "');";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      GoogleSheets.updateName(name, sid);
		      Messenger m = new Messenger(email, "Welcome to I'm here!", "Thank you for registering!");
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		      if(se.getSQLState().startsWith("23")){
		    	  System.out.println("A student with this ID already exists.");
		      }
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
	 * This method creates a new instructor and adds them to a database.
	 * The code then email's the instructor to confirm registration.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param iid The ID number of the instructor.
	 * @param email The Email address of the instructor.
	 * @param name The name of the instructor.
	 * @param password The password that the instructor will use to log in.
	 */
	public static void createInstructor(int iid, String email, String name, String password){
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
		      sql = "INSERT INTO instructor VALUES('"+ iid+"', '" + email +"', '"+ name + "', '"+ password+ "');";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      Messenger m = new Messenger(email, "Welcome to I'm here!", "Thank you for registering!");
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		      if(se.getSQLState().startsWith("23")){
		    	  System.out.println("An Instructor with this ID already exists.");
		      }
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
}
