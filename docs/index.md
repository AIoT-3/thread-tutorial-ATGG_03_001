# Java Thread Tutorial (Java 21+)

본 튜토리얼은 Java 21 환경에서의 멀티스레드 프로그래밍을 다룹니다.

## 학습 목표
1. **멀티스레딩의 핵심 원리 이해**: 프로세스와 스레드의 차이를 명확히 알고, 자바 스레드의 생명주기를 이해합니다.
2. **동기화 및 안정성 확보**: 공유 자원 관리의 중요성을 깨닫고, 경쟁 상태(Race Condition)를 방지하는 다양한 기법을 습득합니다.
3. **효율적인 리소스 관리**: 스레드 풀(Executor Framework)의 동작 원리를 파악하여 시스템 자원을 효율적으로 사용하는 법을 배웁니다.
4. **저수준 구현 역량 강화**: BlockingQueue와 스레드 풀을 직접 구현해보며 자바 동시성 라이브러리의 내부 구조를 심도 있게 이해합니다.
5. **최신 Java 기술 습득**: Java 21의 핵심인 가상 스레드(Virtual Threads)의 개념과 활용법을 익혀 현대적인 동시성 프로그래밍 능력을 갖춥니다.

## 개발 환경
- **Language**: Java 21 (LTS)
- **Build Tool**: Maven
- **Dependencies**:
    - JUnit 5 (테스트 코드 작성)
    - SLF4J & Logback (멀티스레드 로그 추적)
    - Mockito (단위 테스트 보조)
- **IDE**: VS Code 또는 IntelliJ IDEA (Java 21 지원 버전)

## 목차

### [권장 학습 일정]
- **1일차**: Introduction 및 Thread 기초 (1~2장)
- **2일차**: 스레드 동기화 및 공유 자원 관리 (3장)
- **3일차**: 고급 동기화 도구 및 Executor Framework (4~5장)
- **4일차**: [실습] 직접 만드는 스레드 풀 (6장)
- **5일차**: 실전 예제, 모범 사례 및 디버깅 (7장)
- **주말/심화**: Java 21 신기능 (Appendix A1)

### [1. Introduction](01-introduction/README.md)
- [ ] 스레드(Thread)의 어원
- [ ] 멀티스레드 프로그래밍이란? (비유: 1인 식당 vs 전문직 직원이 있는 식당)
- [ ] 싱글 스레드 vs 멀티 스레드 차이
- [ ] 프로세스와 스레드의 차이 (공장과 작업자 모델)
- [ ] 왜 멀티스레드를 사용하는가? (성능과 응답성)
- [ ] Java에서의 스레드 역사 (참고)

### [2. Thread 기초](02-thread-fundamentals/README.md)
- [ ] Thread 클래스와 Runnable 인터페이스 (비유: 요리사와 레시피)
- [ ] 스레드 생성과 실행
- [ ] 스레드 생명주기 (Thread Lifecycle) - 탄생부터 죽음까지
- [ ] 스레드 우선순위와 Daemon 스레드
- [ ] 스레드 종료와 Interruption
- [ ] 스레드 상태 전이 다이어그램 (Mermaid)

### [3. 스레드 동기화 (Synchronization) - 공유 자원 관리](03-thread-synchronization/README.md)
- [ ] 동기화가 필요한 이유 (상태 공유와 경쟁 상태)
- [ ] 임계 영역(Critical Section)과 모니터 (비유: 공중화장실과 열쇠)
- [ ] `synchronized` 키워드 사용법
- [ ] `wait()`, `notify()`, `notifyAll()`를 이용한 스레드 간 통신
- [ ] 가시성(Visibility) 문제와 `volatile`
- [ ] 데드락 방지 (락 획득 순서 강제)
- [ ] Thread Safety란 무엇인가?

### [4. 고급 동기화 도구 (java.util.concurrent)](04-advanced-synchronization/README.md)
- [ ] Lock Framework (`ReentrantLock`, `ReadWriteLock`)
- [ ] 원자적 변수 (Atomic Variables)
- [ ] 카운트다운 래치(CountDownLatch), 세마포어(Semaphore), 사이클릭 배리어(CyclicBarrier)
- [ ] 동시성 컬렉션 (Concurrent Collections)

### [5. 스레드 풀과 Executor Framework - 효율적인 스레드 관리](05-thread-pool-executor/README.md)
- [ ] 스레드를 직접 생성할 때의 문제점
- [ ] ExecutorService와 Executors 팩토리 클래스 (비유: 인력 사무소)
- [ ] 스레드 풀의 종류 (Fixed, Cached, Scheduled, Single)
- [ ] ThreadPoolExecutor의 구성과 동작 원리 (Core/Max Pool Size, Queue)
- [ ] ThreadFactory와 거절 정책 (RejectedExecutionHandler)
- [ ] 스레드 풀 종료 전략 (안전하게 멈추는 법)
- [ ] Callable, Future, 그리고 CompletableFuture (비동기 결과 받기)
- [ ] ForkJoinPool과 Work Stealing (업무 분담 알고리즘)

### [6. [실습] 직접 만드는 스레드 풀](06-custom-thread-pool-workshop/README.md)
- [ ] 업무 저장소: `BlockingQueue` 구현
- [ ] 일꾼: Worker Thread의 설계와 구현
- [ ] 관리자: 커스텀 스레드 풀 클래스 완성
- [ ] 실무 적용을 위한 테스트 및 검증

### [7. 실전 예제 및 모범 사례](07-real-world-examples-best-practices/README.md)
- [ ] 자주 발생하는 오류와 해결책
- [ ] 생산자-소비자 패턴 (식당 주방 비유)
- [ ] 파이프라인 패턴 (Pipeline Pattern)
- [ ] Thread-safe Singleton 패턴 (DCL, LazyHolder)
- [ ] 데드락(Deadlock) 탐지와 방지 (철학자들의 만찬)
- [ ] 멀티스레드 환경에서의 디버깅 팁

---

## Appendix: Java 21 신기능 (번외)

### [A1. Java 21: 가상 스레드 (Virtual Threads) - 경량 스레드](A1-virtual-threads/README.md)
- [ ] 플랫폼 스레드 vs 가상 스레드 (비용 차이)
- [ ] 가상 스레드의 아키텍처 (비유: 유능한 웨이터)
- [ ] 사용법 및 주의사항 (Pinning 현상과 해결)
- [ ] 가상 스레드를 활용한 처리량(Throughput) 개선
