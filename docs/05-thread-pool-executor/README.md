# 5. 스레드 풀과 Executor Framework - 효율적인 스레드 관리

이 장에서는 스레드를 직접 관리하는 대신 스레드 풀을 사용하여 시스템 자원을 효율적으로 관리하고 비동기 작업을 처리하는 방법을 배웁니다.

## 학습 내용

### 5.1 스레드를 직접 생성할 때의 문제점

1.  **생성 비용**: 스레드는 OS 차원의 자원이므로 생성하고 제거하는 데 비용이 큽니다.
2.  **리소스 고갈**: 요청마다 스레드를 생성하면, 갑작스러운 트래픽 폭증 시 시스템 메모리가 고갈되어 서버가 다운될 수 있습니다.
3.  **컨텍스트 스위칭**: 너무 많은 스레드가 있으면 CPU가 작업 전환(Context Switching)에 시간을 다 써버려 실제 성능이 떨어집니다.

### 5.2 ExecutorService와 Executors 팩토리 클래스 (비유: 인력 사무소)

자바는 `Executor` 인터페이스를 통해 작업의 **등록**과 **실행**을 분리했습니다. 이를 **'인력 사무소'**에 비유할 수 있습니다.

*   **스레드 직접 생성**: 일이 생길 때마다 길거리에 나가서 사람을 새로 고용하고, 일이 끝나면 해고하는 방식입니다. (비효율적)
*   **ExecutorService (인력 사무소)**: 미리 일꾼들을 대기시켜 놓고, 일이 들어오면 대기 중인 일꾼에게 일을 맡깁니다. 일꾼은 일을 마치면 다시 사무소로 돌아와 다음 일을 기다립니다.

[ExecutorServiceExample.java](../../src/main/java/com/nhnacademy/pool/ExecutorServiceExample.java)

```java
package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService를 이용한 스레드 풀 기초 예제입니다.
 * 쉽게 이해하기:
 * - 스레드 풀: "인력 사무소"와 같습니다.
 * - 일이 생길 때마다 사람을 고용하는 게 아니라, 미리 고용된 일꾼(스레드)들에게 일을 나눠줍니다.
 * - 일꾼은 일을 마치면 돌아와서 다음 일을 기다립니다.
 */
@Slf4j
public class ExecutorServiceExample {
    public static void main(String[] args) {
        // 3명의 일꾼이 대기 중인 인력 사무소(스레드 풀)를 세웁니다.
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 5개의 작업을 인력 사무소에 맡깁니다.
        for (int i = 0; i < 5; i++) {
            int taskId = i;
            executor.execute(() -> {
                log.info("작업 {} 번을 {} 가 처리하고 있습니다.", taskId, Thread.currentThread().getName());
                try {
                    // 일하는 데 시간이 걸린다고 가정합니다.
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 인력 사무소를 닫습니다. (기존에 맡긴 일은 끝까지 처리합니다.)
        executor.shutdown(); 
    }
}
```

#### [실습] CachedThreadPool 활용

`CachedThreadPool`을 생성하고 다수의 작업을 비동기로 실행하는 코드를 작성해 봅시다.

[ExecutorServiceExercise.java](../../src/main/java/com/nhnacademy/pool/ExecutorServiceExercise.java)

### 5.3 스레드 풀의 종류

*   **FixedThreadPool**: 고정된 개수의 스레드를 가집니다. 부하가 일정할 때 사용합니다.
*   **CachedThreadPool**: 필요에 따라 스레드를 동적으로 생성합니다. 작업 시간이 짧고 요청이 가변적일 때 유리합니다.
*   **ScheduledThreadPool**: 주기적인 작업이나 지연 실행이 필요할 때 사용합니다.

#### [예제] `ScheduledExecutorService` 활용

[SchedulerExample.java](../../src/main/java/com/nhnacademy/pool/SchedulerExample.java)

