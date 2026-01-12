package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;

/**
 * 데드락(Deadlock, 교착 상태)이 발생하는 상황을 재현한 예제입니다.
 * 비전공자 가이드:
 * - 데드락: "외나무다리에서 만난 두 마리 염소"와 같습니다.
 * - 한 명은 A를 잡고 B를 기다리고, 다른 한 명은 B를 잡고 A를 기다리면서 
 *   서로 영원히 양보하지 않는 상태를 말합니다.
 */
@Slf4j
public class DeadlockExample {
    public static void main(String[] args) {
        // 두 개의 공유 자원 (예: 젓가락 A와 젓가락 B)
        Object resourceA = new Object();
        Object resourceB = new Object();

        // 철학자 1: A를 먼저 집고, 그다음에 B를 집으려 함
        Thread t1 = new Thread(() -> {
            synchronized (resourceA) {
                log.info("철학자 1: 젓가락 A를 집었습니다.");
                try { 
                    // 상대방이 다른 젓가락을 집을 시간을 줍니다.
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log.info("철학자 1: 젓가락 B를 기다리는 중...");
                synchronized (resourceB) {
                    log.info("철학자 1: 젓가락 A, B 모두 집어 식사를 합니다.");
                }
            }
        });

        // 철학자 2: B를 먼저 집고, 그다음에 A를 집으려 함 (t1과 반대 순서)
        Thread t2 = new Thread(() -> {
            synchronized (resourceB) {
                log.info("철학자 2: 젓가락 B를 집었습니다.");
                try { 
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log.info("철학자 2: 젓가락 A를 기다리는 중...");
                synchronized (resourceA) {
                    log.info("철학자 2: 젓가락 A, B 모두 집어 식사를 합니다.");
                }
            }
        });

        log.info("데드락 실험을 시작합니다. 프로그램이 멈출 것입니다.");
        t1.start();
        t2.start();
    }
}
