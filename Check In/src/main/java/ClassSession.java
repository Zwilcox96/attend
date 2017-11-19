/*
 * This class creates and manipulates class session objects.
 */
import java.util.Date;
import java.sql.*;
public class ClassSession {
	private static int sessionID;
	private static Course parentCourse;
	private static Date closeTime;
	private static int pin;
	//SQL values
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://attend.ctddylfx9obm.us-west-1.rds.amazonaws.com:3306/attend?autoReconnect=true&useSSL=false";
	static final String USER = "letsstore";
	static final String PASS = "iloveherky";
	
	ClassSession(Course pCourse, Date endTime, int nPin){
		parentCourse = pCourse;
		closeTime = endTime;
		pin = nPin;
		if(getLastID() != 0) {
			sessionID = getLastID() + 1;
		}
		else
		{
			sessionID = parentCourse.getCallNumber()*1000;
		}
	}
	
	public static void attend(Student student, Date timeStamp, int enteredPin){
		Connection conn = null;
		Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		      
		      //STEP 4: Check for appropriate time and pin
		      boolean correct = true;
		      Date curDate = new Date();
		      if(curDate.after(closeTime)){
		    	  correct = false;
		      } else if(enteredPin != pin){
		    	  correct = false;
		      }
		      //STEP 5: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "INSERT INTO attendance VALUES('"+ student.getSID()+"', '" + parentCourse.getCallNumber() +"', '"
		      + sessionID + "', '"+ enteredPin+"', '"+ correct +"');";
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
	
	public static int getLastID(){
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
		      sql = "SELECT SessionID FROM ClassSession " + "WHERE CallNumber = "  + parentCourse.getCallNumber();
		      ResultSet rs = stmt.executeQuery(sql);

		      //STEP 5: Extract data from result set
		      rs.last();
		      return rs.getInt("SessionID");
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
		return 000;
	}
	
}
