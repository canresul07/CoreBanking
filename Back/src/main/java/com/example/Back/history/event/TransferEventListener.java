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
                .status(transfer.getStatus())
                .idempotencyKey(transfer.getIdempotencyKey())
                .timestamp(transfer.getCreatedAt())
                .build();
        transactionEventRepository.save(transactionEvent);
    }
}
