package com.minesweeper.common;

// 미리 정의된 난이도별 보드 크기와 지뢰 수를 제공

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
