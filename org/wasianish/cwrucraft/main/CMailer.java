package org.wasianish.cwrucraft.main;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CMailer {
	public static void sendConfirm(String to, String confirm) {
		Properties properties = System.getProperties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(properties,
			new javax.mail.Authenticator() {
		       	protected PasswordAuthentication getPasswordAuthentication() {
		       		return new PasswordAuthentication("cwrucraft@gmail.com", "allofit.");
	            }
			}
		);
		
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("cwrucraft@gmail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to + "@case.edu"));
			message.setSubject("Welcome to CWRUCraft");
			message.setText("Thank you for joining Case's only campus-wide, student run Minecraft server!\n\n"
					+ "CWRUCraft is a place to procrastinate homework, relieve your boredom in class, and excersize your fingers while evading other murderous case students.\n"
					+ "Complete your registration by typing the command:\n\n"
					+ " /confirm " + confirm + "\n\n"
					+ "Don't forget to invite your friends!\n\n"
					+ "Regards,\n"
					+ "   -The CWRUCraft Team\n\n"
					+ "P.S. Since this server is running off of a sketchy computer in my dorm room, the ip address is subject to change.  We will use this email to communicate ip changes and other noteworthy news.\n\n"
					+ "     Interested in how this works, how to get involved, or just to talk? Email us and we'll get in touch");
			Transport.send(message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
