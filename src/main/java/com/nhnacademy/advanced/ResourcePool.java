package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Semaphore;

/**
 * Semaphore를 이용한 리소스 접근 제한 예제입니다.
 * 비전공자 가이드:
 * - Semaphore: "자리가 정해진 주차장"과 같습니다.
 * - 자리가 있으면 주차하고, 자리가 없으면 다른 차가 나갈 때까지 입구에서 기다려야 합니다.
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
