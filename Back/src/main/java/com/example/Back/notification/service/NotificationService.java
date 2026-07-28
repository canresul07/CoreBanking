package com.example.Back.notification.service;

import com.example.Back.history.event.TransferCompletedEvent;
import com.example.Back.account.event.AtmTransactionEvent;
import com.example.Back.card.event.CardApprovedEvent;
import com.example.Back.notification.entity.Notification;
import com.example.Back.notification.repository.NotificationRepository;
import com.example.Back.account.entity.Account;
import com.example.Back.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    @EventListener
    public void handleTransferCompletedEvent(TransferCompletedEvent event) {
        UUID toAccountId = event.getTransfer().getToAccountId();
        Account toAccount = accountRepository.findById(toAccountId).orElse(null);
        
        if (toAccount != null) {
            Notification notification = Notification.builder()
                .userId(toAccount.getUserId())
                .message("Hesabınıza " + event.getTransfer().getAmount() + " TRY para geldi.")
                .isRead(false)
                .build();
            notificationRepository.save(notification);
        }
    }

    @EventListener
    public void handleAtmTransactionEvent(AtmTransactionEvent event) {
        Account account = accountRepository.findById(event.getAccountId()).orElse(null);
        if (account != null) {
            String action = "DEPOSIT".equals(event.getType()) ? "yatırıldı" : "çekildi";
            Notification notification = Notification.builder()
                .userId(account.getUserId())
                .message(String.format("ATM'den %s numaralı hesabınıza %s TRY para %s.", account.getAccountNumber(), event.getAmount(), action))
                .isRead(false)
                .build();
            notificationRepository.save(notification);
        }
    }

    @EventListener
    public void handleCardApprovedEvent(CardApprovedEvent event) {
        Notification notification = Notification.builder()
            .userId(event.getUserId())
            .message(String.format("Sanal kart başvurunuz onaylandı. Kart No: ****%s", event.getCardNumber().substring(event.getCardNumber().length() - 4)))
            .isRead(false)
            .build();
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream().filter(n -> !n.isRead()).toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
