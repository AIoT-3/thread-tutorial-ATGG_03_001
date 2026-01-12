package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;

/**
 * 직접 만든 커스텀 스레드 풀(MyThreadPool)의 동작을 검증하는 테스트 클래스입니다.
 * 비전공자 가이드:
 * - 테스트 시나리오: 일꾼은 3명인데, 일은 10개를 줍니다.
 * - 어떻게 3명의 직원이 10개의 일을 나눠서 처리하는지 관찰해보세요.
 */
@Slf4j
public class MyThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        // 직원 3명, 바구니 크기 10인 업체를 만듭니다.
        MyThreadPool pool = new MyThreadPool(3, 10);

        log.info("--- [테스트] 10개의 업무를 맡깁니다. ---");
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            pool.execute(() -> {
                log.info("업무 {} 번 처리 중... (담당자: {})", taskId, Thread.currentThread().getName());
                try { 
                    // 한 업무당 0.5초가 걸립니다.
                    Thread.sleep(500); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 모든 업무가 처리될 때까지 넉넉히 기다립니다.
        Thread.sleep(3000);
        
        log.info("--- [테스트] 모든 업무 완료 후 업체 폐업 ---");
        pool.shutdown();
    }
}
