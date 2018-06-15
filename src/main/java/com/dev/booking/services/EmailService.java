package com.dev.booking.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger logger = LogManager.getLogger(EmailService.class);
	
	@Autowired
	public JavaMailSender emailSender;

	public Boolean sendMessage(String to, String subject, String text) {
		try { 
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			logger.debug("Sending email message to: {} with subject {} and text {}", to, subject, text);
			emailSender.send(message);
			return true;
		}
		catch(MailSendException ex) {
			logger.error("Failed to send email to: {} with error {}", to, ex);
			
			return false;
		}
	}

}
