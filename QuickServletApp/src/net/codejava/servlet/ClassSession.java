package net.codejava.servlet;
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
	static final String DB_URL = "jdbc:mysql://attend.ctddylfx9obm.us-west-1.rds.amazonaws.com:3306/attend?autoReconnect"
			+ "=true&useSSL=false";
	static final String USER = "letsstore";
	static final String PASS = "iloveherky";
	
	/**
	 * This constructor creates a new ClassSession.
	 * Data about this ClassSession is stored in a MySQL Database
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param pCourse The Course that this class session belongs to.
	 * @param endTime The number of minutes the ClassSession is meant to be open.
	 * @param nPin The Pin that is required to correctly mark attendance.
	 */
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
		//Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...in class session");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		      		      
		      //STEP 4: Execute a query
		      PreparedStatement stmt = conn.prepareStatement("INSERT INTO classsession VALUES(?, ?, ?, ?);");
		      stmt.setInt(1, parentCourse.getCallNumber());
		      stmt.setTimestamp(2, new java.sql.Timestamp(endTime.getTimeInMillis()));
		      stmt.setInt(3, sessionID);
		      stmt.setInt(4, pin);
		      stmt.executeUpdate();
		      GoogleSheets.getDate();
		      Instructor instructor = parentCourse.getIntsructor();
		      String IEmail = instructor.getEmail();
		      String subject = "Your  new class session has been created.";
		      String message = "Session ID: " + sessionID;
		      Messenger m = new Messenger(IEmail, subject, message);
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
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing*/
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	
	/**
	 * This constructor retrieves an already existing ClassSession.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param inputID The SessionID of the ClassSession that is wanted.
	 */
	ClassSession(int inputID){
		sessionID = inputID;
		 Connection conn = null;
		 //Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement... while fetching existing class session");
		      PreparedStatement stmt = conn.prepareStatement("SELECT CourseNumber, Sdate, Pin FROM classsession " 
		      + "WHERE SessionID = ?");
		      stmt.setInt(1, sessionID);
		      ResultSet rs = stmt.executeQuery();

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
	}
	
	/**
	 * This method marks attendance.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param student The student who is attempting to mark themselves as in class.
	 * @param timeStamp The time that a student attempts to mark themselves in class.
	 * @param enteredPin The pin to be compared with the pin of the session.
	 */
	public static String attend(Student student, Calendar timeStamp, int enteredPin){
		Connection conn = null;
		//Statement stmt = null;
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		      
		      //STEP 4: Check for appropriate enrollment, time, and pin
		      String message = "";
		      int correct = 1;
		      Calendar curDate = Calendar.getInstance();
		      if(!student.isEnrolled(parentCourse)){
		    	  correct = 0;
		    	  message = "You are not enrolled in this course.";
		      }
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
		      PreparedStatement stmt = conn.prepareStatement("INSERT INTO attendance VALUES(?, ?, ?, ?, ?);");
		      stmt.setInt(1, student.getSID());
		      stmt.setInt(2, parentCourse.getCallNumber());
		      stmt.setInt(3, sessionID);
		      stmt.setInt(4, enteredPin);
		      stmt.setInt(5, correct);
		      stmt.executeUpdate();
		      String subject = "Attendance for session: " + sessionID;
		      if(correct == 1){
		    	  int row = GoogleSheets.checkStudent(student.getSID());
		    	  GoogleSheets.markAttendance(row, student.getName());
		    	  message = "Thank you for attending class today! Today's date: " 
		      +  new java.sql.Timestamp(curDate.getTimeInMillis()) + " Today's Session Number: " + sessionID;
		      } else {
		    	  message = message + " Today's date: " 
		    		      +  new java.sql.Timestamp(curDate.getTimeInMillis()) + " Today's Session Number: " + sessionID;
		      }
		      Messenger m = new Messenger(student.getEmail(), subject, message);
		      return message;
		}catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		      System.out.println(se.getSQLState());
		      Calendar curDate = Calendar.getInstance();
		      if(se.getSQLState().startsWith("23")&& pin==enteredPin && curDate.before(closeTime)){
		    	  override(new ClassSession(sessionID), student, true);
		      }
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      /*try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing*/
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		return "Error. You have either already signed up or there is no class session today.";
	}
	
	/**
	 * This method overrides the attendance of a student.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @param session The session where attendance must be changed.
	 * @param student The student which needs an attendance value updated.
	 * @param attended The new attendance value.
	 */
	//todo: make overrides work properly with google sheets
	public static void override(ClassSession session, Student student, boolean attended){
		   Connection conn = null;
		   //Statement stmt = null;
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
			      PreparedStatement stmt = conn.prepareStatement("UPDATE attendance " +
			                   "SET correct = 1 WHERE SID = ? AND SessionID = ?");
			      stmt.setInt(1, student.getSID());
			      stmt.setInt(2, session.getSessionID());
			      stmt.executeUpdate();
			      int row = GoogleSheets.checkStudent(student.getSID());
			      GoogleSheets.markAttendance(row, student.getName(), session.getDate());
		      } else {
		    	  System.out.println("Creating statement...");
		    	  PreparedStatement stmt = conn.prepareStatement("UPDATE attendance " +
		                   "SET correct = 0 WHERE SID = ? AND SessionID = ?");
			      stmt.setInt(1, student.getSID());
			      stmt.setInt(2, session.getSessionID());
			      stmt.executeUpdate();
		      }
		      
		      
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		      if(attended){
		    	  Calendar cal = Calendar.getInstance();
		    	  cal.set(2000, 2, 1);
		    	  session.attend(student, cal, session.getPin());
		      }
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      /*try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing*/
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	}
	
	/**
	 * Find the sessionID of the last session in the same course.
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 * @return The sessionID of the last session in the same course.
	 */
	public static int getLastID(){
		Connection conn = null;
		//Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...in class session ID");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...In Class Session ID");
		      PreparedStatement stmt = conn.prepareStatement("SELECT SessionID FROM classsession " 
		      + "WHERE CourseNumber = ?");
		      stmt.setInt(1, parentCourse.getCallNumber());
		      ResultSet rs = stmt.executeQuery();

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
		return 000;
	}
	
	/**
	 * This method gets the SessionID of the current session
	 * @return The sessionID
	 */
	public static int getSessionID(){
		return sessionID;
	}
	
	/**
	 * This method returns the closing time of a given session.
	 * @return The closing time of a session in the calendar format.
	 */
	public static Calendar getDate(){
		return closeTime;
	}
	private static int getPin(){
		return pin;
	}
}
