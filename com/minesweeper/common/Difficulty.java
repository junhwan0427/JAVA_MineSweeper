package com.minesweeper.common;

//기능: 미리 정의된 난이도별 보드 크기와 지뢰 수를 제공한다.
//구조: EASY/NORMAL/HARD 열거형 상수와 파생 메서드(label 등)를 포함한다.
//관계: GameWindow와 Board에서 설정 정보를 읽어 보드 구성에 사용한다 (의존/uses).

public enum Difficulty {
    EASY(9, 9, 10),
    NORMAL(16, 16, 40),
    HARD(16, 30, 99);

    private final int rows;
    private final int cols;
    private final int mines;

    Difficulty(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getMines() { return mines; }
    
    // 옵션용 난이도 라벨
    public String label() {
        return switch (this) {
            case EASY -> "초급 (9x9, 10)";
            case NORMAL -> "중급 (16x16, 40)";
            case HARD -> "고급 (16x30, 99)";
        };
    }
}
