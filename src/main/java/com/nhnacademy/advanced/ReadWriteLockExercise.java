package com.nhnacademy.advanced;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock을 이용한 데이터 읽기 성능 최적화 실습입니다.
 * 
 * TODO#4-3: ReadWriteLock을 사용하여 읽기 작업은 공유(Shared)되고, 
 * 쓰기 작업은 배타적(Exclusive)으로 수행되도록 구현하세요.
 * 참고 링크: https://www.baeldung.com/java-concurrent-locks
 */
@Slf4j
public class ReadWriteLockExercise {
    private final Map<String, String> cache = new HashMap<>();
    
    // TODO#4-3-1: ReadWriteLock과 그에 따른 readLock, writeLock을 선언하세요.
    private final ReadWriteLock rwLock = null;
    private final Lock readLock = null;
    private final Lock writeLock = null;

    public String get(String key) {
        // TODO#4-3-2: readLock을 사용하여 데이터를 안전하게 가져오도록 구현하세요.
        return null;
    }

    public void put(String key, String value) {
        // TODO#4-3-3: writeLock을 사용하여 데이터를 안전하게 저장하도록 구현하세요.
    }

    public static void main(String[] args) {
        ReadWriteLockExercise exercise = new ReadWriteLockExercise();
        exercise.put("key1", "Initial Data");

        // 여러 스레드에서 동시에 읽기 시도
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                log.info("Read: {}", exercise.get("key1"));
            }).start();
        }

        // 하나의 스레드에서 쓰기 시도
        new Thread(() -> {
            exercise.put("key1", "Updated Data");
            log.info("Data Updated!");
        }).start();
    }
}
