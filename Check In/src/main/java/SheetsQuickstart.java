// This is SheetsQuickstart. This program is used as a demo to demostrate an update to a fix cell location on a Google sheet
// Todos: Please be sure to update the location of your client_secret.json file & the Googlesheet id before running your program.
// Author: Doan Nguyen
// Date: 6/1/17
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Date;
import java.text.*;
import java.util.Calendar;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;



public class SheetsQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Sheets API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials//sheets.googleapis.com-java-quickstart.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart.json
     */
    private static final List<String> SCOPES =
        Arrays.asList( SheetsScopes.SPREADSHEETS );

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        // Todo: Change this text to the location where your client_secret.json resided
        InputStream in = new FileInputStream("/Users/agonz/Desktop/Check In Program/Check In/client_secret.json");
            // SheetsQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    /**
     * This code was borrowed from https://www.tutorialspoint.com/java/java_sending_email.htm
     * @param email
     * @param name
     */
    public static void sendReceipt(String email, String name, String subject, String realMessage){
    	String to = email;
    	//change this later
    	String from = "ecs@csus.edu";
    	String host = "Local Host";
    	Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
           // Create a default MimeMessage object.
           MimeMessage message = new MimeMessage(session);

           // Set From: header field of the header.
           message.setFrom(new InternetAddress(from));

           // Set To: header field of the header.
           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

           // Set Subject: header field
           message.setSubject(subject);

           // Now set the actual message
           message.setText(realMessage);

           // Send message
           Transport.send(message);
           //System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
           mex.printStackTrace();
        }
    }
    /**
     * This method marks the time a given student records their attendance for a give class period
     * @param row The row that a students SID is on
     * @throws IOException when a spreadsheet cannot be reached
     */
    public static void markAttendance(int row) throws IOException{
    	 // THIS CREATES A CURRENT TIME STAMP TO BE USED IN SHEETS
   	 String timeStamp = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());
        
   	 // Create requests object
        List<Request> requests = new ArrayList<>();

        // Create values object
        List<CellData> values = new ArrayList<>();
        
 
        values.add(new CellData()
                .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((timeStamp))));
        
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        // Prints the names and majors of students in a sample spreadsheet:
        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
        // Todo: change this text to have your own spreadsheetID
        String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";
        
        /*
    	// Add string 9/12/2016 value
   	 // Prepare request with proper row and column and its value
       requests.add(new Request()
               .setUpdateCells(new UpdateCellsRequest()
                       .setStart(new GridCoordinate()
                               .setSheetId(0)
                               .setRowIndex(0)     // set the row to row 0 
                               .setColumnIndex(6)) // set the new column 6 to value 6/1/2017 at row 0
                       .setRows(Arrays.asList(
                               new RowData().setValues(values)))
                       .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));
       
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
    	        .setRequests(requests);
    	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
    	        .execute();
    	*/
    	List<CellData> valuesNew = new ArrayList<>();
        // Add string 6/21/2016 value
        valuesNew.add(new CellData()
                .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((timeStamp))));
        
        int column = 0;
                        
        try {
        // Prepare request with proper row and column and its value
        requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(0)
                                .setRowIndex(row)     // set the row to the row that contains the students 
                                .setColumnIndex(column)) // set the column to be the column that contains todays attendance 
                        .setRows(Arrays.asList(
                                new RowData().setValues(valuesNew)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
        BatchUpdateSpreadsheetRequest batchUpdateRequestNew = new BatchUpdateSpreadsheetRequest()
    	        .setRequests(requests);
    	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequestNew)
    	        .execute();      
        } catch (IOException e){
    		System.out.println("error");
    	}
        //todo make code that gets names from the google doc and emails from the DB
        String email = null;
        String studentName = null;
        String subject = "Attendence for todays session";
        String message = studentName + ", we have recieved your attendance for today's class at" + timeStamp;
        sendReceipt(email, studentName, subject, message);
    	
       // Prepare request with proper row and column and its value
    	
    	
    }
    public static int updateName(String name) throws IOException{
        // THIS CREATES A CURRENT TIME STAMP TO BE USED IN SHEETS
    	 String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
         
    	 // Create requests object
         List<Request> requests = new ArrayList<>();

         // Create values object
         List<CellData> values = new ArrayList<>();
         
  
         values.add(new CellData()
                 .setUserEnteredValue(new ExtendedValue()
                         .setStringValue((timeStamp))));
         
         // Build a new authorized API client service.
         Sheets service = getSheetsService();

         // Prints the names and majors of students in a sample spreadsheet:
         // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         // Todo: change this text to have your own spreadsheetID
         String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";
         
         
         
    	// ADD NAME TO SHEET
     	List<CellData> valuesName = new ArrayList<>();
     	//String Name = "Alex";

        valuesName.add(new CellData()
                .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((name))));
     	
        requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(0)
                                .setRowIndex(1)     // set the row to row 1 
                                .setColumnIndex(0)) // put the name of the student to the list
                        .setRows(Arrays.asList(
                                new RowData().setValues(valuesName)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
        BatchUpdateSpreadsheetRequest batchUpdateRequestName = new BatchUpdateSpreadsheetRequest()
    	        .setRequests(requests);
    	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequestName)
    	        .execute();        
    	
    	

    
    }

    public static void main(String[] args) throws IOException {
       
        Scanner kb = new Scanner(System.in);
        System.out.println("Please enter your name");
        String name = kb.nextLine();
        
        int row = updateName(name);
        markAttendance(row);
      
    }


}