```java
package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledExecutorService를 이용한 예약 작업 예제입니다.
 * 쉽게 이해하기:
 * - 예약 스레드 풀: "알람 시계"와 같습니다.
 * - 정해진 시간 뒤에 실행하거나, 일정한 간격으로 반복해서 실행할 수 있습니다.
 */
@Slf4j
public class SchedulerExample {
    public static void main(String[] args) {
        // 예약 담당 일꾼 한 명을 고용합니다.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 1. 단발성 예약: 2초 뒤에 딱 한 번 실행합니다.
        log.info("지연 작업을 예약합니다 (2초 뒤 실행)");
        scheduler.schedule(() -> log.info("[알람] 2초가 지났습니다!"), 2, TimeUnit.SECONDS);

        // 2. 반복성 예약: 1초 대기 후, 3초마다 계속 실행합니다.
        log.info("반복 작업을 예약합니다 (1초 대기 후 3초 간격)");
        scheduler.scheduleAtFixedRate(() -> {
            log.info("[반복] 현재 시간: {}", System.currentTimeMillis());
        }, 1, 3, TimeUnit.SECONDS);

        // 주의: 메인 스레드가 바로 끝나지 않도록 잠시 유지하거나, 
        // 서비스 중단 시 scheduler.shutdown()을 호출해야 합니다.
    }
}
```

#### [실습] ScheduledExecutorService 활용

정해진 시간에 작업을 실행하거나 반복하는 기능을 직접 구현해 봅시다.

[ScheduledExecutorExercise.java](../../src/main/java/com/nhnacademy/pool/ScheduledExecutorExercise.java)

### 5.4 ThreadPoolExecutor의 구성과 동작 원리

`Executors`가 만드는 대부분의 풀은 내부적으로 `ThreadPoolExecutor`를 사용합니다.

[CustomThreadPool.java](../../src/main/java/com/nhnacademy/pool/CustomThreadPool.java)

```java
package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolExecutor를 직접 설정하여 세밀한 스레드 풀을 만드는 예제입니다.
 * 쉽게 이해하기:
 * - Core Size: 평소에 유지할 일꾼 수
 * - Max Size: 바쁠 때 최대로 늘릴 수 있는 일꾼 수
 * - Keep Alive: 일이 없어서 노는 임시 일꾼들을 해고하기 전 대기 시간
 * - Work Queue: 일이 밀렸을 때 쌓아두는 대기 공간
 */
@Slf4j
public class CustomThreadPool {
    public static void main(String[] args) {
        // 커스텀 인력 사무소 설정
        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
            2,                      // 평소에 2명의 일꾼을 둡니다.
            4,                      // 최대 4명까지 늘릴 수 있습니다.
            60, TimeUnit.SECONDS,   // 임시 일꾼은 60초간 일이 없으면 퇴근합니다.
            new LinkedBlockingQueue<>(10), // 대기 공간은 10개입니다.
            new ThreadFactory() {   // 일꾼을 새로 뽑는 규칙을 정합니다.
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("우리집-일꾼-" + t.getId());
                    return t;
                }
            },
            // 대기 공간도 꽉 차고 일꾼도 최대치인데 일이 더 들어오면 어떻게 할까요?
            new ThreadPoolExecutor.CallerRunsPolicy() // "너(요청자)가 직접 해!"라고 시킵니다.
        );

        log.info("커스텀 스레드 풀이 생성되었습니다.");
        
        // 사용 후에는 안전하게 닫아줍니다.
        customPool.shutdown();
    }
}
```

*   **동작 흐름**: 
    1. `corePoolSize` 만큼 스레드를 채웁니다.
    2. 꽉 차면 `Work Queue`에 쌓습니다.
    3. 큐도 꽉 차면 `maxPoolSize`까지 스레드를 늘립니다.
    4. 이것도 꽉 차면 거절 정책(RejectedExecutionHandler)에 따라 작업을 처리합니다.
        *   **AbortPolicy**: 기본값. 예외를 던집니다.
        *   **CallerRunsPolicy**: 작업을 요청한 스레드(예: 메인 스레드)가 직접 작업을 수행하게 합니다. 시스템의 속도를 늦추는 효과가 있습니다.

