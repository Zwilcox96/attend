/*
 * This class creates and manipulates class session objects.
 */
import java.util.Calendar;
import java.sql.*;
public class ClassSession {
	private static int sessionID;
	private static Course parentCourse;
	private static Calendar closeTime;
	private static int pin;
	//SQL values
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://attend.ctddylfx9obm.us-west-1.rds.amazonaws.com:3306/attend?autoReconnect=true&useSSL=false";
	static final String USER = "letsstore";
	static final String PASS = "iloveherky";
	
	ClassSession(Course pCourse, Calendar endTime, int nPin){
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
		Connection conn = null;
		Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...in class session");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		      
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...while creating a new class session" + new java.sql.Timestamp(endTime.getTimeInMillis()));
		      stmt = conn.createStatement();
		      String sql;
		      sql = "INSERT INTO classsession VALUES('"+ parentCourse.getCallNumber()+"', '" 
		      + new java.sql.Timestamp(endTime.getTimeInMillis()) +"', '"
		      + sessionID + "', '"+ pin+"');";
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
	
	ClassSession(int inputID){
		sessionID = inputID;
		 Connection conn = null;
		 Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement... while fetching existing class session");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT CourseNumber, Sdate, Pin FROM classsession " + "WHERE SessionID = "  + sessionID;
		      ResultSet rs = stmt.executeQuery(sql);

		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		    	 Course c = new Course(rs.getInt("CourseNumber"));
		         parentCourse = c;
		         Calendar close = Calendar.getInstance();
		         close.setTimeInMillis(rs.getTimestamp("Sdate").getTime());
		         closeTime = close;
		         System.out.println(close.getTime());
		         pin = rs.getInt("Pin");
		         System.out.println(pin);

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
	
	public static void attend(Student student, Calendar timeStamp, int enteredPin){
		Connection conn = null;
		Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		      
		      //STEP 4: Check for appropriate time and pin
		      String message = "";
		      int correct = 1;
		      Calendar curDate = Calendar.getInstance();
		      if(curDate.after(closeTime)){
		    	  correct = 0;
		    	  message = ("Too Late! You checked in at  " + new java.sql.Timestamp(curDate.getTimeInMillis()) 
		    			  + "Instead of " +  new java.sql.Timestamp(closeTime.getTimeInMillis()));
		      } else if(enteredPin != pin){
		    	  correct = 0;
		    	  message = "Incorrect Pin!";
		      }
		      //STEP 5: Execute a query
		      System.out.println("Creating statement... to mark attendance");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "INSERT INTO attendance VALUES('"+ student.getSID()+"', '" + parentCourse.getCallNumber() +"', '"
		      + sessionID + "', '"+ enteredPin+"', '"+ correct +"');";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      String subject = "Attendance for session: " + sessionID;
		      if(correct == 1){
		    	  message = "Thank you for attending class today! %n Today's date: " 
		      +  new java.sql.Timestamp(curDate.getTimeInMillis()) + "%n Today's Session Number: " + sessionID;
		      } else {
		    	  message = message + "%n Today's date: " 
		    		      +  new java.sql.Timestamp(curDate.getTimeInMillis()) + "%n Today's Session Number: " + sessionID;
		      }
		      Messenger m = new Messenger(student.getEmail(), subject, message);
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
	
	public static void override(ClassSession ses, Student S, boolean attended){
		 Connection conn = null;
		   Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      if(attended == true){
			      System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      String sql = "UPDATE attendance " +
			                   "SET correct = 1 WHERE SID = '" + S.getSID() + "' AND SessionID = '" + ses.getSessionID() + "'";
			      stmt.executeUpdate(sql);
		      } else {
		    	  System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      String sql = "UPDATE attendance " +
			                   "SET correct = 0 WHERE SID = " + S.getSID() + "AND SessionID =" + ses.getSessionID() ;
			      stmt.executeUpdate(sql);
		      }
		      
		      
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
		      System.out.println("Connecting to database...in class session ID");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...In Class Session ID");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT SessionID FROM classsession " + "WHERE CourseNumber = "  + parentCourse.getCallNumber();
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
	
	public static int getSessionID(){
		return sessionID;
	}
	
}
