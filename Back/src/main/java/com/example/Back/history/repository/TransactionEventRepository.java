package com.example.Back.history.repository;

import com.example.Back.history.entity.TransactionEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import org.springframework.data.mongodb.repository.Aggregation;

@Repository
public interface TransactionEventRepository extends MongoRepository<TransactionEvent, String> {
    List<TransactionEvent> findByFromAccountIdOrderByTimestampDesc(UUID fromAccountId);
    List<TransactionEvent> findByFromAccountIdInOrToAccountIdInOrderByTimestampDesc(List<UUID> fromIds, List<UUID> toIds);

    @Aggregation(pipeline = {
        "{ $group: { _id: null, total: { $sum: '$amount' } } }"
    })
    BigDecimal sumTotalTransactionVolume();
}
