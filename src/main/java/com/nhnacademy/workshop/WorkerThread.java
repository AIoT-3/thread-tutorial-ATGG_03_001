package com.nhnacademy.workshop;

import lombok.extern.slf4j.Slf4j;

/**
 * 스레드 풀 내부에서 실제로 일을 하는 일꾼(Worker) 스레드입니다.
 * 비전공자 가이드:
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
