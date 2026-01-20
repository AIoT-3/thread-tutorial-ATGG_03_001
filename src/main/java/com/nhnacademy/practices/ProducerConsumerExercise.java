package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 실무에서 자주 사용되는 생산자-소비자 패턴 실습입니다.
 * 
 * TODO#7-1: ArrayBlockingQueue를 사용하여 생산자와 소비자가 협업하는 코드를 완성하세요.
 * 참고 링크: https://www.baeldung.com/java-blocking-queue
 */
@Slf4j
public class ProducerConsumerExercise {
    private final BlockingQueue<String> tray;
    private final int capacity;

    public ProducerConsumerExercise(int capacity) {
        tray = new ArrayBlockingQueue<>(capacity);
        this.capacity = capacity;
    }

    public synchronized void put(String item) throws InterruptedException {
        while (tray.size() == capacity) {
            log.info("트레이가 가득 찼습니다.");
            wait();
        }

        tray.add(item);

        notifyAll();
    }

    public synchronized String take() throws InterruptedException {
        while (tray.isEmpty()) {
            log.info("트레이가 비었습니다.");
            wait();
        }

        String value = tray.poll();

        notifyAll();

        return value;
    }

    public static void main(String[] args) {
        // TODO#7-1-1: 크기가 5인 ArrayBlockingQueue를 생성하세요.
        ProducerConsumerExercise exercise = new ProducerConsumerExercise(5);

        // 생산자 (요리사)
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    String food = "Dish " + i;
                    // TODO#7-1-2: tray에 음식을 넣으세요. (큐가 가득 차면 대기해야 함)
                    exercise.put(food);

                    log.info("Producer: Cooked {}", food);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Chef");

        // 소비자 (손님)
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    // TODO#7-1-3: tray에서 음식을 꺼내세요. (큐가 비어있으면 대기해야 함)
                    String food = exercise.take();
                    
                    log.info("Consumer: Ate {}", food);
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Guest");

        producer.start();
        consumer.start();
    }
}
