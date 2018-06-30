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
	private Integer mailingTimeDifference;
	@Value("${mailing.message.quantity}")
	private Integer mailingMessageQuantity;
	@Value("${mailing.threads.amount}")
	private Integer mailingThreadsAmount;
	@Value("${mailing.time.offset}")
	private Integer mailingTimeOffset;
	@Value("${mailing.sendingCount.max}")
	private Integer maxSendingCount;

	/**
	 * Selects notifications from a database with status NEW or IN_PROCESS not older
	 * than mailingTimeDifference with offset mailingTimeOffset (offset means, that
	 * each batch has time for being processed and other thread will not take it
	 * again). Submits tasks to TheadPoolTaskExecutor for sending emails.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Scheduled(cron = "${mailing.interval}")
	public void sendNotification() throws InterruptedException, ExecutionException {
		List<Notification> notificationsList = notificationRepository.findByEmailStatusForProcessing(
				LocalDateTime.now().minusMinutes(mailingTimeDifference),
				LocalDateTime.now().minusMinutes(mailingTimeOffset),
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
			notificationRepository.saveAll(validNotificationsList);

			threadPoolTaskExecutor.setCorePoolSize(threadsAmount);
			getBatches(validNotificationsList, mailingMessageQuantity).forEach(batch -> {
				threadPoolTaskExecutor.submit(() -> {
					processBatchOfNotifications(batch);
				});
			});

		}
	}

	/**
	 * Sends emails for a batch of notifications
	 * 
	 * @param batch
	 */
	private void processBatchOfNotifications(List<Notification> batch) {
		User testUser = new User("vladmartishevskii@gmail.com"); // def_x@ukr.net
		batch.forEach(notification -> {
			if (emailService.sendMessage(testUser.getEmail(), notification.getTitle(), notification.getText())) {
				notification.getEmailInfo().setEmailStatus(EmailStatus.PROCESSED);
				logger.info("Notification with id: {} was succesfully sent to user with id: {} ThreadID: {}",
						notification.getId(), notification.getUserId(), Thread.currentThread().getId());
			} else {
				logger.error("Notification with id: {} wasn't sent to user with id: {}", notification.getId(),
						notification.getUserId());
			}
		});
		notificationRepository.saveAll(batch);
	}

	/**
	 * Changes status of notifications to FAILED, if sending count > maxSendingCount
	 * and status IN_PROCESS
	 */
	@Scheduled(cron = "${mailing.status.checkout.interval}")
	public void changeStatus() {
		List<Notification> notificationsList = notificationRepository.findBySendingCountGreaterThan(maxSendingCount);

		notificationsList.forEach(notification -> {
			notification.getEmailInfo().setEmailStatus(EmailStatus.FAILED);
			logger.error("Status of notification with id: {} was changed to FAILED, sending count: {}",
					notification.getId(), notification.getEmailInfo().getSendingCount());

		});
		notificationRepository.saveAll(notificationsList);
	}

	/**
	 * 
	 * @param source
	 *            The source list to split on batches
	 * @param size
	 *            Size of batch
	 * @return List of batches
	 */
	private List<List<Notification>> getBatches(List<Notification> source, int size) {
		return IntStream.range(0, (source.size() + size - 1) / size)
				.mapToObj(i -> source.subList(i * size, Math.min(source.size(), (i + 1) * size)))
				.collect(Collectors.toList());
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

}
