package com.nhnacademy.fundamentals;

import lombok.extern.slf4j.Slf4j;

/**
 * 스레드의 생명주기(Lifecycle)를 직접 관찰해보는 실습입니다.
 * 
 * TODO#2-4: 아래 가이드에 따라 스레드의 다양한 상태(NEW, RUNNABLE, TIMED_WAITING, TERMINATED)를 출력해보세요.
 * 참고 링크: https://www.baeldung.com/java-thread-lifecycle
 */
@Slf4j
public class ThreadLifecycleExercise {
    public static void main(String[] args) throws InterruptedException {
        // TODO#2-4-1: 1초 동안 잠드는 작업을 수행하는 스레드를 생성하세요. (아직 start는 하지 마세요)
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // TODO#2-4-2: 스레드의 현재 상태를 출력하세요. (예상: NEW)
        log.info("상태 (생성 직후): {}", thread.getState());

        // TODO#2-4-3: 스레드를 시작하고 현재 상태를 출력하세요. (예상: RUNNABLE)
        thread.start();
        log.info("상태 (시작 직후): {}", thread.getState());

        // TODO#2-4-4: 메인 스레드에서 0.5초 대기 후 스레드의 상태를 출력하세요. (예상: TIMED_WAITING)
        Thread.sleep(500);
        log.info("상태 (잠자는 중): {}", thread.getState());

        // TODO#2-4-5: 스레드가 종료될 때까지 기다린 후 상태를 출력하세요. (예상: TERMINATED)
        thread.join();
        log.info("상태 (종료 후): {}", thread.getState());
    }
}
