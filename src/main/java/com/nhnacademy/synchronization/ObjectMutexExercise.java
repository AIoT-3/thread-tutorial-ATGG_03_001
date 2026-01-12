package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * Object의 wait()와 notify()를 사용하여 커스텀 Mutex를 구현하는 실습입니다.
 */
@Slf4j
public class ObjectMutexExercise {

    /**
     * 간단한 커스텀 뮤텍스 클래스입니다.
     */
    static class SimpleMutex {
        private boolean isLocked = false;
        private final Object lockObject = new Object();

        /**
         * 락을 획득합니다. 이미 락이 걸려있다면 대기합니다.
         */
        public void lock() throws InterruptedException {
            synchronized (lockObject) {
                // TODO: 락이 이미 걸려있는 동안(isLocked == true) wait()를 호출하여 대기하세요.
                // 힌트: while 문을 사용하여 조건이 충족될 때까지 대기해야 합니다.

                // TODO: 락을 획득했으므로 isLocked를 true로 설정하세요.
            }
        }

        /**
         * 락을 해제합니다. 대기 중인 다른 스레드에게 알립니다.
         */
        public void unlock() {
            synchronized (lockObject) {
                // TODO: 락을 해제(isLocked = false)하고 notify()를 호출하여 대기 중인 스레드를 깨우세요.
            }
        }

    }

    private int counter = 0;
    private final SimpleMutex mutex = new SimpleMutex();

    public void increment() {
        try {
            mutex.lock();
            try {
                counter++;
            } finally {
                mutex.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        ObjectMutexExercise exercise = new ObjectMutexExercise();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) exercise.increment();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) exercise.increment();
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.info("최종 결과: {}", exercise.getCounter());
        log.info("기대 결과: 20000");
    }
}
