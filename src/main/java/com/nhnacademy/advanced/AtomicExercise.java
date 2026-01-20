package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [실습] AtomicInteger와 CAS 연산
 * 
 * 학습 목표:
 * - Lock-free 알고리즘의 핵심인 CAS(Compare-And-Swap) 연산을 이해합니다.
 * - AtomicInteger의 다양한 메소드를 활용해봅니다.
 */
@Slf4j
public class AtomicExercise {
    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicExercise exercise = new AtomicExercise();

        // 10,000번씩 증가
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) exercise.increment();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) exercise.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.info("최종 카운트: {}", exercise.getCounter());

        // TODO#4-1: compareAndSet(expect, update) 메소드를 사용하여 
        // counter의 값이 20,000일 경우 0으로 초기화하는 코드를 작성해보세요.
        // 초기화 성공 시 "카운터가 0으로 초기화되었습니다."라고 로그를 출력하세요.
        // 참고: https://www.baeldung.com/java-atomic-variables
        if (exercise.counter.compareAndSet(20000, 0)) {
            log.info("카운터가 0으로 초기화되었습니다.");
        }
        
        log.info("초기화 후 카운트: {}", exercise.getCounter());
    }
}
