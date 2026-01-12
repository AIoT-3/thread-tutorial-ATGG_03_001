package com.nhnacademy.introduction;

import lombok.extern.slf4j.Slf4j;

/**
 * 순차 실행과 병렬 실행의 성능 차이를 비교하는 예제입니다.
 * 비전공자 가이드: 
 * - 순차 실행: 한 사람이 일을 하나씩 끝내고 다음 일을 하는 방식
 * - 병렬 실행: 여러 사람이 동시에 각자 맡은 일을 처리하는 방식
 */
@Slf4j
public class PerformanceComparison {
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        log.info("--- 순차 실행 시작 ---");
        // 순차 실행 (Single Thread): Task 1이 끝나야 Task 2가 시작됩니다.
        runTask("Task 1");
        runTask("Task 2");

        long end = System.currentTimeMillis();
        log.info("순차 실행 총 소요 시간: {}ms", (end - start));

        start = System.currentTimeMillis();

        log.info("--- 병렬 실행 시작 ---");
        // 병렬 실행 (Multi Thread): 두 개의 스레드(작업자)를 만들어 동시에 일을 시킵니다.
        Thread t1 = new Thread(() -> runTask("Task 1"));
        Thread t2 = new Thread(() -> runTask("Task 2"));

        // start()를 호출해야 실제로 새로운 실타래(스레드)가 풀리기 시작합니다.
        t1.start();
        t2.start();

        // join()은 이 스레드들이 끝날 때까지 메인(Main) 스레드가 기다리게 합니다.
        // 기다리지 않으면 일이 끝나기도 전에 총 시간을 계산해버립니다.
        t1.join(); 
        t2.join(); 

        end = System.currentTimeMillis();
        log.info("병렬 실행 총 소요 시간: {}ms", (end - start));
    }

    /**
     * 1초가 걸리는 가상의 작업을 수행합니다.
     */
    private static void runTask(String name) {
        try {
            log.info("{} 작업 시작...", name);
            Thread.sleep(1000); // 1초간 멈춤 (작업 중임을 시뮬레이션)
            log.info("{} 작업 완료!", name);
        } catch (InterruptedException e) {
            log.error("{} 작업 중단됨", name, e);
            Thread.currentThread().interrupt();
        }
    }
}
