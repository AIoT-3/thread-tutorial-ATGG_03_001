package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService를 이용한 스레드 풀 기초 예제입니다.
 * 비전공자 가이드:
 * - 스레드 풀: "인력 사무소"와 같습니다.
 * - 일이 생길 때마다 사람을 고용하는 게 아니라, 미리 고용된 일꾼(스레드)들에게 일을 나눠줍니다.
 * - 일꾼은 일을 마치면 돌아와서 다음 일을 기다립니다.
 */
@Slf4j
public class ExecutorServiceExample {
    public static void main(String[] args) {
        // 3명의 일꾼이 대기 중인 인력 사무소(스레드 풀)를 세웁니다.
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 5개의 작업을 인력 사무소에 맡깁니다.
        for (int i = 0; i < 5; i++) {
            int taskId = i;
            executor.execute(() -> {
                log.info("작업 {} 번을 {} 가 처리하고 있습니다.", taskId, Thread.currentThread().getName());
                try {
                    // 일하는 데 시간이 걸린다고 가정합니다.
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 인력 사무소를 닫습니다. (기존에 맡긴 일은 끝까지 처리합니다.)
        executor.shutdown(); 
    }
}
