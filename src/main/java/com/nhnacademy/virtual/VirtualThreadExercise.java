package com.nhnacademy.virtual;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [실습] 대량의 가상 스레드 생성 및 성능 확인
 * 
 * 학습 목표:
 * - 가상 스레드의 경량화 특성을 직접 체험합니다.
 * - 수만 개의 스레드를 생성했을 때 시스템의 반응을 관찰합니다.
 */
@Slf4j
public class VirtualThreadExercise {
    public static void main(String[] args) {
        // TODO#A1-1: 가상 스레드 10,000개를 생성하고 실행하는 코드를 작성해보세요.
        // 각 가상 스레드는 1초간 대기(Thread.sleep)한 뒤 종료됩니다.
        // 전체 작업이 완료되는 데 걸리는 총 시간을 측정하여 출력하세요.
        // 힌트: Executors.newVirtualThreadPerTaskExecutor()를 사용하면 편리합니다.
        // 참고: https://www.baeldung.com/java-21-virtual-threads
        log.info("가상 스레드 실습 준비 완료");

        long start = System.currentTimeMillis();

        try (ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10000; i++) {
                threadPool.submit(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        long end = System.currentTimeMillis();

        log.info("수행 시간: {}ms", end - start);
    }
}
