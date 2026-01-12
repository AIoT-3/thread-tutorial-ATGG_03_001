package com.nhnacademy.virtual;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;

/**
 * 가상 스레드의 압도적인 처리량(Throughput)을 확인하는 테스트 예제입니다.
 * 비전공자 가이드:
 * - 기존 스레드: 10만 명을 고용하려면 큰 건물이 필요하고 비용이 엄청납니다. (컴퓨터 메모리 부족으로 다운됨)
 * - 가상 스레드: 10만 명을 고용해도 아주 작은 공간만 있으면 됩니다. (아무 문제 없이 돌아감)
 */
@Slf4j
public class VirtualThreadThroughput {
    public static void main(String[] args) {
        // [경고] 아래 주석을 풀고 실행하면 대부분의 컴퓨터가 멈추거나 오류가 납니다.
        /*
        for (int i = 0; i < 100_000; i++) {
            new Thread(() -> { 
                try { Thread.sleep(10000); } catch (InterruptedException e) {} 
            }).start();
        }
        */
        
        log.info("가상 스레드 10만 개 생성 테스트를 시작합니다...");
        long start = System.currentTimeMillis();
        
        // 10만 개의 작업을 처리하는 가상 스레드 전용 실행기를 만듭니다.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100_000; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        // 모든 스레드가 동시에 1초간 쉬게 합니다.
                        Thread.sleep(1000); 
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                });
            }
        } // 여기서 모든 10만 개의 작업이 끝날 때까지 기다립니다.
        
        long end = System.currentTimeMillis();
        // 10만 개의 스레드가 각각 1초씩 쉬었음에도 전체 시간은 1초를 조금 넘는 수준입니다.
        log.info("10만 개의 가상 스레드 작업 완료에 걸린 시간: {}ms", (end - start));
    }
}
