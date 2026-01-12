# 6. [실습] 직접 만드는 스레드 풀

이 장에서는 앞서 배운 개념들을 바탕으로 BlockingQueue와 Worker Thread를 활용한 커스텀 스레드 풀을 직접 구현해 봅니다.

## 실습 목표
*   `wait()`와 `notify()`를 이용한 `BlockingQueue`의 원리 이해
*   스레드가 재사용되는 메커니즘 파악
*   자바 동시성 라이브러리의 내부 동작 원리 체득

---

### Step 1. 업무 저장소: `SimpleBlockingQueue` 구현

스레드 풀에 들어온 작업들을 담아둘 바구니가 필요합니다. 큐가 비어있으면 가져가려는 스레드가 기다려야 합니다.

[SimpleBlockingQueue.java](../../src/main/java/com/nhnacademy/workshop/SimpleBlockingQueue.java)

```java
package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 직접 구현해보는 차단 큐(BlockingQueue)입니다.
 * 쉽게 이해하기:
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
```

#### [실습] SimpleBlockingQueue 기능 확장

앞서 만든 `SimpleBlockingQueue`에 데이터 개수를 확인하는 기능을 추가해 봅시다.

[SimpleBlockingQueueExercise.java](../../src/main/java/com/nhnacademy/workshop/SimpleBlockingQueueExercise.java)

### Step 2. 일꾼: `WorkerThread` 구현

스레드 풀 내에서 계속 살아있으면서 큐에서 작업을 꺼내 실행하는 역할을 합니다.

[WorkerThread.java](../../src/main/java/com/nhnacademy/workshop/WorkerThread.java)

```java
package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;

/**
 * 스레드 풀 내부에서 실제로 일을 하는 일꾼(Worker) 스레드입니다.
 * 쉽게 이해하기:
 * - 일꾼 스레드: "대기실에 앉아 있는 직원"과 같습니다.
 * - 바구니(taskQueue)에 할 일(Runnable)이 들어올 때까지 기다립니다.
 * - 할 일이 들어오면 꺼내서 처리하고, 다시 대기실로 돌아와 다음 일을 기다립니다.
 */
@Slf4j
public class WorkerThread extends Thread {
    private final SimpleBlockingQueue<Runnable> taskQueue;

    public WorkerThread(SimpleBlockingQueue<Runnable> queue) {
        this.taskQueue = queue;
    }

    @Override
    public void run() {
        try {
            // 그만하라고 할 때(Interrupt)까지 계속해서 일을 찾습니다.
            while (!Thread.currentThread().isInterrupted()) {
                // 바구니에서 할 일을 꺼냅니다. (일이 없으면 여기서 잠시 대기)
                Runnable task = taskQueue.take(); 
                
                log.info("{} 가 작업을 시작합니다.", getName());
                // 맡은 일을 실제로 수행합니다.
                task.run(); 
                log.info("{} 가 작업을 마쳤습니다.", getName());
            }
        } catch (InterruptedException e) {
            log.info("{} 일꾼이 퇴근합니다. (중단됨)", getName());
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 일 바구니와 일꾼을 준비합니다.
        SimpleBlockingQueue<Runnable> queue = new SimpleBlockingQueue<>(5);
        WorkerThread worker = new WorkerThread(queue);
        worker.setName("성실한-일꾼");
        worker.start();

        // 바구니에 일을 던져줍니다.
        queue.put(() -> log.info("--- [작업] 바닥 청소하기 ---"));
        queue.put(() -> log.info("--- [작업] 창문 닦기 ---"));

        // 잠시 지켜보다가 일꾼을 퇴근시킵니다.
        Thread.sleep(500);
        worker.interrupt();
    }
}
```

### Step 3. 관리자: `MyThreadPool` 완성

[MyThreadPool.java](../../src/main/java/com/nhnacademy/workshop/MyThreadPool.java)

```java
package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * 앞서 만든 바구니와 일꾼을 조합하여 완성한 커스텀 스레드 풀입니다.
 * 비전공자 가이드:
 * - 스레드 풀: "작은 용역 업체"와 같습니다.
 * - 업체 사장님은 정해진 인원(poolSize)의 직원을 미리 채용해 둡니다.
 * - 고객(사용자)이 일을 맡기면 바구니에 넣고, 직원들이 순서대로 처리하게 합니다.
 */
@Slf4j
public class MyThreadPool {
    private final SimpleBlockingQueue<Runnable> taskQueue;
    private final List<WorkerThread> workers = new ArrayList<>();

    /**
     * @param poolSize 채용할 직원 수
     * @param queueCapacity 업무 바구니 크기
     */
    public MyThreadPool(int poolSize, int queueCapacity) {
        taskQueue = new SimpleBlockingQueue<>(queueCapacity);
        
        // 정해진 인원만큼 직원(스레드)을 만들고 일을 시작시킵니다.
        for (int i = 0; i < poolSize; i++) {
            WorkerThread worker = new WorkerThread(taskQueue);
            worker.setName("용역-직원-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    /**
     * 업체에 새로운 업무를 맡깁니다.
     */
    public void execute(Runnable task) throws InterruptedException {
        taskQueue.put(task);
    }

    /**
     * 업체의 모든 직원에게 퇴근을 지시합니다.
     */
    public void shutdown() {
        log.info("모든 직원에게 퇴근을 지시합니다...");
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 직원 2명, 바구니 크기 5인 업체를 차립니다.
        MyThreadPool pool = new MyThreadPool(2, 5);
        
        // 3개의 일을 맡깁니다.
        pool.execute(() -> log.info("--- [업무] A 구역 청소 ---"));
        pool.execute(() -> log.info("--- [업무] B 구역 청소 ---"));
        pool.execute(() -> log.info("--- [업무] C 구역 청소 ---"));

        // 잠시 후 업체를 폐업합니다.
        Thread.sleep(1000);
        pool.shutdown();
    }
}
```

### Step 4. 테스트 및 검증

작성한 스레드 풀이 의도한 대로 동작하는지 확인합니다.

[MyThreadPoolTest.java](../../src/main/java/com/nhnacademy/workshop/MyThreadPoolTest.java)

```java
package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;

/**
 * 직접 만든 커스텀 스레드 풀(MyThreadPool)의 동작을 검증하는 테스트 클래스입니다.
 * 비전공자 가이드:
 * - 테스트 시나리오: 일꾼은 3명인데, 일은 10개를 줍니다.
 * - 어떻게 3명의 직원이 10개의 일을 나눠서 처리하는지 관찰해보세요.
 */
@Slf4j
public class MyThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        // 직원 3명, 바구니 크기 10인 업체를 만듭니다.
        MyThreadPool pool = new MyThreadPool(3, 10);

        log.info("--- [테스트] 10개의 업무를 맡깁니다. ---");
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            pool.execute(() -> {
                log.info("업무 {} 번 처리 중... (담당자: {})", taskId, Thread.currentThread().getName());
                try { 
                    // 한 업무당 0.5초가 걸립니다.
                    Thread.sleep(500); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 모든 업무가 처리될 때까지 넉넉히 기다립니다.
        Thread.sleep(3000);
        
        log.info("--- [테스트] 모든 업무 완료 후 업체 폐업 ---");
        pool.shutdown();
    }
}
```
