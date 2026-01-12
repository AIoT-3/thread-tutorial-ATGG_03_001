package com.nhnacademy.introduction;

import lombok.extern.slf4j.Slf4j;

/**
 * 멀티스레드를 통한 GUI 응답성 유지 시뮬레이션입니다.
 * 비전공자 가이드: 
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
