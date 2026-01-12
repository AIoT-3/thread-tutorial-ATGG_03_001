package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * volatile 키워드를 이용한 가시성(Visibility) 해결 예제입니다.
 * 비전공자 가이드:
 * - 가시성 문제: 한 명이 전등 스위치를 껐는데, 다른 방에 있는 사람은 여전히 켜져 있다고 생각하는 상황입니다. (CPU 캐시 때문)
 * - volatile: "이 변수는 캐시를 쓰지 말고 항상 메인 메모리에서 직접 확인해!"라고 지시하는 것입니다.
 */
@Slf4j
public class VolatileExample {
    // volatile이 없으면 메인 스레드가 stop을 true로 바꿔도 
    // worker 스레드는 자신의 방(CPU 캐시)에 있는 예전 값(false)을 계속 보고 루프를 돌 수 있습니다.
    private static volatile boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            log.info("작업자: 일을 시작합니다...");
            while (!stop) {
                // 매우 바쁘게 돌아가는 루프
            }
            log.info("작업자: 중단 신호를 확인하고 일을 마칩니다.");
        });

        worker.start();
        
        // 1초간 지켜봅니다.
        Thread.sleep(1000);
        
        log.info("메인: 이제 그만하라고 스위치를 켭니다 (stop = true).");
        stop = true;
    }
}
