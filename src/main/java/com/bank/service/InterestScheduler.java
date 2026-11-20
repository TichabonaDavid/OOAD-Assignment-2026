package com.bank.service;

import com.bank.dao.AccountDAO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class InterestScheduler implements AutoCloseable {
    private final AccountDAO accountDAO;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "monthly-interest-scheduler");
        thread.setDaemon(true);
        return thread;
    });

    public InterestScheduler(AccountDAO accountDAO) { this.accountDAO = accountDAO; }

    public void start() { scheduleNextPayment(); }

    private void scheduleNextPayment() {
        LocalDateTime now = LocalDateTime.now();
        YearMonth month = YearMonth.from(now);
        LocalDateTime runAt = month.atEndOfMonth().atTime(23, 59, 0);
        if (!runAt.isAfter(now)) runAt = month.plusMonths(1).atEndOfMonth().atTime(23, 59, 0);
        long delay = Math.max(1, Duration.between(now, runAt).toMillis());
        YearMonth paymentMonth = YearMonth.from(runAt);
        executor.schedule(() -> {
            try {
                int count = accountDAO.payMonthlyInterest(paymentMonth);
                System.out.printf("Monthly interest credited to %d accounts for %s.%n", count, paymentMonth);
            } catch (RuntimeException ex) {
                System.err.println("Monthly interest job failed: " + ex.getMessage());
            } finally {
                scheduleNextPayment();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    @Override public void close() { executor.shutdownNow(); }
}

