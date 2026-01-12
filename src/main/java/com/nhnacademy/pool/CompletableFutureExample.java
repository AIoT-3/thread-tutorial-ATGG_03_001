package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture를 이용한 비동기 작업 연결(Chaining) 예제입니다.
 * 비전공자 가이드:
 * - CompletableFuture: "주문 벨"과 같습니다.
 * - 음식을 주문하고 벨을 받은 뒤, 벨이 울리면 다음 단계(음식 받기, 먹기, 반납하기)가 
 *   자동으로 진행되도록 미리 설정해두는 것입니다.
 */
@Slf4j
public class CompletableFutureExample {
    public static void main(String[] args) {
        // 비동기 작업 시작 (별도의 작업자가 처리)
        CompletableFuture.supplyAsync(() -> {
            log.info("1단계: 데이터 가져오기 (시간이 걸리는 작업)");
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            return "원본 데이터";
        }).thenApply(data -> {
            // 1단계 결과가 나오면 자동으로 실행
            log.info("2단계: {} 를 가공합니다.", data);
            return data + " -> 가공된 결과";
        }).thenAccept(result -> {
            // 2단계 결과가 나오면 마지막으로 실행
            log.info("3단계: 최종 결과 출력: {}", result);
        }).join(); // 모든 단계가 끝날 때까지 메인 스레드가 기다립니다.
    }
}
