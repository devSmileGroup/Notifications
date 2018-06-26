package com.dev.booking.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.dev.booking.models.EmailStatus;
import com.dev.booking.models.Notification;
import com.dev.booking.models.User;
import com.dev.booking.repositories.NotificationRepository;

@Component
public class NotificationHandler {
	private static final Logger logger = LogManager.getLogger(NotificationHandler.class);

	@Autowired
	private EmailService emailService;
	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	@Value("${mailing.time.difference}")
	private long mailingTimeDifference;
	@Value("${mailing.message.quantity}")
	private int mailingMessageQuantity;
	@Value("${mailing.threads.amount}")
	private int mailingThreadsAmount;

	/**
	 * Selects notifications from a database with status NEW or IN_PROCESS not older
	 * than mailingTimeDifference
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Scheduled(cron = "${mailing.interval}")
	public void sendNotification() throws InterruptedException, ExecutionException {
		List<Notification> notificationsList = notificationRepository.findByEmailStatusForProcessing(
				LocalDateTime.now().minusMinutes(mailingTimeDifference),
				PageRequest.of(0, mailingMessageQuantity * mailingThreadsAmount));
		Integer threadsAmount = calcNumberOfThreads(notificationsList.size());

		String notificationIds = notificationsList.stream().map(notification -> notification.getId().toString() + "  ")
				.reduce("", String::concat);
		logger.info("Cron task (sendNotification) started. Notifications for processing: {}, count of threads: {}",
				notificationIds, threadsAmount);

		if (threadsAmount != 0) {
			List<Notification> validNotificationsList = notificationsList.stream().map(notification -> {
				notification.getEmailInfo().setSendingCount(notification.getEmailInfo().getSendingCount() + 1);
				notification.getEmailInfo().setEmailStatus(EmailStatus.IN_PROCESS);
				return notification;
			}).collect(Collectors.toList());

			threadPoolTaskExecutor.setCorePoolSize(threadsAmount);
			List<Future> futureTasksList = new ArrayList<>();
			validNotificationsList.forEach(notification -> {
				futureTasksList.add(threadPoolTaskExecutor.submit(() -> {
					processNotification(notification);
				}));
			});

			waitForCompletingTasks(futureTasksList);
			notificationRepository.saveAll(validNotificationsList);
		}
	}

	/**
	 * Waits given tasks to be completed
	 * 
	 * @param futureList
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void waitForCompletingTasks(List<Future> futureList) throws InterruptedException, ExecutionException {
		for (Future future : futureList) {
			future.get();
		}
	}

	/**
	 * Changes status of notifications to FAILED, if sending count > 2
	 */
	@Scheduled(cron = "${mailing.status.checkout.interval}")
	public void changeStatus() {
		List<Notification> notificationsList = notificationRepository.findBySendingCountGreaterThan(2);

		notificationsList.forEach(notification -> {
			notification.getEmailInfo().setEmailStatus(EmailStatus.FAILED);
			logger.error("Status of notification with id: {} was changed to FAILED, sending count: {}",
					notification.getId(), notification.getEmailInfo().getSendingCount());

		});
		notificationRepository.saveAll(notificationsList);
	}

	/**
	 * Calculates number of threads required for processing list of notifications
	 * 
	 * @param notificationsListSize
	 *            size of list for processing
	 * @return
	 */
	private int calcNumberOfThreads(int notificationsListSize) {
		return (int) Math.ceil((float) notificationsListSize / mailingMessageQuantity) < mailingThreadsAmount
				? (int) Math.ceil((float) notificationsListSize / mailingMessageQuantity)
				: mailingThreadsAmount;
	}

	/**
	 * Sends email about notification, changes status to PROCESSED if succeed
	 * 
	 * @param notification
	 *            The notification to be processed
	 * 
	 */
	private void processNotification(Notification notification) {
		User testUser = new User("vladmartishevskii@gmail.com"); // def_x@ukr.net
		if (emailService.sendMessage(testUser.getEmail(), notification.getTitle(), notification.getText())) {
			notification.getEmailInfo().setEmailStatus(EmailStatus.PROCESSED);
			logger.info("Notification with id: {} was succesfully sent to user with id: {} ThreadID: {}",
					notification.getId(), notification.getUserId(), Thread.currentThread().getId());
		} else {
			logger.error("Notification with id: {} wasn't sent to user with id: {}", notification.getId(),
					notification.getUserId());
		}
	}
}
