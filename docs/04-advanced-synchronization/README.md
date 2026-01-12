# 4. 고급 동기화 도구 (java.util.concurrent)

이 장에서는 자바에서 제공하는 고수준의 동기화 도구들을 사용하여 더 효율적이고 정교하게 스레드를 제어하는 방법을 배웁니다.

## 학습 내용

### 4.1 Lock Framework (`ReentrantLock`, `ReadWriteLock`)

`synchronized`의 한계를 극복하기 위해 JDK 5부터 도입되었습니다.

[ReentrantLockExample.java](../../src/main/java/com/nhnacademy/advanced/ReentrantLockExample.java)

```java
package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock을 이용한 고급 동기화 예제입니다.
 * 쉽게 이해하기:
 * - ReentrantLock: "자물쇠를 수동으로 잠그고 여는 방식"입니다.
 * - 장점: 락을 얻으려고 시도해보고 안 되면 다른 일을 하거나(tryLock), 
 *   기다리는 시간을 정할 수 있어 더 유연합니다.
 */
@Slf4j
public class ReentrantLockExample {
    // 공정한 자물쇠(객체)를 만듭니다.
    private final ReentrantLock lock = new ReentrantLock();
    private int counter = 0;

    /**
     * 기본적인 락 사용법 (반드시 finally에서 해제해야 함)
     */
    public void increment() {
        lock.lock(); // 자물쇠 잠그기 (누군가 쓰고 있으면 여기서 대기)
        try {
            counter++;
        } finally {
            // 예외가 발생하더라도 자물쇠는 반드시 열어줘야 다른 사람이 쓸 수 있습니다.
            lock.unlock(); 
        }
    }

    /**
     * 락 획득 시도 (안 되면 포기)
     */
    public void tryIncrement() {
        if (lock.tryLock()) { // 자물쇠를 바로 얻을 수 있는지 확인
            try {
                counter++;
                log.info("락을 얻어 성공적으로 숫자를 올렸습니다.");
            } finally {
                lock.unlock();
            }
        } else {
            // 자물쇠를 얻지 못했을 때 기다리지 않고 다른 일을 수행합니다.
            log.info("다른 사람이 쓰고 있네요. 기다리지 않고 다른 일을 합니다.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockExample example = new ReentrantLockExample();

        // 두 작업자가 숫자를 올립니다.
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.increment();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.info("최종 결과: {}", example.counter);
    }
}
```

#### [실습] ReentrantLock의 tryLock 활용

`ReentrantLock`의 `tryLock`을 사용하여 락 획득 시 타임아웃을 처리하는 코드를 작성해 봅시다.

[ReentrantLockExercise.java](../../src/main/java/com/nhnacademy/advanced/ReentrantLockExercise.java)

*   **ReentrantLock**: 가장 대표적인 구현체입니다.
    * **장점**: 락을 획득하려고 무한정 대기하지 않게 설정 가능(`tryLock`), 공정성(Fairness) 설정 가능, 여러 개의 대기 조건(`Condition`) 사용 가능
        * **공정한 Lock**: 요청된 순서대로 락을 제공합니다. 가장 오래 기다린 스레드가 우선권을 갖습니다. 성능은 다소 떨어질 수 있습니다.
        * **비공정한 Lock**: 대기 순서와 상관없이 임의의 스레드에게 락을 제공합니다. 성능은 더 좋지만 특정 스레드가 오래 기다릴 수 있습니다.
* **ReadWriteLock**: 읽기 작업은 여러 스레드가 동시에 할 수 있지만, 쓰기 작업은 오직 하나만 할 수 있도록 분리한 락입니다.

#### [예제] `ReadWriteLock` 활용
읽기 작업이 쓰기 작업보다 훨씬 빈번한 경우, `ReadWriteLock`을 사용하면 성능을 획기적으로 높일 수 있습니다.

[CacheSystem.java](../../src/main/java/com/nhnacademy/advanced/CacheSystem.java)

