package com.dev.booking.services;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.dev.booking.models.EmailStatus;
import com.dev.booking.models.Notification;
import com.dev.booking.models.User;
import com.dev.booking.repositories.NotificationRepository;

@Component
public class ScheduledTasks {
	private static final Logger logger = LogManager.getLogger(ScheduledTasks.class);
	
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
		List<Notification> notificationsList = notificationRepository
				.findByEmailStatus(
						"NEW",
						"IN_PROCESS",
						LocalDateTime.now().minusMinutes(mailingTimeDifference),
						PageRequest.of(0, mailingMessageQuantity * mailingThreadsAmount)
						);
		
		Integer threadsAmount = calcNumberOfThreads(notificationsList.size());
		if(threadsAmount != 0) {
			ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
			
			List<Notification> validNotificationsList = notificationsList.stream().map(notification -> {
				notification.getEmailInfo().setSendingCount(notification.getEmailInfo().getSendingCount() + 1);
				notification.getEmailInfo().setEmailStatus(EmailStatus.IN_PROCESS);
				return notification;
			}).collect(Collectors.toList());
			notificationRepository.saveAll(validNotificationsList);
			
			if(validNotificationsList.size() > 0) {
				executorService.submit(() -> {
					validNotificationsList.forEach(notification -> {
						//TODO get user email by userId from Administration service
						User testUser = new User("def_x@ukr.net");
						
						if(emailService.sendMessage(testUser.getEmail(), notification.getTitle(), notification.getText())) {
							notification.getEmailInfo().setEmailStatus(EmailStatus.PROCESSED);
							notificationRepository.save(notification);
							
							logger.info("Notification with id: {} succesfully sended to user with id: {}", notification.getId(), notification.getUserId());
						}
						else {
							logger.error("Notification with id: {} not sended to user with id: {}", notification.getId(), notification.getUserId());
						}
					});
				});
			}
		}
	}
	
	@Scheduled(cron="${mailing.status.checkout.interval}")
	public void changeStatus() {
		List<Notification> notificationsList = notificationRepository
				.findByEmailStatus(
						"NEW",
						"IN_PROCESS",
						LocalDateTime.now(),
						PageRequest.of(0, mailingMessageQuantity * mailingThreadsAmount));
		

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
