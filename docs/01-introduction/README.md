# 1. Introduction

이 장에서는 멀티스레드 프로그래밍의 기본 개념과 필요성, 그리고 자바에서의 역사에 대해 배웁니다.

## 학습 내용

### 1.1 스레드(Thread)의 어원

'스레드(Thread)'의 사전적 의미는 **'실'**입니다. 

*   **어원**: 바느질할 때 쓰는 실처럼, 프로그램의 실행 흐름이 마치 하나의 실타래를 풀어나가는 것과 같다고 해서 붙여진 이름입니다.
*   **의미**: 여러 개의 스레드가 있다는 것은, 하나의 프로그램 내에서 여러 가닥의 실이 동시에 짜여 나가는 것처럼 여러 개의 작업 흐름이 동시에 진행됨을 뜻합니다.

### 1.2 멀티스레드 프로그래밍이란? (비유를 통한 이해)

멀티스레딩은 하나의 프로그램이 동시에 여러 개의 작업을 수행할 수 있도록 하는 기술입니다.

*   **비유**: 식당에서의 업무 처리
    *   **싱글 스레드**: 혼자서 서빙, 요리, 계산을 모두 하는 1인 식당입니다. 손님이 많아지면 대기 시간이 길어집니다.
    *   **멀티 스레드**: 서빙 담당, 요리 담당, 계산 담당 직원이 각각 따로 있는 식당입니다. 여러 손님의 요구를 동시에 처리할 수 있어 효율적입니다.

### 1.3 싱글 스레드 vs 멀티 스레드 차이

싱글 스레드와 멀티 스레드의 가장 큰 차이는 '동시성'과 '자원 활용'에 있습니다.

| 구분 | 싱글 스레드 (Single Thread) | 멀티 스레드 (Multi Thread) |
| :--- | :--- | :--- |
| **작업 방식** | 한 번에 하나의 작업만 순차적으로 처리 | 여러 작업을 동시에(또는 병렬로) 처리 |
| **속도** | 작업이 길어지면 뒤의 작업이 대기 (Blocking) | 긴 작업이 있어도 다른 스레드에서 업무 처리 가능 |
| **자원 효율** | CPU 코어를 하나만 사용함 | 멀티 코어 CPU를 최대한 활용함 |
| **복잡도** | 설계가 간단하고 자원 경합 문제가 없음 | 설계가 복잡하고 동기화(Synchronization) 이슈 발생 |

### 1.4 프로세스와 스레드의 차이 (공장과 작업자 모델)

전공자들에게는 메모리 구조의 차이로, 비전공자들에게는 공장 모델로 설명할 수 있습니다.

*   **프로세스 (Process)**: 실행 중인 프로그램의 인스턴스입니다. 운영체제로부터 독립된 메모리 공간(Code, Data, Stack, Heap)을 할당받습니다.
    *   **공장 모델**: 하나의 독립된 **공장 건물이**며, 다른 공장과는 담벼락으로 격리되어 있습니다.
*   **스레드 (Thread)**: 프로세스 내에서 실행되는 흐름의 단위입니다. 프로세스의 자원(Heap, Static Data 등)을 공유하면서 자신만의 Stack을 가집니다.
    *   **공장 모델**: 공장 안에서 일하는 **작업자**입니다. 같은 공장 안의 기계(자원)를 공유하며 일합니다.

| 구분 | 프로세스 | 스레드 |
| :--- | :--- | :--- |
| 메모리 공유 | 독립적 (공유하려면 IPC 필요) | 프로세스 내 자원 공유 |
| 생성 비용 | 높음 | 낮음 |
| 안정성 | 한 프로세스가 죽어도 다른 프로세스에 영향 없음 | 한 스레드의 문제가 프로세스 전체에 영향을 줄 수 있음 |

### 1.5 왜 멀티스레드를 사용하는가? (성능과 응답성)

1.  **응답성 (Responsiveness)**: GUI 애플리케이션에서 무거운 연산을 하는 동안에도 사용자의 입력을 받을 수 있게 합니다.
2.  **자원 공유 (Resource Sharing)**: 프로세스 간 통신보다 스레드 간 통신이 훨씬 빠르고 간편합니다.
3.  **경제성 (Economy)**: 프로세스를 새로 만드는 것보다 스레드를 만드는 것이 운영체제 입장에서 비용이 저렴합니다.
4.  **멀티프로세서 활용**: 여러 개의 CPU 코어를 동시에 활용하여 프로그램 실행 속도를 높일 수 있습니다.

#### [예제] 순차 실행 vs 병렬 실행 비교

멀티스레드를 사용했을 때 얼마나 빨라지는지 간단한 코드로 확인해 볼 수 있습니다.

[PerformanceComparison.java](../../src/main/java/com/nhnacademy/introduction/PerformanceComparison.java)

