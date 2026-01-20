package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * volatile 키워드를 이용한 변수 가시성(Visibility) 실습입니다.
 * 
 * TODO#3-3: volatile 키워드의 유무에 따라 프로그램의 동작이 어떻게 달라지는지 확인해보세요.
 * 참고 링크: https://www.baeldung.com/java-volatile
 */
@Slf4j
public class VisibilityExercise {
    // TODO#3-3-1: 아래 flag 변수에 volatile을 추가하기 전과 후의 차이를 관찰하세요.
    // volatile이 없으면 메인 스레드에서 변경한 flag 값이 작업 스레드에 즉시 반영되지 않을 수 있습니다.
    private static volatile boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            log.info("작업 스레드 시작 (flag 대기 중...)");

            while (!flag) {
                // flag가 true가 될 때까지 무한 루프
                // 주의: 루프 안에 코드가 없으면 최적화로 인해 flag 값을 다시 읽지 않을 수 있습니다.
            }
            log.info("작업 스레드 종료! (flag가 true로 변경됨)");
        });

        worker.start();

        Thread.sleep(1000); // 1초 대기

        log.info("메인 스레드: flag를 true로 변경합니다.");
        flag = true;

        worker.join();
        log.info("메인 스레드 종료");
    }
}
