package com.nhnacademy.pool;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory를 구현하여 스레드 생성을 커스텀하는 예제입니다.
 * 비전공자 가이드:
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
