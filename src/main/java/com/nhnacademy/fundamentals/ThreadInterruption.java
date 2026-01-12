package com.nhnacademy.fundamentals;

import lombok.extern.slf4j.Slf4j;

/**
 * 스레드를 안전하게 중단시키는 방법을 보여주는 예제입니다.
 * 비전공자 가이드:
 * - interrupt(): 작업자에게 "이제 그만하고 퇴근해!"라고 신호를 보내는 것과 같습니다.
 * - 작업자는 이 신호를 받고 하던 일을 안전하게 정리하고 종료해야 합니다.
 */
@Slf4j
public class ThreadInterruption {
    public static void main(String[] args) throws InterruptedException {
        // 1. 작업자 스레드 생성
        Thread worker = new Thread(() -> {
            // isInterrupted(): 누군가 나에게 퇴근 신호를 보냈는지 확인합니다.
            while (!Thread.currentThread().isInterrupted()) {
                log.info("작업자: 열심히 일하는 중...");
                try {
                    // 0.1초 동안 대기
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // sleep 중에 퇴근 신호(interrupt)를 받으면 예외가 발생합니다.
                    log.error("작업자: 자는 동안 퇴근 신호를 받았어요! 하던 일을 정리합니다.");
                    // 중요: InterruptedException이 발생하면 인터럽트 상태가 초기화되므로 
                    // 다시 설정해주거나 루프를 빠져나가야 합니다.
                    Thread.currentThread().interrupt(); 
                }
            }
            log.info("작업자: 안전하게 도구를 정리하고 퇴근합니다.");
        });

        worker.start();
        
        // 메인 스레드가 0.5초 동안 지켜봅니다.
        Thread.sleep(500);
        
        log.info("메인: 이제 작업자에게 퇴근 신호를 보냅니다.");
        // worker 스레드에게 중단 신호를 보냅니다.
        worker.interrupt(); 
    }
}
