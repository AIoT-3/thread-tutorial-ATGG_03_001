package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;
import java.util.LinkedList;
import java.util.Queue;

/**
 * wait()와 notifyAll()을 이용한 생산자-소비자 패턴 실습입니다.
 * 
 * TODO#3-2: SharedBuffer 클래스를 완성하여 생산자와 소비자가 안전하게 데이터를 주고받도록 하세요.
 * 참고 링크: https://www.baeldung.com/java-wait-notify
 */
@Slf4j
public class SharedBufferExercise {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int CAPACITY = 3;

    /**
     * 데이터를 생산하여 큐에 넣습니다.
     * TODO#3-2-1: synchronized를 적용하고, 큐가 가득 찼을 때 wait()를 호출하도록 구현하세요.
     * 데이터를 넣은 후에는 notifyAll()을 호출해야 합니다.
     */
    public void produce(int value) throws InterruptedException {
        // 여기에 코드를 작성하세요.
    }

    /**
     * 큐에서 데이터를 꺼내 소비합니다.
     * TODO#3-2-2: synchronized를 적용하고, 큐가 비어있을 때 wait()를 호출하도록 구현하세요.
     * 데이터를 꺼낸 후에는 notifyAll()을 호출해야 합니다.
     */
    public int consume() throws InterruptedException {
        // 여기에 코드를 작성하세요.
        return 0;
    }

    public static void main(String[] args) {
        SharedBufferExercise buffer = new SharedBufferExercise();

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.produce(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.consume();
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }
}
