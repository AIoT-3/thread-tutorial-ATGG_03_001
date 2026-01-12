package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock을 이용한 고급 동기화 예제입니다.
 * 비전공자 가이드:
 * - ReentrantLock: "자물쇠를 수동으로 잠그고 여는 방식"입니다.
 * - 장점: 락을 얻으려고 시도해보고 안 되면 다른 일을 하거나(tryLock), 
 *   기다리는 시간을 정할 수 있어 더 유연합니다.
 */
@Slf4j
public class ReentrantLockExample {
    // 공정한 자물쇠(객체)를 만듭니다.
    private final ReentrantLock lock = new ReentrantLock();
    private int counter = 0;

    /**
     * 기본적인 락 사용법 (반드시 finally에서 해제해야 함)
     */
    public void increment() {
        lock.lock(); // 자물쇠 잠그기 (누군가 쓰고 있으면 여기서 대기)
        try {
            counter++;
        } finally {
            // 예외가 발생하더라도 자물쇠는 반드시 열어줘야 다른 사람이 쓸 수 있습니다.
            lock.unlock(); 
        }
    }

    /**
     * 락 획득 시도 (안 되면 포기)
     */
    public void tryIncrement() {
        if (lock.tryLock()) { // 자물쇠를 바로 얻을 수 있는지 확인
            try {
                counter++;
                log.info("락을 얻어 성공적으로 숫자를 올렸습니다.");
            } finally {
                lock.unlock();
            }
        } else {
            // 자물쇠를 얻지 못했을 때 기다리지 않고 다른 일을 수행합니다.
            log.info("다른 사람이 쓰고 있네요. 기다리지 않고 다른 일을 합니다.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockExample example = new ReentrantLockExample();

        // 두 작업자가 숫자를 올립니다.
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.increment();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.info("최종 결과: {}", example.counter);
    }
}
