import java.io.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class Messenger {
	
	public Messenger(String email, String subject, String realMessage) {
    
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
    				return new PasswordAuthentication("asztechcsus@gmail.com", "xK2-3Rb-tYr-NZ7");//change accordingly  
    			}
    		});
    	
    	//compose message  
    	try {
    		MimeMessage message = new MimeMessage(session);
    		message.setFrom(new InternetAddress("ecs@ecs.edu"));  
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
}