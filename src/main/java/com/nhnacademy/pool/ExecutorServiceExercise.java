package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * [실습] CachedThreadPool 활용
 * 
 * 학습 목표:
 * - 작업 부하에 따라 스레드 수가 동적으로 변하는 CachedThreadPool의 특징을 이해합니다.
 * - ExecutorService를 이용해 다수의 작업을 효율적으로 실행해봅니다.
 */
@Slf4j
public class ExecutorServiceExercise {
    public static void main(String[] args) {
        // TODO#5-1: Executors.newCachedThreadPool()을 사용하여 스레드 풀을 생성하고, 
        // 10개의 작업을 실행해보세요. 
        // 각 작업은 약 100ms 정도 대기(Thread.sleep)한 뒤, 자신의 스레드 이름을 로그로 남깁니다.
        // 모든 작업이 끝난 후에는 스레드 풀을 shutdown() 해야 합니다.
        // 참고: https://www.baeldung.com/java-executors-types
        try (ExecutorService threadPool = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 10; i++) {
                int taskNum = i + 1;

                threadPool.submit(() -> {
                    log.info("{}: {}번 작업 시작", Thread.currentThread().getName(), taskNum);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    log.info("{}: {}번 작업 종료", Thread.currentThread().getName(), taskNum);
                });
            }
        }

        log.info("모든 작업이 등록되었습니다.");
    }
}
