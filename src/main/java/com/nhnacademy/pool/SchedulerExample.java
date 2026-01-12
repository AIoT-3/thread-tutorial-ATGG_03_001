package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledExecutorService를 이용한 예약 작업 예제입니다.
 * 비전공자 가이드:
 * - 예약 스레드 풀: "알람 시계"와 같습니다.
 * - 정해진 시간 뒤에 실행하거나, 일정한 간격으로 반복해서 실행할 수 있습니다.
 */
@Slf4j
public class SchedulerExample {
    public static void main(String[] args) {
        // 예약 담당 일꾼 한 명을 고용합니다.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 1. 단발성 예약: 2초 뒤에 딱 한 번 실행합니다.
        log.info("지연 작업을 예약합니다 (2초 뒤 실행)");
        scheduler.schedule(() -> log.info("[알람] 2초가 지났습니다!"), 2, TimeUnit.SECONDS);

        // 2. 반복성 예약: 1초 대기 후, 3초마다 계속 실행합니다.
        log.info("반복 작업을 예약합니다 (1초 대기 후 3초 간격)");
        scheduler.scheduleAtFixedRate(() -> {
            log.info("[반복] 현재 시간: {}", System.currentTimeMillis());
        }, 1, 3, TimeUnit.SECONDS);

        // 주의: 메인 스레드가 바로 끝나지 않도록 잠시 유지하거나, 
        // 서비스 중단 시 scheduler.shutdown()을 호출해야 합니다.
    }
}
