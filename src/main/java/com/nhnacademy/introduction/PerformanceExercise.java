package com.nhnacademy.introduction;

import lombok.extern.slf4j.Slf4j;

/**
 * [실습] 3개의 작업을 병렬로 실행해보기
 * 
 * 학습 목표:
 * - Thread 객체를 생성하고 start(), join()을 사용하는 방법을 익힙니다.
 * - 여러 작업을 동시에 실행했을 때의 성능 이점을 확인합니다.
 */
@Slf4j
public class PerformanceExercise {
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        log.info("--- 병렬 실행 실습 시작 ---");

        // TODO#1-1: 3개의 작업을 병렬로 실행하는 코드를 작성해보세요. (Task 1, 2, 3)
        // 힌트: 
        // 1. Thread t1, t2, t3 객체를 생성하세요. (람다식 사용 권장)
        // 2. 각 스레드 내에서 runTask("Task name")를 호출하세요.
        // 3. 모든 스레드에 대해 start()를 호출하여 실행을 시작하세요.
        // 4. 모든 스레드에 대해 join()을 호출하여 메인 스레드가 기다리게 하세요.
        // 참고: https://www.baeldung.com/java-start-thread
        Thread t1 = new Thread(() -> runTask("t1"));
        Thread t2 = new Thread(() -> runTask("t2"));
        Thread t3 = new Thread(() -> runTask("t3"));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        long end = System.currentTimeMillis();
        log.info("병렬 실행 총 소요 시간: {}ms", (end - start));
        log.info("기대 소요 시간: 약 1000ms (3개의 작업이 동시에 실행되므로)");
    }

    /**
     * 1초가 걸리는 가상의 작업을 수행합니다.
     */
    private static void runTask(String name) {
        try {
            log.info("{} 작업 시작...", name);
            Thread.sleep(1000); // 1초간 멈춤
            log.info("{} 작업 완료!", name);
        } catch (InterruptedException e) {
            log.error("{} 작업 중단됨", name, e);
            Thread.currentThread().interrupt();
        }
    }
}
