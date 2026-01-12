# 7. 실전 예제 및 모범 사례

이 장에서는 멀티스레드 프로그래밍 시 자주 발생하는 문제들과 이를 해결하기 위한 패턴 및 디버깅 팁을 배웁니다.

## 학습 내용

### 7.1 자주 발생하는 오류와 해결책

1.  **Race Condition (경쟁 상태)**: 앞서 배운 동기화 기법(`synchronized`, `Lock`, `Atomic`)을 적절히 사용하여 해결합니다.
2.  **Visibility (가시성) 문제**: 한 스레드가 변경한 값이 다른 스레드에게 즉시 보이지 않는 현상입니다.
    *   **해결**: `volatile` 키워드를 사용하거나 동기화 블록을 통해 CPU 캐시가 아닌 메인 메모리에서 값을 읽도록 강제합니다.
3.  **Liveness 문제**: 스레드가 살아서 동작하고는 있지만, 진행이 안 되는 상태입니다. (Deadlock, Livelock, Starvation)

#### [예제] 데드락(Deadlock) 상황 재현

[DeadlockExample.java](../../src/main/java/com/nhnacademy/practices/DeadlockExample.java)

```java
package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeadlockExample {
    public static void main(String[] args) {
        Object resourceA = new Object();
        Object resourceB = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (resourceA) {
                log.info("Thread 1: Locked A");
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (resourceB) {
                    log.info("Thread 1: Locked B");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (resourceB) { // t1과 반대 순서로 락 획득 시도
                log.info("Thread 2: Locked B");
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (resourceA) {
                    log.info("Thread 2: Locked A");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
```

*   **방지 전략**:
    1.  **락 획득 순서 정하기**: 항상 리소스 A -> B 순서로 락을 얻게 합니다.
    2.  **타임아웃 사용**: `tryLock`을 사용하여 일정 시간 동안 락을 못 얻으면 포기하게 합니다.

### 7.2 생산자-소비자 패턴 (식당 주방 비유)

멀티스레딩의 가장 전형적인 디자인 패턴입니다. `BlockingQueue`를 사용하면 아주 쉽게 구현할 수 있습니다.

[ProducerConsumerExample.java](../../src/main/java/com/nhnacademy/practices/ProducerConsumerExample.java)

```java
package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class ProducerConsumerExample {
    public static void main(String[] args) {
        BlockingQueue<String> tray = new ArrayBlockingQueue<>(5);

        // 생산자 (요리사)
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    String food = "Dish " + i;
                    tray.put(food);
                    log.info("Producer: Cooked {}", food);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                log.error("Producer interrupted", e);
                Thread.currentThread().interrupt();
            }
        });

        // 소비자 (웨이터)
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    String food = tray.take();
                    log.info("Consumer: Served {}", food);
                    Thread.sleep(500); // 서빙이 더 오래 걸림
                }
            } catch (InterruptedException e) {
                log.error("Consumer interrupted", e);
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}
```

#### [실습] 실전 생산자-소비자 패턴 구현

`ArrayBlockingQueue`를 사용하여 실제 서비스에서 활용 가능한 생산자-소비자 구조를 직접 작성해 봅시다.

[ProducerConsumerExercise.java](../../src/main/java/com/nhnacademy/practices/ProducerConsumerExercise.java)

### 7.3 파이프라인 패턴 (Pipeline Pattern)

작업을 여러 단계로 나누고, 각 단계를 별도의 스레드(또는 스레드 풀)가 처리하게 하여 처리량을 높이는 방식입니다.

[PipelineExample.java](../../src/main/java/com/nhnacademy/practices/PipelineExample.java)

