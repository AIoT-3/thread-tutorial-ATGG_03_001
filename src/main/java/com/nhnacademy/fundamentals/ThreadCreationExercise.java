package com.nhnacademy.fundamentals;

import lombok.extern.slf4j.Slf4j;

/**
 * [실습] Runnable 인터페이스 구현을 통한 스레드 생성
 * 
 * 학습 목표:
 * - Runnable 인터페이스를 구현하는 별도의 클래스를 작성해봅니다.
 * - 구현한 클래스를 사용하여 Thread를 생성하고 실행하는 법을 익힙니다.
 */
@Slf4j
public class ThreadCreationExercise {
    public static void main(String[] args) {
        log.info("메인 스레드 시작: {}", Thread.currentThread().getName());

        // TODO#2-1: 아래에 정의한 MyRunnable 클래스의 인스턴스를 생성하고,
        // 이를 이용해 Thread 객체를 만들어 실행(start) 시키는 코드를 작성하세요.
        // 힌트: Thread thread = new Thread(new MyRunnable());
        // 참고: https://www.baeldung.com/java-runnable-vs-extending-thread

        log.info("메인 스레드 종료");
    }
}

// TODO#2-2: Runnable 인터페이스를 구현하는 MyRunnable 클래스를 아래에 작성해보세요.
// 힌트: 
// class MyRunnable implements Runnable {
//     @Override
//     public void run() {
//         // 로그 출력 등 작업 수행
//     }
// }
