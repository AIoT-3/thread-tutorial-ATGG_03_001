package com.nhnacademy.fundamentals;

import lombok.extern.slf4j.Slf4j;

/**
 * 스레드를 생성하는 세 가지 방법을 보여주는 예제입니다.
 * 비전공자 가이드:
 * - Thread 상속: 요리사가 직접 레시피를 몸에 문신함 (클래스 자체가 스레드가 됨)
 * - Runnable 구현: 요리사가 레시피 북을 따로 들고 있음 (할 일과 작업자를 분리함)
 */
@Slf4j
public class ThreadCreation {
    public static void main(String[] args) {
        // 1. Thread 클래스 상속 방식
        // MyThread 클래스는 Thread를 상속받았으므로 그 자체로 스레드 객체입니다.
        Thread myThread = new MyThread();
        myThread.start(); // 새로운 실타래 시작!

        // 2. Runnable 인터페이스 구현 방식 (익명 클래스)
        // Thread라는 '작업자'에게 Runnable이라는 '할 일'을 전달합니다.
        Thread runnableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("방법 2 (익명 클래스): {} 스레드가 일을 합니다.", Thread.currentThread().getName());
            }
        });
        runnableThread.start();

        // 3. Runnable 구현 방식 (람다식 - 현대적이고 가장 권장되는 방식)
        // 코드가 간결하며 '할 일'만 명확하게 보여줍니다.
        Thread lambdaThread = new Thread(() -> {
            log.info("방법 3 (람다식): {} 스레드가 간결하게 일을 합니다.", Thread.currentThread().getName());
        });
        lambdaThread.start();
    }
}

/**
 * Thread 클래스를 상속받아 직접 스레드를 정의합니다.
 */
@Slf4j
class MyThread extends Thread {
    @Override
    public void run() {
        log.info("방법 1 (클래스 상속): {} 스레드가 상속받은 일을 합니다.", Thread.currentThread().getName());
    }
}
