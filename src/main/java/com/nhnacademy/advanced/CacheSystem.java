package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock을 이용한 데이터 읽기 성능 최적화 예제입니다.
 * 비전공자 가이드: 
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
