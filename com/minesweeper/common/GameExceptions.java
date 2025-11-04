package com.minesweeper.common;

//기능: 게임 도중 발생하는 특수 상황(폭발, 잘못된 조작)에 대한 런타임 예외 모음을 제공한다.
//구조: 인스턴스화를 방지하는 유틸성 클래스와 두 개의 static inner RuntimeException 클래스로 구성된다.
//관계: MineCell/CellButton/Board에서 예외를 발생시켜 GameWindow(UI)로 흐름을 전달한다 (의존/uses).

public final class GameExceptions {

    private GameExceptions() {} // 인스턴스화 방지

    // 지뢰 클릭 → 게임 종료
    public static class BoomException extends RuntimeException {
        public BoomException(String msg) { super(msg); }
    }

    // 말이 안 되는 조작(보드 밖 좌표, 열린 칸에 깃발 등)
    public static class InvalidActionException extends RuntimeException {
        public InvalidActionException(String msg) { super(msg); }
    }
}