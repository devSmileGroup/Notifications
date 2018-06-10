package com.dev.booking.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	/*private static final Logger logger = Logger.getLogger(EmailService.class);
	
	@Autowired
	public JavaMailSender emailSender;

	public void sendMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		logger.debug("Sending email message to: " + to + " with subject: " + subject + " and text: " + text);
		emailSender.send(message);
	}*/

}
