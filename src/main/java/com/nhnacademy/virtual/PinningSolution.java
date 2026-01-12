package com.nhnacademy.virtual;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 가상 스레드의 Pinning 문제를 ReentrantLock으로 해결하는 예제입니다.
 * 비전공자 가이드:
 * - Pinning: 가상 스레드가 synchronized 블록 안에 있을 때, 
 *   진짜 일꾼(Carrier Thread)까지 같이 멈춰버리는 현상입니다. (효율이 나빠짐)
 * - 해결: synchronized 대신 ReentrantLock을 쓰면, 가상 스레드만 멈추고 
 *   진짜 일꾼은 다른 일을 하러 갈 수 있습니다.
 */
@Slf4j
public class PinningSolution {
    // synchronized 대신 유연한 자물쇠를 사용합니다.
    private final ReentrantLock lock = new ReentrantLock();

    public void safeMethod() {
        lock.lock(); // 자물쇠 잠그기
        try {
            log.info("{} 가 자물쇠를 얻고 잠시 잠을 잡니다.", Thread.currentThread());
            // 여기서 잠을 자더라도 가상 스레드만 멈추고 진짜 일꾼은 해방됩니다.
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            log.info("{} 가 자물쇠를 열었습니다.", Thread.currentThread());
            lock.unlock(); // 자물쇠 풀기
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PinningSolution solution = new PinningSolution();

        // 두 개의 가상 스레드를 동시에 실행합니다.
        Thread v1 = Thread.startVirtualThread(solution::safeMethod);
        Thread v2 = Thread.startVirtualThread(solution::safeMethod);

        v1.join();
        v2.join();
    }
}
