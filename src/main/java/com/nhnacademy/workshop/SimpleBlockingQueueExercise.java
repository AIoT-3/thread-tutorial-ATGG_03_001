package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;
import java.util.LinkedList;
import java.util.Queue;

/**
 * [실습] SimpleBlockingQueue 기능 확장
 * 
 * 학습 목표:
 * - 동기화된 메소드 내에서 상태를 확인하고 로그를 남기는 법을 익힙니다.
 * - wait/notify 메커니즘이 실제 큐 크기에 미치는 영향을 확인합니다.
 */
@Slf4j
public class SimpleBlockingQueueExercise<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public SimpleBlockingQueueExercise(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait(); 
        }
        queue.add(item);
        // 여기에 현재 큐 크기를 로그로 출력하는 코드를 추가하세요.
        notifyAll(); 
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); 
        }
        T item = queue.poll();
        // 여기에 현재 큐 크기를 로그로 출력하는 코드를 추가하세요.
        notifyAll(); 
        return item;
    }

    // TODO#6-1: SimpleBlockingQueue의 현재 데이터 개수를 반환하는 size() 메소드를 추가하세요.
    // 참고: https://www.baeldung.com/java-blocking-queue

    public static void main(String[] args) throws InterruptedException {
        SimpleBlockingQueueExercise<Integer> queue = new SimpleBlockingQueueExercise<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.put(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.take();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}
