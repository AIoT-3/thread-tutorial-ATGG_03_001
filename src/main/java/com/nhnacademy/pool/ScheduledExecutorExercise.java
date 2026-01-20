package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledExecutorService를 이용한 지연 및 반복 작업 실습입니다.
 * 
 * TODO#5-2: ScheduledExecutorService를 사용하여 작업을 지연 실행하거나 
 * 일정한 간격으로 반복 실행하도록 구현하세요.
 * 참고 링크: https://www.baeldung.com/java-executor-service-tutorial
 */
@Slf4j
public class ScheduledExecutorExercise {
    public static void main(String[] args) {
        // TODO#5-2-1: 1개의 스레드를 가진 ScheduledExecutorService를 생성하세요.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        log.info("Tasks are being scheduled...");

        // TODO#5-2-2: 2초 뒤에 "Delayed Task executed!"를 출력하는 작업을 예약하세요.
        scheduler.schedule(() -> log.info("Delayed Task executed!"), 2, TimeUnit.SECONDS);

        // TODO#5-2-3: 1초 대기 후, 3초마다 "Periodic Task executed!"를 출력하는 작업을 예약하세요.
        scheduler.scheduleAtFixedRate(() -> log.info("Periodic Task executed!"), 1, 3, TimeUnit.SECONDS);

        // TODO#5-2-4: 10초 후에 scheduler를 종료(shutdown)하도록 설정하세요.
        scheduler.schedule(() -> {
            log.info("Shutting down Scheduler...");
            scheduler.shutdown();
        }, 10, TimeUnit.SECONDS);
    }
}
