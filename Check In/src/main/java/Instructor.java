import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * A class to access professor information
 * Todo: Catch Errors
 */
public class Instructor {
	private static int instructorID;
	private static String name;
	private static String email;
	
	//These variables are for the database connection
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/attend?autoReconnect=true&useSSL=false";
	static final String USER = "root";
	static final String PASS = "password";
	
	
	//make a constructor for a given student
	/*
	 * JDBC code is modified from code originally found on tutorialspoint.com
	 */
	Instructor(int iID){
		instructorID = iID;
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
		      sql = "SELECT IID, IName, IEmail FROM instructor " + "WHERE IID = "  + iID;
		      ResultSet rs = stmt.executeQuery(sql);

		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         name = rs.getString("IName");
		         email = rs.getString("IEmail");
		         //Display values
		         System.out.print("SID: " + iID);
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
		}//end main
		

	
	public static String getEmail(){
		return email;
	}
	
	public static String getName(){
		return name;
	}
	
	public static int getSID(){
		return instructorID;
	}
}