```java
package com.nhnacademy.introduction;

import lombok.extern.slf4j.Slf4j;

/**
 * 순차 실행과 병렬 실행의 성능 차이를 비교하는 예제입니다.
 * 쉽게 이해하기: 
 * - 순차 실행: 한 사람이 일을 하나씩 끝내고 다음 일을 하는 방식
 * - 병렬 실행: 여러 사람이 동시에 각자 맡은 일을 처리하는 방식
 */
@Slf4j
public class PerformanceComparison {
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        log.info("--- 순차 실행 시작 ---");
        // 순차 실행 (Single Thread): Task 1이 끝나야 Task 2가 시작됩니다.
        runTask("Task 1");
        runTask("Task 2");

        long end = System.currentTimeMillis();
        log.info("순차 실행 총 소요 시간: {}ms", (end - start));

        start = System.currentTimeMillis();

        log.info("--- 병렬 실행 시작 ---");
        // 병렬 실행 (Multi Thread): 두 개의 스레드(작업자)를 만들어 동시에 일을 시킵니다.
        Thread t1 = new Thread(() -> runTask("Task 1"));
        Thread t2 = new Thread(() -> runTask("Task 2"));

        // start()를 호출해야 실제로 새로운 실타래(스레드)가 풀리기 시작합니다.
        t1.start();
        t2.start();

        // join()은 이 스레드들이 끝날 때까지 메인(Main) 스레드가 기다리게 합니다.
        // 기다리지 않으면 일이 끝나기도 전에 총 시간을 계산해버립니다.
        t1.join(); 
        t2.join(); 

        end = System.currentTimeMillis();
        log.info("병렬 실행 총 소요 시간: {}ms", (end - start));
    }

    /**
     * 1초가 걸리는 가상의 작업을 수행합니다.
     */
    private static void runTask(String name) {
        try {
            log.info("{} 작업 시작...", name);
            Thread.sleep(1000); // 1초간 멈춤 (작업 중임을 시뮬레이션)
            log.info("{} 작업 완료!", name);
        } catch (InterruptedException e) {
            log.error("{} 작업 중단됨", name, e);
            Thread.currentThread().interrupt();
        }
    }
}
```

#### [실습] 3개 작업의 병렬 실행

앞서 배운 내용을 바탕으로 3개의 작업을 동시에 실행하는 코드를 직접 작성해 봅시다.

[PerformanceExercise.java](../../src/main/java/com/nhnacademy/introduction/PerformanceExercise.java)

#### [예제 2] GUI 응답성 시뮬레이션 (비유: 주문받는 직원과 요리하는 직원)

사용자가 버튼을 클릭했을 때 무거운 작업(예: 파일 다운로드)이 시작된다고 가정해 봅시다. 싱글 스레드라면 다운로드 동안 프로그램이 멈추지만, 멀티스레드는 다운로드 중에도 다른 클릭을 처리할 수 있습니다.

[GuiResponsiveness.java](../../src/main/java/com/nhnacademy/introduction/GuiResponsiveness.java)

```java
package com.nhnacademy.introduction;

import lombok.extern.slf4j.Slf4j;

/**
 * 멀티스레드를 통한 GUI 응답성 유지 시뮬레이션입니다.
 * 쉽게 이해하기: 
 * - 메인 스레드(주방장): 주문을 받고 관리하는 역할
 * - 배경 스레드(보조 요리사): 시간이 오래 걸리는 요리를 담당
 * 보조 요리사가 요리하는 동안에도 주방장은 계속해서 손님의 주문을 받을 수 있습니다.
 */
@Slf4j
public class GuiResponsiveness {
    public static void main(String[] args) {
        log.info("GUI 프로그램이 시작되었습니다. (메인 스레드 시작)");

        // 1. 무거운 작업 요청 (예: 3초가 걸리는 데이터 처리)
        // 새로운 스레드를 만들어서 작업을 맡깁니다.
        new Thread(() -> {
            log.info("[배경 작업] 무거운 데이터 처리를 시작합니다... (3초 소요 예상)");
            try {
                Thread.sleep(3000); // 3초간 작업 시뮬레이션
            } catch (InterruptedException e) {
                log.error("[배경 작업] 오류 발생", e);
                Thread.currentThread().interrupt();
            }
            log.info("[배경 작업] 처리가 완료되었습니다!");
        }).start();

        // 2. 메인 스레드는 위의 작업이 끝나기를 기다리지 않고 즉시 다음 코드를 실행합니다.
        // 덕분에 사용자는 프로그램이 멈췄다고 느끼지 않습니다.
        log.info("[메인 작업] 사용자의 다른 입력을 계속 기다립니다 (응답성 유지).");
    }
}
```

### 1.6 Java에서의 스레드 역사 (참고)

자바는 탄생부터 멀티스레딩을 고려한 언어였습니다. 아래 내용은 자바 스레드 기술의 흐름을 이해하기 위한 참고 자료입니다.

1.  **JDK 1.0**: `Thread` 클래스와 `Runnable` 인터페이스 제공. `synchronized`를 이용한 기초적인 동기화.
2.  **JDK 5 (J2SE 5.0)**: `java.util.concurrent` (J.U.C) 패키지 등장. `ExecutorService`, `Lock`, `Atomic` 등 현대적인 동시성 도구 도입.
3.  **JDK 7**: `ForkJoinPool` 등장 (병렬 처리 최적화).
4.  **JDK 8**: `CompletableFuture`, 스트림 API의 병렬 처리 기능 도입.
5.  **JDK 21**: **가상 스레드(Virtual Threads)** 도입. 수백만 개의 스레드를 가볍게 실행할 수 있는 혁명적인 변화.
