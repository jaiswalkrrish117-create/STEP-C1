package com.gdb.service;

import com.gdb.domain.*;
import com.gdb.exceptions.*;

public class TransferService {

    public TransferService() {
    }

    public void transfer(IAccount from, IAccount to, double amount, int pin)
            throws AccountException {
        if (from == null || to == null) {
            throw new AccountException("Source and destination accounts are required");
        }
        if (!from.isActive() || !to.isActive()) {
            throw new InactiveAccountException("Both accounts must be active to transfer funds");
        }
        if (!from.verifyPin(pin)) {
            throw new InvalidPinException("Incorrect PIN");
        }
        if (!from.canWithdraw(amount)) {
            throw new InsufficientBalanceException("Insufficient balance for transfer of Rs. " + amount);
        }
        // daily limit lives on Account, not IAccount
        Account source = (Account) from;
        source.resetDailyTransferIfNeeded();
        if (!source.canTransfer(amount)) {
            throw new AccountException("Daily transfer limit exceeded. Remaining today: Rs. "
                    + source.getRemainingDailyTransferLimit());
        }
        // every check passed, now the money can move
        from.withdraw(amount, pin);
        to.deposit(amount);
        source.updateDailyTransferTotal(amount);
    }
}
