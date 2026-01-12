package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * 경쟁 상태(Race Condition)를 시뮬레이션하는 예제입니다.
 * 비전공자 가이드:
 * - 경쟁 상태: 여러 사람이 하나의 통장(공유 자원)에서 동시에 돈을 입금할 때, 
 *   동시에 처리하다가 입금 기록이 누락되는 상황과 같습니다.
 */
@Slf4j
public class RaceConditionExample {
    private int counter = 0;

    /**
     * 카운터를 1씩 증가시킵니다.
     * 비전공자 설명: 이 동작은 내부적으로 '읽기 -> 1 더하기 -> 다시 쓰기'의 3단계로 이루어집니다.
     * 두 스레드가 동시에 '읽기'를 하면, 같은 값을 읽어서 1만 더해지는 문제가 발생합니다.
     */
    public void increment() {
        counter++; 
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        RaceConditionExample example = new RaceConditionExample();

        // 두 명의 작업자(t1, t2)가 각각 10,000번씩 총 20,000번을 더하려고 합니다.
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

        // 동기화 처리를 하지 않았기 때문에 결과가 20,000이 나오지 않을 확률이 매우 높습니다.
        log.info("최종 카운트 결과: {}", example.getCounter());
        log.info("기대했던 결과: 20000");
    }
}
