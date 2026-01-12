package com.nhnacademy.practices;

import lombok.extern.slf4j.Slf4j;

/**
 * 멀티스레드 환경에서 안전한 싱글톤(Singleton) 패턴 예제입니다. (Double-Checked Locking)
 * 비전공자 가이드:
 * - 싱글톤: "세상에 단 하나뿐인 객체"를 만드는 디자인 패턴입니다.
 * - 멀티스레드에서는 여러 명이 동시에 만들려고 하면 여러 개가 생길 수 있어 주의해야 합니다.
 */
@Slf4j
public class SafeSingleton {
    // volatile: 여러 스레드가 동시에 이 변수를 볼 때 최신 값을 보장합니다.
    private static volatile SafeSingleton instance;

    private SafeSingleton() {
        // 외부에서 뉴(new) 하지 못하게 생성자를 숨깁니다.
    }

    /**
     * 객체를 가져옵니다. 없으면 새로 만듭니다.
     */
    public static SafeSingleton getInstance() {
        if (instance == null) { // 1차 확인 (자물쇠 없이 빠르게)
            synchronized (SafeSingleton.class) { // 자물쇠 잠그기
                if (instance == null) { // 2차 확인 (자물쇠 안에서 한 번 더)
                    log.info("--- 유일한 객체를 새로 생성합니다. ---");
                    instance = new SafeSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        // 두 명의 작업자가 동시에 객체를 달라고 합니다.
        Thread t1 = new Thread(() -> {
            SafeSingleton s = SafeSingleton.getInstance();
            log.info("작업자 1의 객체 주소: {}", s);
        });

        Thread t2 = new Thread(() -> {
            SafeSingleton s = SafeSingleton.getInstance();
            log.info("작업자 2의 객체 주소: {}", s);
        });

        t1.start();
        t2.start();
    }
}
