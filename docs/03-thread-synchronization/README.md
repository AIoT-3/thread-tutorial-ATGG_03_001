# 3. 스레드 동기화 (Synchronization) - 공유 자원 관리

이 장에서는 여러 스레드가 공유 자원에 동시에 접근할 때 발생하는 문제와 이를 해결하기 위한 동기화 기법을 배웁니다.

## 학습 내용

### 3.1 동기화가 필요한 이유 (상태 공유와 경쟁 상태)

*   **공유 자원**: 여러 스레드가 동시에 접근하여 읽고 쓰는 메모리 영역(Heap 등)입니다.
*   **경쟁 상태 (Race Condition)**: 두 개 이상의 스레드가 경쟁적으로 공유 자원을 수정할 때, 실행 순서에 따라 결과가 달라지는 현상입니다.

#### [예제] 경쟁 상태가 발생하는 코드

[RaceConditionExample.java](../../src/main/java/com/nhnacademy/synchronization/RaceConditionExample.java)

```java
package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * 경쟁 상태(Race Condition)를 시뮬레이션하는 예제입니다.
 * 쉽게 이해하기:
 * - 경쟁 상태: 여러 사람이 하나의 통장(공유 자원)에서 동시에 돈을 입금할 때, 
 *   동시에 처리하다가 입금 기록이 누락되는 상황과 같습니다.
 */
@Slf4j
public class RaceConditionExample {
    private int counter = 0;

    /**
     * 카운터를 1씩 증가시킵니다.
     * 요약 설명: 이 동작은 내부적으로 '읽기 -> 1 더하기 -> 다시 쓰기'의 3단계로 이루어집니다.
     * 두 스레드가 동시에 '읽기'를 하면, 같은 값을 읽어서 1만 더해지는 문제가 발생합니다.
     */
    public void increment() {
        counter++; 
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        RaceConditionExample example = new RaceConditionExample();

        // 두 명의 작업자(t1, t2)가 각각 10,000번씩 총 20,000번을 더하려고 합니다.
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) example.increment();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) example.increment();
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // 동기화 처리를 하지 않았기 때문에 결과가 20,000이 나오지 않을 확률이 매우 높습니다.
        log.info("최종 카운트 결과: {}", example.getCounter());
        log.info("기대했던 결과: 20000");
    }
}
```

#### [실습] synchronized를 이용한 데이터 정합성 확보

경쟁 상태가 발생하는 코드에 `synchronized`를 적용하여 문제를 해결해 봅시다.

[RaceConditionExercise.java](../../src/main/java/com/nhnacademy/synchronization/RaceConditionExercise.java)

### 3.2 임계 영역(Critical Section)과 뮤텍스(Mutex) / 모니터 (화장실과 열쇠 비유)

멀티스레드 환경에서 동기화를 이해하는 가장 쉬운 방법은 **'공중화장실'** 모델입니다.

*   **임계 영역 (Critical Section)**: 한 번에 한 사람만 들어갈 수 있는 **화장실 칸**입니다. 공유 데이터가 수정되는 코드 영역을 의미합니다.
*   **뮤텍스 (Mutex, Mutual Exclusion)**: 공유 자원에 대한 **상호 배제**를 달성하기 위한 도구입니다. 오직 하나의 스레드만 락을 소유할 수 있으며, 락을 가진 스레드만이 자원에 접근할 수 있습니다.
*   **모니터 (Monitor)**: 뮤텍스보다 상위 개념으로, 자바 객체가 기본적으로 내장하고 있는 동기화 메커니즘입니다. `synchronized` 키워드를 통해 구현됩니다.
    *   자바의 모든 객체는 고유 락(Intrinsic Lock)이라는 '열쇠'를 하나씩 가지고 있습니다.
    *   화장실에 들어가려는 사람(스레드)은 반드시 열쇠를 손에 넣어야 합니다.
    *   먼저 온 사람이 열쇠를 가지고 들어가 문을 잠그면, 뒤에 온 사람은 앞사람이 나와서 열쇠를 반납할 때까지 밖에서 기다려야 합니다.

