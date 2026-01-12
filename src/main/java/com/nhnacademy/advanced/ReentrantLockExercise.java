package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * [실습] ReentrantLock의 tryLock 활용
 * 
 * 학습 목표:
 * - ReentrantLock을 사용하여 명시적으로 락을 제어하는 법을 배웁니다.
 * - tryLock을 이용해 락 획득을 시도하고 타임아웃을 처리하는 법을 익힙니다.
 */
@Slf4j
public class ReentrantLockExercise {
    private final ReentrantLock lock = new ReentrantLock();
    private int counter = 0;

    public void longTask() {
        lock.lock();
        try {
            log.info("{} 가 락을 획득하여 2초간 작업을 수행합니다.", Thread.currentThread().getName());
            Thread.sleep(2000); 
            counter++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            log.info("{} 가 락을 해제했습니다.", Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockExercise exercise = new ReentrantLockExercise();

        // 첫 번째 스레드가 락을 오래 잡고 있습니다.
        Thread t1 = new Thread(exercise::longTask, "Worker-1");
        t1.start();

        // 메인 스레드에서 약간 대기 후 두 번째 스레드 시작
        Thread.sleep(100);

        // TODO#4-2: tryLock(long time, TimeUnit unit)을 사용하여 
        // 1초 동안 락을 획득하려고 시도하고, 획득에 실패하면 "락 획득 시간 초과" 메시지를 출력하는 코드를 작성해보세요.
        // 획득 성공 시에는 counter를 1 증가시키고 락을 해제해야 합니다.
        // 참고: https://www.baeldung.com/java-concurrent-locks
        Thread t2 = new Thread(() -> {
            log.info("Worker-2 가 락 획득을 시도합니다.");
            // 여기에 작성하세요.
        }, "Worker-2");

        t2.start();
        t1.join();
        t2.join();

        log.info("최종 카운트: {}", exercise.counter);
    }
}
