package com.minesweeper.common;


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

    // 필요 시 추가 예외 예시
    public static class AlreadyOpenedException extends RuntimeException {
        public AlreadyOpenedException(String msg) { super(msg); }
    }

    public static class GameLockedException extends RuntimeException {
        public GameLockedException(String msg) { super(msg); }
    }
}