#### [실습] Object를 이용한 커스텀 Mutex 구현
자바의 모든 객체가 가진 `wait()`와 `notify()`를 활용하여 직접 `Mutex` 클래스를 설계해 봅시다.

[ObjectMutexExercise.java](../../src/main/java/com/nhnacademy/synchronization/ObjectMutexExercise.java)

### 3.3 `synchronized` 키워드 사용법

자바에서 가장 기본적인 동기화 도구입니다.

[SynchronizationExample.java](../../src/main/java/com/nhnacademy/synchronization/SynchronizationExample.java)

```java
package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * synchronized 키워드를 사용한 동기화 예제입니다.
 * 쉽게 이해하기:
 * - synchronized: "이 문을 열고 들어가면 나 혼자만 쓸 거야!"라고 선언하는 것과 같습니다.
 * - 문에 걸린 자물쇠(Lock)를 얻은 사람만 안으로 들어갈 수 있습니다.
 */
@Slf4j
public class SynchronizationExample {
    private int counter = 0;

    /**
     * 1. 메소드 전체에 동기화 걸기
     * 한 번에 하나의 스레드만 이 메소드를 실행할 수 있습니다.
     */
    public synchronized void incrementSyncMethod() {
        counter++;
    }

    /**
     * 2. 특정 블록에만 동기화 걸기 (더 권장되는 방식)
     * 필요한 부분에만 자물쇠를 걸어 다른 코드의 실행 속도를 떨어뜨리지 않습니다.
     */
    private final Object lock = new Object(); // 자물쇠 역할을 할 전용 객체
    public void incrementSyncBlock() {
        synchronized (lock) {
            counter++;
        }
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizationExample example = new SynchronizationExample();
        
        // 두 작업자가 서로 다른 방식으로 1,000번씩 더합니다.
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.incrementSyncMethod();
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.incrementSyncBlock();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // 동기화 덕분에 정확히 2,000이 출력됩니다.
        log.info("최종 카운트 결과: {}", example.getCounter());
    }
}
```

### 3.4 `wait()`, `notify()`, `notifyAll()`를 이용한 스레드 간 통신

스레드가 단순히 기다리는 것이 아니라, 특정 조건이 만족될 때까지 '잠들었다가 깨어나는' 방식입니다.

#### [예제] 공유 창고 (Data Buffer) 모델

[SharedBuffer.java](../../src/main/java/com/nhnacademy/synchronization/SharedBuffer.java)

```java
package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;
import java.util.LinkedList;
import java.util.Queue;

/**
 * wait()와 notifyAll()을 이용한 스레드 간 통신 예제입니다.
 * 쉽게 이해하기:
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
```

*   **반드시 `synchronized` 블록 내에서만 호출 가능**합니다.
*   `wait()`: 락을 반납하고 대기실(Wait Set)에서 기다립니다.
*   `notify()`: 대기실에서 기다리는 스레드 중 하나를 깨웁니다.
*   `notifyAll()`: 대기실에서 기다리는 모든 스레드를 깨웁니다. (일반적으로 더 안전합니다.)

#### [실습] wait()와 notifyAll()을 이용한 스레드 간 통신

생산자와 소비자 스레드가 서로 신호를 주고받으며 안전하게 데이터를 주고받는 코드를 작성해 봅시다.

[SharedBufferExercise.java](../../src/main/java/com/nhnacademy/synchronization/SharedBufferExercise.java)

### 3.5 가시성(Visibility) 문제와 `volatile`

멀티스레드 환경에서 한 스레드가 변경한 값이 다른 스레드에게 즉시 보이지 않을 수 있습니다. 이는 CPU가 성능 향상을 위해 메인 메모리가 아닌 각 코어의 **캐시(Cache)**에서 값을 읽기 때문입니다.

[VolatileExample.java](../../src/main/java/com/nhnacademy/synchronization/VolatileExample.java)

