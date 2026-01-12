package com.nhnacademy.fundamentals;

import lombok.extern.slf4j.Slf4j;

/**
 * [실습] 스레드 안전하게 중단하기
 * 
 * 학습 목표:
 * - interrupt() 신호를 처리하는 방법을 익힙니다.
 * - 루프 내에서 안전하게 종료 조건을 체크하는 법을 배웁니다.
 */
@Slf4j
public class ThreadInterruptionExercise {
    public static void main(String[] args) throws InterruptedException {
        // TODO#2-3: 10초 동안 1초 간격으로 숫자를 출력하는 스레드를 만드세요.
        // 메인 스레드에서 3초 뒤에 해당 스레드를 중단(interrupt)시키고 
        // 스레드가 "작업이 중단되었습니다."를 출력하며 안전하게 종료되는지 확인하세요.
        // 힌트: 
        // 1. while (!Thread.currentThread().isInterrupted()) 루프를 사용하세요.
        // 2. Thread.sleep() 시 발생하는 InterruptedException을 catch하여 interrupt 상태를 다시 설정하세요.
        // 참고: https://www.baeldung.com/java-thread-stop

        log.info("메인: 3초 대기 중...");
        Thread.sleep(3000);

        log.info("메인: 작업 중단 신호를 보냅니다.");
        // 여기에 스레드 중단 코드를 작성하세요.
    }
}
