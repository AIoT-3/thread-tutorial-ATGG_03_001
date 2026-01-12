package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * 자원 획득 순서를 강제하여 데드락을 방지하는 예제입니다.
 * 비전공자 가이드:
 * - 해결책: "무조건 왼쪽 젓가락을 먼저 집고 나서 오른쪽을 집어야 한다"는 규칙을 세우는 것입니다.
 * - 모두가 똑같은 순서로만 행동하면, 서로 꼬여서 멈추는 일이 발생하지 않습니다.
 */
@Slf4j
public class DeadlockPrevention {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void safeMethod() {
        // 항상 lock1 -> lock2 순서로만 획득하도록 규칙을 정함
        // 철학자 1도, 2도 모두 이 순서를 따릅니다.
        synchronized (lock1) {
            log.info("{} 가 1번 자물쇠를 얻었습니다.", Thread.currentThread().getName());
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            
            synchronized (lock2) {
                log.info("{} 가 2번 자물쇠까지 얻어 작업을 완료했습니다.", Thread.currentThread().getName());
            }
        }
    }

    public static void main(String[] args) {
        DeadlockPrevention example = new DeadlockPrevention();
        
        // 두 작업자가 동시에 같은 순서로 자원을 요청합니다.
        Thread t1 = new Thread(example::safeMethod, "작업자 1");
        Thread t2 = new Thread(example::safeMethod, "작업자 2");

        log.info("데드락 방지 실험을 시작합니다. 이번에는 멈추지 않고 끝날 것입니다.");
        t1.start();
        t2.start();
    }
}
