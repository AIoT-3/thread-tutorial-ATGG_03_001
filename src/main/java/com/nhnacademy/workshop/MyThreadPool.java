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
