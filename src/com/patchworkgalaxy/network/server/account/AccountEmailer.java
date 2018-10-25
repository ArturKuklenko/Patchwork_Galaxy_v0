package com.patchworkgalaxy.network.server.account;

import com.patchworkgalaxy.PatchworkGalaxy;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class AccountEmailer {
    
    private AccountEmailer() {}
    
    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;
    private static final String SMTP_AUTH_USER = "donotreply@hailstormstudios.net";
    private static final String SMTP_AUTH_PWD  = "xundypxdvnucyevs";
    
    static void sendEmail(final String recipient, final String subject, final String body) {
	(new Thread(new Runnable() {
	    @Override
	    public void run() {
		sendEmail0(recipient, subject, body);
	    }
	})).start();
    }
    
    private static void sendEmail0(String recipient, String subject, String body) {
	try {
	    Properties props = new Properties();
	    props.put("mail.transport.protocol", "smtps");
	    props.put("mail.smtps.host", SMTP_HOST_NAME);
	    props.put("mail.smtps.port", SMTP_HOST_PORT);
	    props.put("mail.smtps.user", SMTP_AUTH_USER);
	    props.put("mail.smtps.password", SMTP_AUTH_PWD);
	    props.put("mail.smtps.auth", "true");

	    Session mailSession = Session.getDefaultInstance(props);
	    mailSession.setDebug(true);
	    Transport transport = mailSession.getTransport();

	    MimeMessage message = new MimeMessage(mailSession);
	    message.setSubject(subject);
	    message.setContent(body, "text/plain");

	    message.addRecipient(Message.RecipientType.TO,
		 new InternetAddress(recipient));

	    transport.connect
	      (SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);

	    transport.sendMessage(message,
		message.getRecipients(Message.RecipientType.TO));
	    transport.close();
	}
	catch(Exception e) {
	    PatchworkGalaxy.writeException(e);
	}
    }
    
}
