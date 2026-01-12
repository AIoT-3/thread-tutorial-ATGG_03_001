package com.nhnacademy.fundamentals;

import lombok.extern.slf4j.Slf4j;

/**
 * 자바 스레드의 생명주기(상태 변화)를 관찰하는 예제입니다.
 * 비전공자 가이드:
 * - NEW: 아이가 태어났지만 아직 걷지 못함 (객체 생성)
 * - RUNNABLE: 아이가 걷기 시작함 (실행 중 또는 실행 가능)
 * - TIMED_WAITING: 아이가 낮잠을 자는 중 (일정 시간 대기)
 * - TERMINATED: 아이가 일과를 마치고 잠자리에 듬 (종료)
 */
@Slf4j
public class ThreadLifecycle {
    public static void main(String[] args) throws InterruptedException {
        // 1. 새로운 스레드 객체 생성 (아직 시작 안 함)
        Thread thread = new Thread(() -> {
            try {
                // 1초간 잠들기 (TIMED_WAITING 상태가 됨)
                Thread.sleep(1000); 
                synchronized (ThreadLifecycle.class) {
                    // 락을 기다릴 때 BLOCKED 상태가 될 수 있음
                }
            } catch (InterruptedException e) {
                log.error("스레드 오류 발생", e);
                Thread.currentThread().interrupt();
            }
        });

        // NEW 상태 출력
        log.info("상태 1 (생성 직후): {}", thread.getState()); 

        // 2. 스레드 시작
        thread.start();
        // RUNNABLE 상태 출력 (실행 중일 때)
        log.info("상태 2 (시작 직후): {}", thread.getState()); 

        // 스레드가 sleep()에 들어갈 때까지 잠시 대기
        Thread.sleep(500);
        // TIMED_WAITING 상태 출력
        log.info("상태 3 (잠자는 중): {}", thread.getState()); 

        // 스레드가 끝날 때까지 기다림
        thread.join();
        // TERMINATED 상태 출력
        log.info("상태 4 (종료 후): {}", thread.getState()); 
    }
}