#### [예제] `ThreadFactory`를 통한 스레드 커스텀
스레드에 의미 있는 이름을 부여하거나 데몬 여부를 설정할 때 유용합니다.

[CustomThreadFactory.java](../../src/main/java/com/nhnacademy/pool/CustomThreadFactory.java)

```java
package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory를 구현하여 스레드 생성을 커스텀하는 예제입니다.
 * 쉽게 이해하기:
 * - ThreadFactory: "작업자 전용 명찰 제작기"와 같습니다.
 * - 작업자가 새로 뽑힐 때마다 정해진 규칙에 따라 이름표를 달아주고 설정을 해줍니다.
 */
@Slf4j
public class CustomThreadFactory implements ThreadFactory {
    private int counter = 0;
    private final String prefix;

    public CustomThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public synchronized Thread newThread(Runnable r) {
        // 새로운 작업자를 만들 때 이름을 "Worker-0", "Worker-1" 식으로 붙여줍니다.
        String name = prefix + "-" + counter++;
        Thread t = new Thread(r, name);
        log.info("새로운 작업자 생성: {}", name);
        return t;
    }

    public static void main(String[] args) {
        // "요리사"라는 이름을 붙여주는 명찰 제작기를 만듭니다.
        CustomThreadFactory factory = new CustomThreadFactory("요리사");
        
        Thread t1 = factory.newThread(() -> log.info("요리 시작"));
        Thread t2 = factory.newThread(() -> log.info("주문 접수"));

        t1.start();
        t2.start();
    }
}
```

### 5.6 Callable, Future, 그리고 CompletableFuture

#### [예제] `CompletableFuture` 비동기 파이프라인
여러 비동기 작업을 조합하여 복잡한 흐름을 만들 수 있습니다.

[CompletableFutureExample.java](../../src/main/java/com/nhnacademy/pool/CompletableFutureExample.java)

```java
package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture를 이용한 비동기 작업 연결(Chaining) 예제입니다.
 * 쉽게 이해하기:
 * - CompletableFuture: "주문 벨"과 같습니다.
 * - 음식을 주문하고 벨을 받은 뒤, 벨이 울리면 다음 단계(음식 받기, 먹기, 반납하기)가 
 *   자동으로 진행되도록 미리 설정해두는 것입니다.
 */
@Slf4j
public class CompletableFutureExample {
    public static void main(String[] args) {
        // 비동기 작업 시작 (별도의 작업자가 처리)
        CompletableFuture.supplyAsync(() -> {
            log.info("1단계: 데이터 가져오기 (시간이 걸리는 작업)");
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            return "원본 데이터";
        }).thenApply(data -> {
            // 1단계 결과가 나오면 자동으로 실행
            log.info("2단계: {} 를 가공합니다.", data);
            return data + " -> 가공된 결과";
        }).thenAccept(result -> {
            // 2단계 결과가 나오면 마지막으로 실행
            log.info("3단계: 최종 결과 출력: {}", result);
        }).join(); // 모든 단계가 끝날 때까지 메인 스레드가 기다립니다.
    }
}
```

#### [실습] CompletableFuture 체이닝

비동기 작업들을 연결하여 순차적으로 처리하는 파이프라인을 만들어 봅시다.

[CompletableFutureExercise.java](../../src/main/java/com/nhnacademy/pool/CompletableFutureExercise.java)

### 5.7 ForkJoinPool과 Work Stealing

*   **ForkJoinPool**: 큰 작업을 쪼개서(Fork) 처리하고 합치는(Join) 방식에 특화되었습니다. (병렬 스트림의 기본 풀)
*   **Work Stealing**: 노는 스레드가 바쁜 스레드의 큐 뒷부분에서 일을 훔쳐와서(Steal) 처리하여 CPU 활용도를 극대화합니다.