```java
package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock을 이용한 데이터 읽기 성능 최적화 예제입니다.
 * 쉽게 이해하기: 
 * - 읽기 자물쇠: "책을 읽는 것은 여러 명이 동시에 해도 괜찮아요."
 * - 쓰기 자물쇠: "하지만 책 내용을 고치는 건 한 번에 한 명만 해야 해요. 읽는 사람도 없어야 하죠."
 */
@Slf4j
public class CacheSystem {
    private final Map<String, String> cache = new HashMap<>();
    
    // 읽기와 쓰기용 자물쇠가 따로 있는 특수 자물쇠를 준비합니다.
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    /**
     * 데이터를 가져옵니다. (여러 스레드가 동시에 실행 가능)
     */
    public String get(String key) {
        readLock.lock(); 
        try {
            return cache.get(key);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 데이터를 저장합니다. (오직 한 스레드만 실행 가능하며, 읽는 스레드도 없어야 함)
     */
    public void put(String key, String value) {
        writeLock.lock();
        try {
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        CacheSystem cacheSystem = new CacheSystem();
        cacheSystem.put("key1", "초기 데이터");

        // 동시에 여러 명이 읽기를 시도해도 서로 방해하지 않습니다.
        Thread t1 = new Thread(() -> log.info("읽기 1: {}", cacheSystem.get("key1")));
        Thread t2 = new Thread(() -> log.info("읽기 2: {}", cacheSystem.get("key1")));
        
        // 쓰기는 읽기가 모두 끝날 때까지 기다렸다가 혼자서 수행합니다.
        Thread t3 = new Thread(() -> {
            cacheSystem.put("key1", "수정된 데이터");
            log.info("데이터가 수정되었습니다.");
        });

        t1.start();
        t2.start();
        t3.start();
    }
}
```

#### [실습] ReadWriteLock 활용

데이터 읽기 비중이 높은 상황을 가정하여 `ReadWriteLock`으로 성능을 최적화해 봅시다.

[ReadWriteLockExercise.java](../../src/main/java/com/nhnacademy/advanced/ReadWriteLockExercise.java)

### 4.2 원자적 변수 (Atomic Variables)

락을 걸지 않고도(Lock-Free) 안전하게 변수를 수정할 수 있게 해주는 클래스들입니다.

[AtomicExample.java](../../src/main/java/com/nhnacademy/advanced/AtomicExample.java)

```java
package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger를 사용한 Lock-free 동기화 예제입니다.
 * 쉽게 이해하기:
 * - Atomic(원자적): "더 이상 쪼갤 수 없는 최소 단위"라는 뜻입니다.
 * - 자물쇠(Lock)를 걸지 않고도, 아주 빠르게 숫자를 계산할 수 있는 특수한 도구입니다.
 * - 통장에 돈을 넣을 때 자물쇠로 문을 잠그는 대신, 은행원이 아주 빠른 속도로 한 명씩 처리하는 것과 비슷합니다.
 */
@Slf4j
public class AtomicExample {
    // 원자적 정수 객체 생성
    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        // 내부적으로 CAS(Compare-And-Swap)라는 고도의 기술을 사용하여 
        // 자물쇠 없이도 안전하게 값을 1 증가시킵니다.
        counter.incrementAndGet(); 
    }

    public int getCounter() {
        return counter.get();
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicExample example = new AtomicExample();

        // 10,000번씩 더하는 두 명의 작업자
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

        // 자물쇠 없이도 정확히 20,000이 출력됩니다.
        log.info("최종 원자적 카운트 결과: {}", example.getCounter());
    }
}
```

#### [실습] AtomicInteger와 CAS 연산

`AtomicInteger`를 사용하여 자물쇠 없이 안전하게 값을 변경하는 코드를 작성해 봅시다.

[AtomicExercise.java](../../src/main/java/com/nhnacademy/advanced/AtomicExercise.java)

*   **원리**: **CAS(Compare-And-Swap)** 알고리즘을 사용합니다. CPU가 현재 값과 예상 값이 같을 때만 새 값으로 교체하는 저수준 연산입니다.
*   **성능**: 저수준의 경합이 발생하는 경우 `synchronized`보다 성능이 좋은 경우가 많습니다.

### 4.3 카운트다운 래치(CountDownLatch)와 세마포어(Semaphore)

스레드 간의 흐름을 제어하는 신호등 역할을 합니다.

*   **CountDownLatch (육상 경기 출발선)**: 
    *   모든 선수(스레드)가 출발선에 준비될 때까지 기다리는 심판의 총소리와 같습니다.
    *   카운트가 0이 될 때까지 `await()`에서 대기하며, 0이 되는 순간 모든 스레드가 동시에 출발합니다.
*   **Semaphore (유료 주차장)**:
    *   주차장(공유 자원)에 들어갈 수 있는 차량 수(스레드 수)를 제한하는 관리원과 같습니다.
    *   주차 자리가 있으면 들여보내고, 자리가 꽉 차면 차가 나갈 때까지 입구에서 대기시킵니다.

#### [예제] `Semaphore`를 이용한 리소스 제한

[ResourcePool.java](../../src/main/java/com/nhnacademy/advanced/ResourcePool.java)

