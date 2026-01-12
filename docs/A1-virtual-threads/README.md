# Appendix A1. Java 21: 가상 스레드 (Virtual Threads) - 경량 스레드

이 장에서는 Java 21의 가장 혁신적인 기능 중 하나인 가상 스레드의 개념과 구조, 그리고 이를 활용하여 처리량을 높이는 방법을 배웁니다.

## 학습 내용

### A1.1 플랫폼 스레드 vs 가상 스레드 (비용 차이)

*   **플랫폼 스레드 (Platform Thread)**: OS 스레드와 1:1로 매핑되는 전통적인 자바 스레드입니다. 생성 비용이 비싸고 메모리를 많이 사용(기본 약 1MB)하여 수천 개 이상 만들기 어렵습니다.
*   **가상 스레드 (Virtual Thread)**: JVM이 관리하는 논리적인 스레드입니다. OS 스레드와 1:N으로 매핑되며, 생성 비용이 매우 저렴하고 메모리 사용량이 극히 적습니다. (수십만~수백만 개 생성 가능)

### A1.2 가상 스레드의 아키텍처 (비유: 유능한 웨이터)

가상 스레드는 실제 실행될 때만 플랫폼 스레드(Carrier Thread)에 올라탑니다. 이를 **'식당의 웨이터'**에 비유하면 이해가 쉽습니다.

*   **전통적인 스레드 (플랫폼 스레드)**: 손님(작업) 한 명당 웨이터가 한 명씩 붙어 있는 구조입니다. 손님이 메뉴를 고민하거나 음식을 먹는 동안(I/O 대기)에도 웨이터는 그 테이블 옆에서 아무것도 못 하고 서 있어야 합니다. 손님이 많아지면 웨이터도 그만큼 늘려야 해서 인건비(메모리)가 감당이 안 됩니다.
*   **가상 스레드 (유능한 웨이터)**: 웨이터는 주문을 받고 주방에 전달한 뒤, 음식이 나올 때까지 다른 테이블에 가서 일을 합니다. 한 명의 유능한 웨이터(Carrier Thread)가 수많은 손님(Virtual Thread)을 동시에 접대하는 방식입니다.

*   **Mount/Unmount**: 가상 스레드가 I/O 작업(네트워크 요청, DB 쿼리 등)을 만나 블로킹되면, JVM은 이 가상 스레드를 플랫폼 스레드에서 내려(Unmount) 버리고 다른 가상 스레드를 올립니다.
*   **효과**: 플랫폼 스레드는 쉬지 않고 계속 일할 수 있어, CPU 자원을 극도로 효율적으로 사용하게 됩니다.

### A1.3 사용법 및 주의사항

[VirtualThreadExample.java](../../src/main/java/com/nhnacademy/virtual/VirtualThreadExample.java)

```java
package com.nhnacademy.virtual;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;

@Slf4j
public class VirtualThreadExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. 단순 생성 및 실행
        Thread vThread = Thread.startVirtualThread(() -> {
            log.info("Hello from Virtual Thread: {}", Thread.currentThread());
        });
        vThread.join();

        // 2. ExecutorService 사용 (추천)
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                log.info("Task running in virtual thread");
            });
        }
    }
}
```

#### [실습] 대량의 가상 스레드 생성 및 성능 확인

가상 스레드를 대량으로 생성하여 성능을 직접 확인해 봅시다.

[VirtualThreadExercise.java](../../src/main/java/com/nhnacademy/virtual/VirtualThreadExercise.java)

*   **주의: Pinning 현상**: `synchronized` 블록 내부에서 I/O 작업을 수행하면 가상 스레드가 플랫폼 스레드에 '고정(Pinned)'되어 효율이 떨어집니다.

[PinningSolution.java](../../src/main/java/com/nhnacademy/virtual/PinningSolution.java)

```java
// Pinning 해결 예시
package com.nhnacademy.virtual;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class PinningSolution {
    private final ReentrantLock lock = new ReentrantLock();

    public void safeMethod() {
        lock.lock();
        try {
            // 가상 스레드만 블로킹되고 Carrier Thread는 다른 일을 할 수 있음
            log.info("{} acquired lock and sleeping...", Thread.currentThread());
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            log.info("{} released lock.", Thread.currentThread());
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PinningSolution solution = new PinningSolution();

        Thread v1 = Thread.startVirtualThread(solution::safeMethod);
        Thread v2 = Thread.startVirtualThread(solution::safeMethod);

        v1.join();
        v2.join();
    }
}
```

### A1.4 가상 스레드를 활용한 처리량(Throughput) 개선

얼마나 많은 가상 스레드를 동시에 돌릴 수 있는지 테스트해 봅니다.

[VirtualThreadThroughput.java](../../src/main/java/com/nhnacademy/virtual/VirtualThreadThroughput.java)

```java
package com.nhnacademy.virtual;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;

@Slf4j
public class VirtualThreadThroughput {
    public static void main(String[] args) {
        // 플랫폼 스레드로 10만 개를 만들면 어떻게 될까요?
        // for (int i = 0; i < 100_000; i++) {
        //     new Thread(() -> { try { Thread.sleep(10000); } catch (InterruptedException e) {} }).start();
        // }
        // -> 대부분의 PC에서 OutOfMemoryError 또는 리소스 부족으로 실행 불가능
        
        long start = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100_000; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        Thread.sleep(1000); // I/O 작업 시뮬레이션
                    } catch (InterruptedException e) {
                        log.error("Task interrupted", e);
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                });
            }
        } // executor.close()에서 모든 작업 완료 대기
        
        long end = System.currentTimeMillis();
        log.info("Time taken for 100,000 tasks: {}ms", (end - start));
    }
}
```
*가상 스레드는 작업을 수행하지 않을 때 플랫폼 스레드(Carrier Thread)를 점유하지 않기 때문에, 수만 개의 동시 접속을 처리하는 서버 시스템에서 혁신적인 성능 향상을 가져옵니다.*
