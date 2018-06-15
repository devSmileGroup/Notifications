package com.dev.booking.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dev.booking.models.EmailStatus;
import com.dev.booking.models.Notification;
import com.dev.booking.repositories.NotificationRepository;

@Component
public class ScheduledTasks {
	private static final Logger logger = Logger.getLogger(ScheduledTasks.class);
	private static final int EMAIL_SEND_DELAY_IN_MINUTES = 20; // in minutes
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Scheduled(cron="*/20 * * * * *")
	public void sendNotification() {
		List<Notification> searchedNotifications = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS");
		
		for(Notification notification:searchedNotifications) {
			if(readyForSending(notification.getCreatedDate())) {
				if(notification.getEmailInfo().getStatus() != EmailStatus.IN_PROCESS) {
					notification.getEmailInfo().setSendingCount(notification.getEmailInfo().getSendingCount()+1);
					notification.getEmailInfo().setStatus(EmailStatus.IN_PROCESS);
					notificationRepository.save(notification);
				}
				
				Thread thread = new Thread("NOTIFICATION_THREAD") {
					public void run() {
						int failedCount;
						if(emailService.sendMessage("def_x@ukr.net", notification.getTitle(), notification.getText())) {
							failedCount = 0;
							notification.getEmailInfo().setStatus(EmailStatus.PROCESSED);
							
							logger.debug("send notification "
									+ notification.getTitle()
									+ "to user with id - "
									+ notification.getUserId());
						}
						else {
							failedCount = notification.getEmailInfo().getSendingCount() + 1;
							
							logger.error("Failed to send notification "
									+ notification.getTitle()
									+ "to user with id - "
									+ notification.getUserId());
						}
						notification.getEmailInfo().setSendingCount(failedCount);
						notificationRepository.save(notification);
					}
				};
				thread.start();
			}
		}
	}
	
	@Scheduled(cron="*/25 * * * * *")
	public void changeStatus() {
		List<Notification> list = notificationRepository.findByEmailStatus("NEW", "IN_PROCESS");
		
		for(Notification el:list) {
			if(el.getEmailInfo().getSendingCount() > 2) {
				el.getEmailInfo().setStatus(EmailStatus.FAILED);
				notificationRepository.save(el);
			}
		}
	}
	
	private Boolean readyForSending(Date modified) {
		return (Calendar.getInstance().getTimeInMillis() - modified.getTime()) / (1000 * 1) > EMAIL_SEND_DELAY_IN_MINUTES; // *60
	}
}
