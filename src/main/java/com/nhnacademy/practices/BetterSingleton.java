package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;

/**
 * 가장 권장되는 방식의 싱글톤 패턴(LazyHolder) 예제입니다.
 * 비전공자 가이드:
 * - 자바 언어의 특성을 이용해 자물쇠(synchronized) 없이도 완벽하게 안전한 싱글톤을 만듭니다.
 * - 객체가 실제로 필요할 때까지 생성을 미루다가(Lazy), 필요한 순간에 딱 한 번만 만듭니다.
 */
@Slf4j
public class BetterSingleton {
    private BetterSingleton() {
        // 생성자를 숨깁니다.
    }

    /**
     * 내부 클래스를 사용하여 객체를 보관합니다.
     * 이 클래스는 getInstance()가 호출될 때 처음 로딩되며, 이때 단 한 번 객체가 생성됩니다.
     */
    private static class Holder {
        private static final BetterSingleton INSTANCE = new BetterSingleton();
    }

    public static BetterSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public static void main(String[] args) {
        log.info("싱글톤 테스트를 시작합니다.");
        
        Thread t1 = new Thread(() -> {
            BetterSingleton s = BetterSingleton.getInstance();
            log.info("작업자 1의 객체 주소: {}", s);
        });

        Thread t2 = new Thread(() -> {
            BetterSingleton s = BetterSingleton.getInstance();
            log.info("작업자 2의 객체 주소: {}", s);
        });

        t1.start();
        t2.start();
    }
}
