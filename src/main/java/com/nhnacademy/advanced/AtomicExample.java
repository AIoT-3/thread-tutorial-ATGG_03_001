package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger를 사용한 Lock-free 동기화 예제입니다.
 * 비전공자 가이드:
 * - Atomic(원자적): "더 이상 쪼갤 수 없는 최소 단위"라는 뜻입니다.
 * - 자물쇠(Lock)를 걸지 않고도, 아주 빠르게 숫자를 계산할 수 있는 특수한 도구입니다.
 * - 통장에 돈을 넣을 때 자물쇠로 문을 잠그는 대신, 은행원이 아주 빠른 속도로 한 명씩 처리하는 것과 비슷합니다.
 */
@Slf4j
public class AtomicExample {
    // 원자적 정수 객체 생성
    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        // 내부적으로 CAS(Compare-And-Swap)라는 고도의 기술을 사용하여 
        // 자물쇠 없이도 안전하게 값을 1 증가시킵니다.
        counter.incrementAndGet(); 
    }

    public int getCounter() {
        return counter.get();
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicExample example = new AtomicExample();

        // 10,000번씩 더하는 두 명의 작업자
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) example.increment();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) example.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // 자물쇠 없이도 정확히 20,000이 출력됩니다.
        log.info("최종 원자적 카운트 결과: {}", example.getCounter());
    }
}
