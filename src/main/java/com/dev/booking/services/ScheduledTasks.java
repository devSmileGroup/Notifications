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
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Value("${mailing.time.difference}")
	private long mailingTimeDifference;
	
	@Scheduled(cron="${mailing.interval}")
	public void sendNotification() {
		List<Notification> notificationsList = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS");
		
		Integer threadsAmount = (int) Math.ceil((float)notificationsList.size() / 50) < 10 
				? (int) Math.ceil((float)notificationsList.size() / 50) 
				: 10;
		if(threadsAmount != 0) {
			ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
			
			List<Notification> validNotificationsList = new ArrayList<Notification>();
			
			notificationsList.forEach(notification -> {
				if(readyToSend(LocalDateTime.now(), notification.getModifiedDate())) {
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
						//TODO get user email by userId
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
			}
		});
	}
	
	private Boolean readyToSend(LocalDateTime current, LocalDateTime modified) {
		long timeDifference;
		
		timeDifference = ChronoUnit.YEARS.between(modified, current) * 365 * 24 * 60 * 60
				+ ChronoUnit.DAYS.between(modified, current) * 24 * 60 * 60
				+ ChronoUnit.HOURS.between(modified, current) * 60 * 60
				+ ChronoUnit.MINUTES.between(modified, current) * 60
				+ ChronoUnit.SECONDS.between(modified, current);
		
		if(timeDifference >= mailingTimeDifference) {
			return true;
		}
		else {
			return false;
		}
	}
}
