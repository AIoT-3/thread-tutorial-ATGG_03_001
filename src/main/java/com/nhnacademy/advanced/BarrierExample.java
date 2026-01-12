package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier를 이용한 스레드 동기화 예제입니다.
 * 비전공자 가이드:
 * - CyclicBarrier: "단체 관광 가이드"와 같습니다.
 * - 모든 관광객(스레드)이 약속 장소에 모여야 다음 장소로 이동할 수 있습니다.
 * - 한 명이라도 늦으면 모두가 기다려야 합니다.
 */
@Slf4j
public class BarrierExample {
    public static void main(String[] args) {
        // 3명이 모이면 "출발합니다!"라고 외치는 배리어를 만듭니다.
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            log.info("--- [가이드] 모든 인원이 모였습니다. 이제 출발합니다! ---");
        });

        // 각 관광객이 할 일
        Runnable touristTask = () -> {
            try {
                log.info("{} 가 도착하여 대기 중입니다.", Thread.currentThread().getName());
                // 약속 장소에서 다른 사람들을 기다립니다.
                barrier.await();
                
                // 모두가 모이면 동시에 실행됩니다.
                log.info("{} 가 이동을 시작합니다.", Thread.currentThread().getName());
            } catch (Exception e) {
                log.error("대기 중 오류 발생", e);
                Thread.currentThread().interrupt();
            }
        };

        // 3명의 관광객(스레드) 출발
        new Thread(touristTask, "관광객 1").start();
        new Thread(touristTask, "관광객 2").start();
        new Thread(touristTask, "관광객 3").start();
    }
}
