package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch를 이용한 스레드 동기화 실습입니다.
 * 
 * TODO#4-4: CountDownLatch를 사용하여 여러 스레드가 모두 준비될 때까지 
 * 기다렸다가 동시에 작업을 시작하도록 구현하세요.
 * 참고 링크: https://www.baeldung.com/java-countdown-latch
 */
@Slf4j
public class CountDownLatchExercise {
    public static void main(String[] args) throws InterruptedException {
        int workerCount = 3;
        // TODO#4-4-1: workerCount만큼의 숫자를 가진 CountDownLatch를 생성하세요.
        CountDownLatch latch = new CountDownLatch(workerCount);

        for (int i = 0; i < workerCount; i++) {
            int workerId = i;
            new Thread(() -> {
                try {
                    log.info("Worker {} is preparing...", workerId);
                    Thread.sleep((long) (Math.random() * 1000));
                    log.info("Worker {} is ready!", workerId);
                    
                    // TODO#4-4-2: 준비가 완료되었음을 알리기 위해 latch의 숫자를 줄이세요.
                    latch.countDown();
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        log.info("Main thread is waiting for all workers to be ready...");
        
        // TODO#4-4-3: 모든 작업자가 준비될 때까지 기다리세요.
        latch.await();
        
        log.info("All workers are ready! Let's start the main task.");
    }
}
