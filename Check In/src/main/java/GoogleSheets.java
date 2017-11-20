/*
 *Google Sheets authorizes the use of google sheets 
 */


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

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
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.Sheets;

public class GoogleSheets {
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
        InputStream in = new FileInputStream("client_secret.json");
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
     * Finds and returns the last column in the spreadsheet
     * @return column
     */
    public static int getColumn() throws IOException{
        // Build a new authorized API client service.
        Sheets service = getSheetsService();
        
        String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";
        
        String range = "A1:Z1";
        
        ValueRange result = service.spreadsheets().values().get(spreadsheetId, range).setMajorDimension("COLUMNS").execute();
        int column = result.getValues() != null ? result.getValues().size() : 0;
        
    	return column;
    }
    
    /**
     * This method will check to see if the student name/id is already listed in the sheet. If the name/id is not found, this 
     * indicates the student has not signed in before and should be added to the first empty row available. If the student is found,
     * then the location on the sheet should be determined in order to correctly mark the attendance.
     * @param name 
     * @return rowNum
     * @throws IOException 
     */
    public static int checkStudent(String name) throws IOException {
    	// Build a new authorized API client service.
        Sheets service = getSheetsService();
        String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";   
        String range = "A2:A50";
        int rowNum = -1;
        Sheets.Spreadsheets.Values.Get request = service.spreadsheets().values().get(spreadsheetId, range);
        ValueRange response = request.execute();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray arr = jsonObject.getJSONArray("values");
        String[] strArr = new String[arr.length()];
        
        // Takes the sheets response and puts the existing student names into a string array.
        for (int i=0; i<strArr.length; i++) {
        	strArr[i] = arr.optString(i);
        }
            
        boolean exists=false;
        
        // Formats student names. Also checks to see if student name already exists.
        for (int i=0; i<strArr.length; i++) {
         strArr[i] = strArr[i].replaceAll("\\[","").replaceAll("\\]", "").replaceAll("\"", "");
        	if(strArr[i].compareToIgnoreCase(name) == 0 ) {
        		    rowNum = i+1;
        		    exists = true;
        	}
        }
        	    
        // If existing student is not found, s/he is asked to enter their student ID to be enrolled in the class.
        if (!exists) {
        	System.out.println("Welcome, " + name + ". Please enter your Student ID to be added to the class.");
            Scanner kb = new Scanner(System.in);
            String studentID = kb.nextLine();
            rowNum = strArr.length + 1;
                
            List<Request> requests = new ArrayList<>();
            List<CellData> valuesName = new ArrayList<>();
            List<CellData> valuesID = new ArrayList<>();

            valuesName.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((name))));
             	
            requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(0)
                                .setRowIndex(rowNum)     // set the row to blank row
                                .setColumnIndex(0))      // set column to 1
                        .setRows(Arrays.asList(
                                new RowData().setValues(valuesName)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
            BatchUpdateSpreadsheetRequest batchUpdateRequestName = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequestName)
                .execute();  
            	
            valuesID.add(new CellData()
                .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((studentID))));
             	
            requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(0)
                                .setRowIndex(rowNum)     // set the row
                                .setColumnIndex(1))      // set column to 1
                        .setRows(Arrays.asList(
                                new RowData().setValues(valuesID)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
            BatchUpdateSpreadsheetRequest batchUpdateRequestID = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequestID)
                .execute();        
             
            updateName(name, rowNum);
            kb.close();
            }
        return rowNum;
    }
    
    /**
     * This code was borrowed from https://stackoverflow.com/questions/37495808/java-send-email-via-gmail
     * Prints the date at the top row
     * @throws IOException
     */
    public static void getDate() throws IOException{
        // THIS CREATES A CURRENT TIME STAMP TO BE USED IN SHEETS
    	 String timeStamp = new SimpleDateFormat("MM/dd/YYYY").format(Calendar.getInstance().getTime());
         int column = getColumn();
    	 // Create requests object
         List<Request> requests = new ArrayList<>();

         // Create values object
         List<CellData> values = new ArrayList<>();
         
  
         values.add(new CellData()
                 .setUserEnteredValue(new ExtendedValue()
                         .setStringValue((timeStamp))));

         // Build a new authorized API client service.
         Sheets service = getSheetsService();

         String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";
         
        requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(0)
                                .setRowIndex(0)     // set the row to row 1 
                                .setColumnIndex(column)) // put the name of the student to the list
                        .setRows(Arrays.asList(
                                new RowData().setValues(values)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
    	        .setRequests(requests);
    	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
    	        .execute();         
    }
    
    /**
     * This method marks the time a given student records their attendance for a give class period
     * @param row The row that a students SID is on
     * @throws IOException when a spreadsheet cannot be reached
     */
    public static void markAttendance(int row, String name) throws IOException{
    	
    	// THIS CREATES A CURRENT TIME STAMP TO BE USED IN SHEETS
   	    String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
   	    // Create requests object
        List<Request> requests = new ArrayList<>();
        // Create values object
        List<CellData> values = new ArrayList<>();
 
        values.add(new CellData()
                .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((timeStamp))));
        
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        List<CellData> valuesNew = new ArrayList<>();
        // Add string of current time in HH:mm:ss format
        valuesNew.add(new CellData()
                .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((timeStamp))));
        
        String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";  

        int column = getColumn() - 1;        
        
        try {
        //getDate();
        // Prepare request to mark a time stamp on the google sheet.
        requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(0)
                                .setRowIndex(row)        // set the row to the row that contains the students 
                                .setColumnIndex(column)) // set the column to be the column that contains todays attendance 
                        .setRows(Arrays.asList(
                                new RowData().setValues(valuesNew)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
        BatchUpdateSpreadsheetRequest batchUpdateRequestNew = new BatchUpdateSpreadsheetRequest()
    	        .setRequests(requests);
    	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequestNew)
    	        .execute();   
    	// Prepare request with proper row and column and its value
        System.out.println("Attendence for todays session:");
    	System.out.println(name + ", we have recieved your attendance for today's class at " + timeStamp);
        //to do: make code that gets names from the google doc and emails from the DB
        String email = "asztechcsus@gmail.com";
        String studentName = name;
        String subject = "Attendence for todays session";
        String message = studentName + ", we have recieved your attendance for today's class at " + timeStamp;
        //sendReceipt(email, subject, message);
        Messenger m = new Messenger(email, subject, message);
        
        } catch (IOException e){
        	//if the get request fails.
    		System.out.println("error");
    	}
    }
    
    /**
     * This method will insert a new student into the google sheets (name and student ID). Returns a 1 indicating
     * this is a valid student.
     * 
     * @param name
     * @param rowNum
     * @return 1
     * @throws IOException
     */
    public static int updateName(String name, int rowNum) throws IOException{

    	 // Create requests object
         List<Request> requests = new ArrayList<>();
         // Build a new authorized API client service.
         Sheets service = getSheetsService();
         String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";
    	// ADD NAME TO SHEET
     	List<CellData> valuesName = new ArrayList<>();

        valuesName.add(new CellData()
                .setUserEnteredValue(new ExtendedValue()
                        .setStringValue((name))));
     	
        requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(0)
                                .setRowIndex(rowNum)     // set the row
                                .setColumnIndex(0)) 	 // set column to 0
                        .setRows(Arrays.asList(
                                new RowData().setValues(valuesName)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
        BatchUpdateSpreadsheetRequest batchUpdateRequestName = new BatchUpdateSpreadsheetRequest()
    	        .setRequests(requests);
    	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequestName)
    	        .execute();        
    	
    	return 1;
   
    }
    
    /**
     * Allows instructor to set a PIN for the class. Student must enter a matching PIN in order to be 
     * marked as present. Currently, hard-coded to D1 cell.
     * @throws IOException
     */
    public static void setPin() throws IOException{
    	
    	Scanner kb = new Scanner(System.in);
    	System.out.println("Please enter a PIN to set for the class");
    	String pin = kb.nextLine();
    	kb.close();
    	
    	// Create requests object
        List<Request> requests = new ArrayList<>();
        // Build a new authorized API client service.
        Sheets service = getSheetsService();
        String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";
    	List<CellData> valuesName = new ArrayList<>();

       valuesName.add(new CellData()
               .setUserEnteredValue(new ExtendedValue()
                       .setStringValue((pin))));
    	
       requests.add(new Request()
               .setUpdateCells(new UpdateCellsRequest()
                       .setStart(new GridCoordinate()
                               .setSheetId(0)
                               .setRowIndex(0)     
                               .setColumnIndex(3))
                       .setRows(Arrays.asList(
                               new RowData().setValues(valuesName)))
                       .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));        
       BatchUpdateSpreadsheetRequest batchUpdateRequestName = new BatchUpdateSpreadsheetRequest()
   	        .setRequests(requests);
   	service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequestName)
   	        .execute();
   	System.out.println("PIN " + pin + " has been set.");        
    }
    
    /**
     * This method will check to see if the student entered a valid PIN.
     * @return pin
     * @throws IOException 
     */
    public static String getPin() throws IOException{
    	Sheets service = getSheetsService();
        String spreadsheetId = "1A-WnepO4dK77xY4AmFU53PnTCxDwpJdkMpXqXYHgAxQ";  
    	String val = "D1";
        Sheets.Spreadsheets.Values.Get request2 = service.spreadsheets().values().get(spreadsheetId, val);
        ValueRange response2 = request2.execute();
        JSONObject pinObject = new JSONObject(response2);
        JSONArray arr2 = pinObject.getJSONArray("values");
        String pin = arr2.optString(0);
        return pin = pin.replaceAll("\\[","").replaceAll("\\]", "").replaceAll("\"", "");
    	
    }
    
}