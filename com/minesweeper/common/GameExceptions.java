package com.minesweeper.common;

// 게임 도중 발생하는 특수 상황(폭발, 잘못된 조작)에 대한 런타임 예외 모음을 제공

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