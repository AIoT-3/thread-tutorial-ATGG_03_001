package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * [실습] synchronized를 이용한 데이터 정합성 확보
 * 
 * 학습 목표:
 * - 경쟁 상태(Race Condition)가 발생하는 원인을 이해합니다.
 * - synchronized 키워드를 적용하여 임계 영역을 보호하는 법을 익힙니다.
 */
@Slf4j
public class RaceConditionExercise {
    private int counter = 0;

    public void increment() {
        // TODO#3-1: 이 메소드에 적절한 동기화(synchronized) 처리를 하여 
        // 여러 스레드가 동시에 호출해도 데이터가 유실되지 않도록 하세요.
        // 참고: https://www.baeldung.com/java-synchronized
        synchronized (this) {
            counter++;
        }
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        RaceConditionExercise exercise = new RaceConditionExercise();

        // 10,000번씩 더하는 두 개의 스레드 생성
        Thread t1 = new Thread(exercise::addValueForLoop);
        Thread t2 = new Thread(exercise::addValueForLoop);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.info("최종 카운트 결과: {}", exercise.getCounter());
        log.info("기대했던 결과: 20000");
        
        if (exercise.getCounter() == 20000) {
            log.info("성공: 데이터 정합성이 유지되었습니다.");
        } else {
            log.error("실패: 데이터가 유실되었습니다.");
        }
    }

    private void addValueForLoop() {
        for (int i = 0; i < 10000; i++) {
            increment();
        }
    }
}
