package com.dev.booking.services;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dev.booking.models.EmailStatus;
import com.dev.booking.models.Notification;
import com.dev.booking.models.User;
import com.dev.booking.repositories.NotificationRepository;

@Component
public class ScheduledTasks {
	private static final Logger logger = Logger.getLogger(ScheduledTasks.class);
	private static final int EMAIL_SEND_DELAY_IN_MINUTES = 20; // in minutes
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Value("${mailing.time.difference}")
	private long mailingTimeDifference;
	
	@Value("${mailing.message.quantity}")
	private int mailingMessageQuantity;
	@Value("${mailing.threads.amount}")
	private int mailingThreadsAmount;
	
	@Scheduled(cron="${mailing.interval}")
	public void sendNotification() {
		List<Notification> notificationsList = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS");
		
		Integer threadsAmount = calcNumberOfThreads(notificationsList.size());
		if(threadsAmount != 0) {
			ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
			
			List<Notification> validNotificationsList = new ArrayList<Notification>();
			
			notificationsList.forEach(notification -> {;
				if(LocalDateTime.now().isAfter(notification.getModifiedDate().plusSeconds(mailingTimeDifference))) {
					EmailStatus emailStatus = notification.getEmailInfo().getEmailStatus();
					int sendingCount = notification.getEmailInfo().getSendingCount();
					
					if(emailStatus.equals(EmailStatus.NEW)) {
						notification.getEmailInfo().setEmailStatus(EmailStatus.IN_PROCESS);
					}
					
					notification.getEmailInfo().setSendingCount(++sendingCount);
					notificationRepository.save(notification);
					validNotificationsList.add(notification);
				}
			});
			
			if(validNotificationsList.size() > 0) {
				executorService.submit(() -> {
					validNotificationsList.forEach(notification -> {
						//TODO get user email by userId from Administration service
						User testUser = new User("def_x@ukr.net");
						
						if(emailService.sendMessage(testUser.getEmail(), notification.getTitle(), notification.getText())) {
							notification.getEmailInfo().setEmailStatus(EmailStatus.PROCESSED);
							notificationRepository.save(notification);
							
							logger.info(String.format("Notification with id: %s successfully sended"
									+ " to user with id: %s", notification.getId(), notification.getUserId()));
						}
						else {
							logger.error(String.format("Notification with id: %s not sended"
									+ " to user with id: %s", notification.getId(), notification.getUserId()));
						}
					});
				});
			}
		}
	}
	
	@Scheduled(cron="${mailing.status.checkout.interval}")
	public void changeStatus() {
		List<Notification> notificationsList = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS");
		

		notificationsList.forEach(notification -> {
			if(notification.getEmailInfo().getSendingCount() > 2) {
				notification.getEmailInfo().setEmailStatus(EmailStatus.FAILED);
				notificationRepository.save(notification);
				
				logger.error(String.format("Status of notification with id: %s set to failed, "
						+ " sending count = %s", notification.getId(), notification.getEmailInfo().getSendingCount()));
			}
		});
	}
	

	private int calcNumberOfThreads(int notificationsListSize) {
		return (int) Math.ceil((float)notificationsListSize / mailingMessageQuantity) < mailingThreadsAmount 
				? (int) Math.ceil((float)notificationsListSize / mailingMessageQuantity) 
				: mailingThreadsAmount;
	}
}