```java
package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class PipelineExample {
    public static void main(String[] args) {
        BlockingQueue<String> step1ToStep2 = new ArrayBlockingQueue<>(10);
        BlockingQueue<String> step2ToFinal = new ArrayBlockingQueue<>(10);

        // Step 1: 데이터 로드
        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    step1ToStep2.put("RawData-" + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                log.error("Step 1 interrupted", e);
                Thread.currentThread().interrupt();
            }
        }).start();

        // Step 2: 데이터 가공
        Thread worker2 = new Thread(() -> {
            try {
                while (true) {
                    String data = step1ToStep2.take();
                    step2ToFinal.put(data + " (Processed)");
                }
            } catch (InterruptedException e) {
                log.error("Step 2 interrupted", e);
                Thread.currentThread().interrupt();
            }
        });
        worker2.setDaemon(true);
        worker2.start();

        // Step 3: 데이터 저장/출력
        Thread worker3 = new Thread(() -> {
            try {
                while (true) {
                    String result = step2ToFinal.take();
                    log.info("Final Output: {}", result);
                }
            } catch (InterruptedException e) {
                log.error("Step 3 interrupted", e);
                Thread.currentThread().interrupt();
            }
        });
        worker3.setDaemon(true);
        worker3.start();
        
        try {
            Thread.sleep(2000); // 작업 완료 대기
        } catch (InterruptedException e) {
            log.error("Main thread interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
```

### 7.4 멀티스레드 환경에서의 안전한 Singleton 패턴

싱글톤 패턴을 구현할 때 멀티스레드 환경을 고려하지 않으면 여러 개의 인스턴스가 생성될 수 있습니다.

#### [예제] Double-Checked Locking (DCL)

[SafeSingleton.java](../../src/main/java/com/nhnacademy/practices/SafeSingleton.java)

```java
package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SafeSingleton {
    // volatile이 없으면 반반쯤 생성된 객체를 다른 스레드가 참조할 위험이 있음
    private static volatile SafeSingleton instance;

    private SafeSingleton() {}

    public static SafeSingleton getInstance() {
        if (instance == null) {
            synchronized (SafeSingleton.class) {
                if (instance == null) {
                    instance = new SafeSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            SafeSingleton s = SafeSingleton.getInstance();
            log.info("Singleton instance: {}", s);
        });

        Thread t2 = new Thread(() -> {
            SafeSingleton s = SafeSingleton.getInstance();
            log.info("Singleton instance: {}", s);
        });

        t1.start();
        t2.start();
    }
}
```

#### [예제] LazyHolder 방식 (가장 권장됨)

자바의 클래스 로딩 시점의 원자성을 이용한 방식입니다. `synchronized` 없이도 Thread-safe 합니다.

[BetterSingleton.java](../../src/main/java/com/nhnacademy/practices/BetterSingleton.java)

```java
package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BetterSingleton {
    private BetterSingleton() {}

    private static class Holder {
        private static final BetterSingleton INSTANCE = new BetterSingleton();
    }

    public static BetterSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            BetterSingleton s = BetterSingleton.getInstance();
            log.info("Singleton instance: {}", s);
        });

        Thread t2 = new Thread(() -> {
            BetterSingleton s = BetterSingleton.getInstance();
            log.info("Singleton instance: {}", s);
        });

        t1.start();
        t2.start();
    }
}
```

### 7.5 멀티스레드 환경에서의 디버깅 팁

멀티스레드 버그는 재현이 어렵기로 유명합니다(Heisenbug).

1.  **로그 활용**: `System.out.println` 보다는 로깅 프레임워크(SLF4J, Logback)를 사용하고, 로그 설정에 **스레드 이름(`%thread`)**을 반드시 포함시키세요.
2.  **Thread Dump 분석**: 문제가 생겼을 때 현재 모든 스레드의 상태를 찍어보는 것입니다. (IntelliJ의 'Dump Threads' 기능이나 `jstack` 도구 활용)
3.  **디버거의 'Thread' 뷰**: 중단점(Breakpoint)을 잡았을 때 다른 스레드들의 위치와 변수 값을 확인할 수 있습니다.
4.  **단위 테스트**: `CountDownLatch` 등을 활용하여 인위적으로 경쟁 상태를 유도하는 테스트 코드를 작성해 봅니다.