```java
package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Semaphore;

/**
 * Semaphore를 이용한 리소스 접근 제한 예제입니다.
 * 쉽게 이해하기:
 * - Semaphore: "자리가 정해진 주차장"과 같습니다.
 * - 자리가 있으면 주차하고, 자리가 없으면 다른 차가 나갈 때까지 입구에서 대기시킵니다.
 */
@Slf4j
public class ResourcePool {
    private final Semaphore semaphore;
    
    /**
     * @param limit 허용 가능한 최대 작업자 수
     */
    public ResourcePool(int limit) {
        // 허가증(Permit)의 개수를 정합니다.
        this.semaphore = new Semaphore(limit);
    }

    /**
     * 리소스를 사용합니다.
     */
    public void useResource() {
        try {
            // 허가증을 획득하려고 시도합니다. (자리가 없으면 대기)
            semaphore.acquire(); 
            log.info("{} 가 자원을 사용 중입니다.", Thread.currentThread().getName());
            
            // 1초간 자원을 사용하는 척합니다.
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            log.error("작업 중단됨", e);
            Thread.currentThread().interrupt();
        } finally {
            log.info("{} 가 자원을 반납했습니다.", Thread.currentThread().getName());
            // 사용이 끝나면 반드시 허가증을 반납하여 다음 사람이 쓸 수 있게 합니다.
            semaphore.release(); 
        }
    }

    public static void main(String[] args) {
        // 자리가 2개뿐인 리소스 풀을 만듭니다.
        ResourcePool pool = new ResourcePool(2);

        // 5명의 사용자가 동시에 사용하려고 시도합니다.
        for (int i = 0; i < 5; i++) {
            new Thread(pool::useResource, "사용자-" + i).start();
        }
    }
}
```

*   **CyclicBarrier (단체 관광 버스)**:
    *   모든 관광객(스레드)이 모여야 버스가 출발하는 것과 같습니다.
    *   `CountDownLatch`와 비슷하지만, **재사용이 가능**하다는 점이 다릅니다. (버스 한 대가 떠나고 나면 다음 사람들을 위해 다시 대기 가능)

#### [예제] `CyclicBarrier` 활용

[BarrierExample.java](../../src/main/java/com/nhnacademy/advanced/BarrierExample.java)

```java
package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier를 이용한 스레드 동기화 예제입니다.
 * 쉽게 이해하기:
 * - CyclicBarrier: "단체 관광 가이드"와 같습니다.
 * - 모든 관광객(스레드)이 약속 장소에 모여야 다음 장소로 이동할 수 있습니다.
 * - 한 명이라도 늦으면 모두가 기다려야 합니다.
 */
@Slf4j
public class BarrierExample {
    public static void main(String[] args) {
        // 3명이 모이면 "출발합니다!"라고 외치는 배리어를 만듭니다.
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            log.info("--- [가이드] 모든 인원이 모였습니다. 이제 출발합니다! ---");
        });

        // 각 관광객이 할 일
        Runnable touristTask = () -> {
            try {
                log.info("{} 가 도착하여 대기 중입니다.", Thread.currentThread().getName());
                // 약속 장소에서 다른 사람들을 기다립니다.
                barrier.await();
                
                // 모두가 모이면 동시에 실행됩니다.
                log.info("{} 가 이동을 시작합니다.", Thread.currentThread().getName());
            } catch (Exception e) {
                log.error("대기 중 오류 발생", e);
                Thread.currentThread().interrupt();
            }
        };

        // 3명의 관광객(스레드) 출발
        new Thread(touristTask, "관광객 1").start();
        new Thread(touristTask, "관광객 2").start();
        new Thread(touristTask, "관광객 3").start();
    }
}
```

#### [실습] CountDownLatch 활용

여러 스레드가 동시에 작업을 시작할 수 있도록 `CountDownLatch`를 사용하는 코드를 작성해 봅시다.

[CountDownLatchExercise.java](../../src/main/java/com/nhnacademy/advanced/CountDownLatchExercise.java)

### 4.4 동시성 컬렉션 (Concurrent Collections)

멀티스레드 환경에서 안전하게 사용할 수 있도록 설계된 컬렉션들입니다.

*   **ConcurrentHashMap**: 전체를 잠그지 않고 부분(Segment/Bucket)만 잠그는 방식을 사용하여 매우 빠릅니다. **`Collections.synchronizedMap()`보다 훨씬 효율적입니다.**
*   **CopyOnWriteArrayList**: 읽기 작업이 압도적으로 많고 쓰기 작업이 매우 드물 때 유리합니다. 쓸 때마다 전체 리스트를 새로 복사합니다.
*   **BlockingQueue**: 큐가 비어있으면 `take()`가 대기하고, 가득 차 있으면 `put()`이 대기합니다. (생산자-소비자 패턴의 핵심)
