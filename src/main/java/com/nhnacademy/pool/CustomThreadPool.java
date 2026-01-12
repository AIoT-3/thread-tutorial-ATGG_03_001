package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolExecutor를 직접 설정하여 세밀한 스레드 풀을 만드는 예제입니다.
 * 비전공자 가이드:
 * - Core Size: 평소에 유지할 일꾼 수
 * - Max Size: 바쁠 때 최대로 늘릴 수 있는 일꾼 수
 * - Keep Alive: 일이 없어서 노는 임시 일꾼들을 해고하기 전 대기 시간
 * - Work Queue: 일이 밀렸을 때 쌓아두는 대기 공간
 */
@Slf4j
public class CustomThreadPool {
    public static void main(String[] args) {
        // 커스텀 인력 사무소 설정
        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
            2,                      // 평소에 2명의 일꾼을 둡니다.
            4,                      // 최대 4명까지 늘릴 수 있습니다.
            60, TimeUnit.SECONDS,   // 임시 일꾼은 60초간 일이 없으면 퇴근합니다.
            new LinkedBlockingQueue<>(10), // 대기 공간은 10개입니다.
            new ThreadFactory() {   // 일꾼을 새로 뽑는 규칙을 정합니다.
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("우리집-일꾼-" + t.getId());
                    return t;
                }
            },
            // 대기 공간도 꽉 차고 일꾼도 최대치인데 일이 더 들어오면 어떻게 할까요?
            new ThreadPoolExecutor.CallerRunsPolicy() // "너(요청자)가 직접 해!"라고 시킵니다.
        );

        log.info("커스텀 스레드 풀이 생성되었습니다.");
        
        // 사용 후에는 안전하게 닫아줍니다.
        customPool.shutdown();
    }
}
