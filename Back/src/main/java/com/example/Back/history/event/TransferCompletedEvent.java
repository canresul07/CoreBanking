package com.example.Back.history.event;

import com.example.Back.transfer.entity.Transfer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferCompletedEvent {
    private final Transfer transfer;
}
