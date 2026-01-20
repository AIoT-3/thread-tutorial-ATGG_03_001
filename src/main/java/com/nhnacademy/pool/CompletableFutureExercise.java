package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture를 이용한 비동기 프로그래밍 실습입니다.
 * 
 * TODO#5-3: CompletableFuture를 사용하여 비동기 작업을 생성하고, 
 * 작업 결과를 가공하여 최종 결과를 출력하는 파이프라인을 구축하세요.
 * 참고 링크: https://www.baeldung.com/java-completablefuture
 */
@Slf4j
public class CompletableFutureExercise {
    public static void main(String[] args) {
        // TODO#5-3-1: supplyAsync를 사용하여 "Hello"를 반환하는 비동기 작업을 만드세요.
        // TODO#5-3-2: thenApply를 사용하여 뒤에 " World"를 추가하세요.
        // TODO#5-3-3: thenAccept를 사용하여 최종 결과를 로그로 출력하세요.
        // TODO#5-3-4: join을 사용하여 모든 작업이 완료될 때까지 기다리세요.

        CompletableFuture.supplyAsync(() -> "Hello")
                .thenApply(data -> data + "World")
                .thenAccept(result -> log.info("{}", result))
                .join();
    }
}
