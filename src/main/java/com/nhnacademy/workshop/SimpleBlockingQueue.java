package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 직접 구현해보는 차단 큐(BlockingQueue)입니다.
 * 비전공자 가이드:
 * - 차단 큐: "바구니"와 같습니다.
 * - 바구니가 가득 차면 더 넣으려는 사람이 기다려야 하고(Blocking), 
 * - 바구니가 비어 있으면 꺼내려는 사람이 기다려야 합니다.
 */
@Slf4j
public class SimpleBlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public SimpleBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 바구니에 물건을 넣습니다.
     */
    public synchronized void put(T item) throws InterruptedException {
        // 바구니가 꽉 찼는지 확인합니다.
        while (queue.size() == capacity) {
            log.info("바구니가 가득 찼습니다. 자리가 날 때까지 기다립니다...");
            wait(); 
        }
        queue.add(item);
        // 물건을 넣었으니, 기다리던 사람들에게 알립니다.
        notifyAll(); 
    }

    /**
     * 바구니에서 물건을 꺼냅니다.
     */
    public synchronized T take() throws InterruptedException {
        // 바구니가 비었는지 확인합니다.
        while (queue.isEmpty()) {
            log.info("바구니가 비었습니다. 물건이 들어올 때까지 기다립니다...");
            wait(); 
        }
        T item = queue.poll();
        // 물건을 꺼내 빈 자리가 생겼으니, 기다리던 사람들에게 알립니다.
        notifyAll(); 
        return item;
    }

    public static void main(String[] args) throws InterruptedException {
        // 크기가 2인 바구니를 만듭니다.
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(2);

        // 생산자: 5개의 물건을 넣으려고 합니다.
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    log.info("물건 넣기 시도: {}", i);
                    queue.put(i);
                    log.info("물건 넣기 성공: {}", i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 소비자: 5개의 물건을 꺼내려고 합니다.
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Integer val = queue.take();
                    log.info("물건 꺼내기 성공: {}", val);
                    Thread.sleep(300); // 꺼내는 데 시간이 더 걸린다고 가정
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
