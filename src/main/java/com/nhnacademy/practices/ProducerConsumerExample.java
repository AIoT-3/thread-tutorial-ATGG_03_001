package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 생산자-소비자 패턴의 실전 예제입니다.
 * 비전공자 가이드:
 * - 생산자(요리사): 음식을 만들어 접시(Queue)에 놓습니다.
 * - 소비자(웨이터): 접시에 있는 음식을 손님에게 서빙합니다.
 * - 접시가 가득 차면 요리사가 쉬고, 접시가 비어 있으면 웨이터가 기다립니다.
 */
@Slf4j
public class ProducerConsumerExample {
    public static void main(String[] args) {
        // 최대 5개의 음식을 담을 수 있는 접시(바구니)
        BlockingQueue<String> tray = new ArrayBlockingQueue<>(5);

        // 생산자 (요리사) 스레드
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    String food = "맛있는 요리 " + i;
                    // 접시에 음식을 놓습니다. (자리가 없으면 대기)
                    tray.put(food);
                    log.info("[요리사] {} 를 만들었습니다. (접시 잔여: {})", food, tray.size());
                    Thread.sleep(200); // 요리하는 시간
                }
            } catch (InterruptedException e) {
                log.error("요리 중단", e);
                Thread.currentThread().interrupt();
            }
        });

        // 소비자 (웨이터) 스레드
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    // 접시에서 음식을 가져갑니다. (비어 있으면 대기)
                    String food = tray.take();
                    log.info("[웨이터] {} 를 서빙합니다. (접시 잔여: {})", food, tray.size());
                    Thread.sleep(500); // 서빙하는 시간 (요리보다 오래 걸림)
                }
            } catch (InterruptedException e) {
                log.error("서빙 중단", e);
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}
