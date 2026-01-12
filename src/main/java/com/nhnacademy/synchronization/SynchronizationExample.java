package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * synchronized 키워드를 사용한 동기화 예제입니다.
 * 비전공자 가이드:
 * - synchronized: "이 문을 열고 들어가면 나 혼자만 쓸 거야!"라고 선언하는 것과 같습니다.
 * - 문에 걸린 자물쇠(Lock)를 얻은 사람만 안으로 들어갈 수 있습니다.
 */
@Slf4j
public class SynchronizationExample {
    private int counter = 0;

    /**
     * 1. 메소드 전체에 동기화 걸기
     * 한 번에 하나의 스레드만 이 메소드를 실행할 수 있습니다.
     */
    public synchronized void incrementSyncMethod() {
        counter++;
    }

    /**
     * 2. 특정 블록에만 동기화 걸기 (더 권장되는 방식)
     * 필요한 부분에만 자물쇠를 걸어 다른 코드의 실행 속도를 떨어뜨리지 않습니다.
     */
    private final Object lock = new Object(); // 자물쇠 역할을 할 전용 객체
    public void incrementSyncBlock() {
        synchronized (lock) {
            counter++;
        }
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizationExample example = new SynchronizationExample();
        
        // 두 작업자가 서로 다른 방식으로 1,000번씩 더합니다.
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.incrementSyncMethod();
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.incrementSyncBlock();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // 동기화 덕분에 정확히 2,000이 출력됩니다.
        log.info("최종 카운트 결과: {}", example.getCounter());
    }
}
