package com.Calling.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to) {

		boolean flag = false;
		String from = "notificaton@dovesoft.ltd";
		String host = "transemail.dove-soft.com";

		Properties properties = System.getProperties();
		System.out.println(properties);

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1:
		Session session = Session.getInstance(properties, new Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication("notificaton@dovesoft.ltd", "10D76927B36A264ABFEE98911B4EA60B37F2");
			}
		});

		session.setDebug(true);

		// step 2:
		MimeMessage m = new MimeMessage(session);

		try {
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);
			// m.setText(message);
			m.setContent(message, "text/html");

			// step 3:
			Transport.send(m);

			System.out.println("sent success....");
			flag = true;

		} catch (MessagingException mex) {
			// Handle specific messaging exceptions
			mex.printStackTrace();
		}

		return flag;
	}

}
