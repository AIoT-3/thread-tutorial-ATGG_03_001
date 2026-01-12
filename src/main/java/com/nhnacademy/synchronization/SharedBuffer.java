package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;
import java.util.LinkedList;
import java.util.Queue;

/**
 * wait()와 notifyAll()을 이용한 스레드 간 통신 예제입니다.
 * 비전공자 가이드:
 * - wait(): "재료가 없으니 다 될 때까지 잠깐 자고 있을게."
 * - notifyAll(): "재료가 다 준비됐어! 자고 있는 사람들 다 일어나!"
 */
@Slf4j
public class SharedBuffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int CAPACITY = 5; // 창고의 최대 크기

    /**
     * 데이터를 생산하여 창고에 넣습니다.
     */
    public synchronized void produce(int value) throws InterruptedException {
        // 창고가 꽉 찼으면 더 이상 넣을 수 없습니다.
        while (queue.size() == CAPACITY) {
            log.info("창고가 가득 찼습니다. 생산자가 기다립니다...");
            wait(); // 창고에 자리가 생길 때까지 잠듭니다.
        }
        queue.offer(value);
        log.info("생산됨: {}", value);
        
        // 소비자에게 데이터가 들어왔으니 가져가라고 깨웁니다.
        notifyAll(); 
    }

    /**
     * 창고에서 데이터를 꺼내 소비합니다.
     */
    public synchronized int consume() throws InterruptedException {
        // 창고가 비어있으면 가져갈 것이 없습니다.
        while (queue.isEmpty()) {
            log.info("창고가 비었습니다. 소비자가 기다립니다...");
            wait(); // 데이터가 들어올 때까지 잠듭니다.
        }
        int value = queue.poll();
        log.info("소비됨: {}", value);
        
        // 생산자에게 빈 자리가 생겼으니 더 만들어도 된다고 깨웁니다.
        notifyAll(); 
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        SharedBuffer buffer = new SharedBuffer();

        // 생산자 스레드: 10개의 데이터를 만듭니다.
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    buffer.produce(i);
                    Thread.sleep(100); // 만드는 데 걸리는 시간
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 소비자 스레드: 10개의 데이터를 가져갑니다.
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    buffer.consume();
                    Thread.sleep(200); // 소비하는 데 걸리는 시간
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
