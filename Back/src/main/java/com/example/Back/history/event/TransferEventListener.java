package com.example.Back.history.event;

import com.example.Back.history.entity.TransactionEvent;
import com.example.Back.history.repository.TransactionEventRepository;
import com.example.Back.transfer.entity.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.scheduling.annotation.Async;

@Component
@RequiredArgsConstructor

public class TransferEventListener {

    private final TransactionEventRepository transactionEventRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)

    public void handleTransferCompleted(TransferCompletedEvent event) {
        Transfer transfer = event.getTransfer();
        TransactionEvent transactionEvent = TransactionEvent.builder()
                .transferId(transfer.getId())
                .fromAccountId(transfer.getFromAccountId())
                .toAccountId(transfer.getToAccountId())
                .amount(transfer.getAmount())
                .timestamp(transfer.getCreatedAt())
                .build();
        transactionEventRepository.save(transactionEvent);
    }

    // TODO(Can): Transfer başarılı olduğunda (Transaction commitlendiğinde)
    // MongoDB'ye kayıt atan metodu yaz:
    // 1. @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // anotasyonunu ekle.
    // 2. Metot imzasını public void handleTransferCompleted(TransferCompletedEvent
    // event) şeklinde yaz.
    // 3. (İsteğe bağlı ekstra bilgi) @Async anotasyonu eklenirse MongoDB kayıt
    // işlemi asenkron (arka planda) yapılır. Şimdilik senkron da yapabilirsin.
    // 4. Metot içinde: event.getTransfer() ile transfer bilgisini al.
    // 5. TransactionEvent.builder() ile yeni bir TransactionEvent oluştur ve
    // fieldlarını doldur (timestamp için event.getTransfer().getCreatedAt() veya
    // LocalDateTime.now() kullan).
    // 6. transactionEventRepository.save() ile MongoDB'ye kaydet!

}
