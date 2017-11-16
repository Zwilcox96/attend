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
	
	ClassSession(Course pCourse, Date endTime, int nPin){
		parentCourse = pCourse;
		closeTime = endTime;
		pin = nPin;
		//todo: make a sessionID creator.
	}
	
}