```java
package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * volatile 키워드를 이용한 가시성(Visibility) 해결 예제입니다.
 * 쉽게 이해하기:
 * - 가시성 문제: 한 명이 전등 스위치를 껐는데, 다른 방에 있는 사람은 여전히 켜져 있다고 생각하는 상황입니다. (CPU 캐시 때문)
 * - volatile: "이 변수는 캐시를 쓰지 말고 항상 메인 메모리에서 직접 확인해!"라고 지시하는 것입니다.
 */
@Slf4j
public class VolatileExample {
    // volatile이 없으면 메인 스레드가 stop을 true로 바꿔도 
    // worker 스레드는 자신의 방(CPU 캐시)에 있는 예전 값(false)을 계속 보고 루프를 돌 수 있습니다.
    private static volatile boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            log.info("작업자: 일을 시작합니다...");
            while (!stop) {
                // 매우 바쁘게 돌아가는 루프
            }
            log.info("작업자: 중단 신호를 확인하고 일을 마칩니다.");
        });

        worker.start();
        
        // 1초간 지켜봅니다.
        Thread.sleep(1000);
        
        log.info("메인: 이제 그만하라고 스위치를 켭니다 (stop = true).");
        stop = true;
    }
}
```

#### [예제] 데드락 방지: 락 획득 순서 강제 (비유: 젓가락 사용 규칙)

두 개 이상의 락을 사용할 때는 항상 일정한 순서로 락을 획득해야 데드락을 피할 수 있습니다.

[DeadlockPrevention.java](../../src/main/java/com/nhnacademy/synchronization/DeadlockPrevention.java)

```java
package com.nhnacademy.synchronization;

import lombok.extern.slf4j.Slf4j;

/**
 * 자원 획득 순서를 강제하여 데드락을 방지하는 예제입니다.
 * 쉽게 이해하기:
 * - 해결책: "무조건 왼쪽 젓가락을 먼저 집고 나서 오른쪽을 집어야 한다"는 규칙을 세우는 것입니다.
 * - 모두가 똑같은 순서로만 행동하면, 서로 꼬여서 멈추는 일이 발생하지 않습니다.
 */
@Slf4j
public class DeadlockPrevention {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void safeMethod() {
        // 항상 lock1 -> lock2 순서로만 획득하도록 규칙을 정함
        // 철학자 1도, 2도 모두 이 순서를 따릅니다.
        synchronized (lock1) {
            log.info("{} 가 1번 자물쇠를 얻었습니다.", Thread.currentThread().getName());
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            
            synchronized (lock2) {
                log.info("{} 가 2번 자물쇠까지 얻어 작업을 완료했습니다.", Thread.currentThread().getName());
            }
        }
    }

    public static void main(String[] args) {
        DeadlockPrevention example = new DeadlockPrevention();
        
        // 두 작업자가 동시에 같은 순서로 자원을 요청합니다.
        Thread t1 = new Thread(example::safeMethod, "작업자 1");
        Thread t2 = new Thread(example::safeMethod, "작업자 2");

        log.info("데드락 방지 실험을 시작합니다. 이번에는 멈추지 않고 끝날 것입니다.");
        t1.start();
        t2.start();
    }
}
```

#### [실습] volatile을 이용한 가시성 문제 해결

`volatile` 키워드를 사용하여 스레드 간에 값이 즉시 공유되는지 확인해 봅시다.

[VisibilityExercise.java](../../src/main/java/com/nhnacademy/synchronization/VisibilityExercise.java)

### 3.6 Thread Safety란 무엇인가?

어떤 클래스나 함수가 여러 스레드로부터 동시에 호출되어도 항상 의도한 대로 정확하게 동작하는 상태를 말합니다.

*   **Thread Safety를 달성하는 방법**:
    1.  공유 상태를 아예 만들지 않기 (무상태성, Immutable 객체 사용)
    2.  공유 상태를 동기화하기 (`synchronized`, Lock 등)
    3.  동시성 라이브러리 사용하기 (`java.util.concurrent`)
