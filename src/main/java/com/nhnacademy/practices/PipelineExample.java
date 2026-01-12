package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 파이프라인 패턴(Pipeline Pattern) 예제입니다.
 * 비전공자 가이드:
 * - 파이프라인: "공장의 조립 라인"과 같습니다.
 * - 1단계(부품 만들기) -> 2단계(조립하기) -> 3단계(포장하기)를 
 *   각각 다른 작업자가 맡아서 동시에 진행하면 전체 생산 속도가 올라갑니다.
 */
@Slf4j
public class PipelineExample {
    public static void main(String[] args) {
        // 단계별 사이를 연결하는 컨베이어 벨트(Queue)
        BlockingQueue<String> belt1To2 = new ArrayBlockingQueue<>(10);
        BlockingQueue<String> belt2To3 = new ArrayBlockingQueue<>(10);

        // 1단계: 원재료 준비
        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String material = "원재료-" + i;
                    belt1To2.put(material);
                    log.info("[1단계] {} 준비 완료", material);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // 2단계: 가공 작업
        Thread worker2 = new Thread(() -> {
            try {
                while (true) {
                    String material = belt1To2.take();
                    String product = material + " -> [가공됨]";
                    belt2To3.put(product);
                    log.info("[2단계] {} 처리 중...", material);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        worker2.setDaemon(true); // 프로그램 종료 시 함께 종료되도록 설정
        worker2.start();

        // 3단계: 최종 포장 및 출력
        Thread worker3 = new Thread(() -> {
            try {
                while (true) {
                    String product = belt2To3.take();
                    log.info("[3단계] 최종 결과: {} 포장 완료!", product);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        worker3.setDaemon(true);
        worker3.start();
        
        // 모든 작업이 끝날 때까지 잠시 대기
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
