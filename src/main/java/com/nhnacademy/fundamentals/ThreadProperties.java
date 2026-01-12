package com.nhnacademy.fundamentals;

import lombok.extern.slf4j.Slf4j;

/**
 * 스레드의 속성(이름, 우선순위)을 설정하고 확인하는 예제입니다.
 * 비전공자 가이드:
 * - 이름: 작업자에게 이름표를 달아주는 것과 같습니다. (디버깅 시 매우 중요!)
 * - 우선순위: 작업자들 중 누가 더 급한 일을 하는지 OS에게 힌트를 줍니다.
 */
@Slf4j
public class ThreadProperties {
    public static void main(String[] args) {
        // 스레드가 할 일 정의
        Thread thread = new Thread(() -> {
            log.info("작업자 이름: {}", Thread.currentThread().getName());
            log.info("작업자 우선순위: {}", Thread.currentThread().getPriority());
        });

        // 1. 스레드 이름 설정 (달지 않으면 Thread-0 같은 기본 이름이 붙음)
        thread.setName("Custom-Worker-01");

        // 2. 우선순위 설정 (1 ~ 10, 기본값은 5)
        // 주의: OS마다 동작이 다를 수 있어 절대적인 순서를 보장하지는 않습니다.
        thread.setPriority(Thread.MAX_PRIORITY); // 가장 높은 우선순위(10)

        // 3. 실행
        thread.start();
    }
}
