package com.nhnacademy.virtual;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;

/**
 * Java 21의 혁신적인 기능인 가상 스레드(Virtual Thread) 기초 예제입니다.
 * 비전공자 가이드:
 * - 가상 스레드: "구름처럼 가벼운 스레드"입니다.
 * - 기존 스레드는 무겁고 귀해서 아껴 써야 했지만, 
 *   가상 스레드는 아주 가벼워서 수십만 개를 만들어도 컴퓨터가 힘들어하지 않습니다.
 * - 마치 한 명의 유능한 웨이터가 여러 테이블을 동시에 돌보는 것과 같습니다.
 */
@Slf4j
public class VirtualThreadExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. 단순 생성 및 실행
        // startVirtualThread를 통해 아주 가벼운 작업자를 하나 만듭니다.
        Thread vThread = Thread.startVirtualThread(() -> {
            log.info("가상 스레드에서 인사드립니다! 작업자 정보: {}", Thread.currentThread());
        });
        vThread.join();

        // 2. 가상 스레드 전용 인력 사무소(Executor) 사용 (가장 추천되는 방식)
        // try-with-resources 구문을 사용하면 작업이 끝나고 자동으로 사무소를 닫습니다.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                log.info("인력 사무소를 통해 고용된 가상 스레드가 일을 합니다.");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            });
        }
    }
}
