// This is SheetsQuickstart. This program is used as a demo to demostrate an update to a fix cell location on a Google sheet
// Todos: Please be sure to update the location of your client_secret.json file & the Googlesheet id before running your program.
// Author: Doan Nguyen
// Date: 6/1/17
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.text.*;
import java.util.Calendar;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.*;
import org.json.*;

public class SheetsQuickstart extends GoogleSheets{
	
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
     * This code was borrowed from https://stackoverflow.com/questions/37495808/java-send-email-via-gmail
     * @param email
     * @param name
     */
    public static void sendReceipt(String email, String subject, String realMessage) {
    	String to = email;  

    	//Get the session object  
    	Properties props = new Properties();
    	props.put("mail.smtp.host", "smtp.gmail.com");
    	props.put("mail.smtp.socketFactory.port", "465");
    	props.put("mail.smtp.socketFactory.class",
    			"javax.net.ssl.SSLSocketFactory");
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.port", "465");

    	Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
    			protected PasswordAuthentication getPasswordAuthentication() {
    				return new PasswordAuthentication("agonz08@gmail.com", "extzhdzabitmjmhm");//change accordingly  
    			}
    		});
    	//compose message  
    	try {
    		MimeMessage message = new MimeMessage(session);
    		message.setFrom(new InternetAddress("ecs@ecs.edu"));//change accordingly  
    		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    		message.setSubject(subject);
    		message.setText(realMessage);
    		//send message  
    		Transport.send(message);
    		System.out.println("message sent successfully");
    	} catch (MessagingException e) {
    		throw new RuntimeException(e);
    	}
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
    
    public static void main(String[] args) throws IOException {
       
        Scanner kb = new Scanner(System.in);
        System.out.println("Please enter your name:");
        String name = kb.nextLine();
        
        if (name.equals("Professor")) {
        	setPin();
        } else if(name.equals("reg")) {
        	//Register.createInstructor(1234, "test@test.edu", "Tester Testersmith", "hi mom");
        	Instructor i = new Instructor(1234);
        	String def = i.getEmail();
        	System.out.println(def);
        	Student s = new Student(1234);
        	int abc = s.getSID();
        	System.out.println(abc);
        } else {
            System.out.println("Please enter the PIN for todays class:");
            String studentPin = kb.nextLine();
            if (studentPin.equals(getPin()) ) {
            	int row = checkStudent(name);
                markAttendance(row, name);
            } else {
            	System.out.println("You have entered an incorrect PIN!");
            }
        }   
        kb.close();
    }
}