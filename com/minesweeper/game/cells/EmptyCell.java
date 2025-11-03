package com.minesweeper.game.cells;

import com.minesweeper.common.FlagState;

public class EmptyCell extends Cell {
    private int nearMineCount; // 주변 지뢰 개수

    public EmptyCell(int row, int col) {
        super(row, col);
        this.isMine = false;
        this.nearMineCount = 0;
    }

    public void setNearMineCount(int count) {
        this.nearMineCount = count;
    }

    public int getNearMineCount() {
        return nearMineCount;
    }

    @Override
    public void onLeftClick() {
        if (!cellOpened && flagState == FlagState.NONE) {
            cellOpened = true;
            // TODO: 빈칸일 경우 인접 칸 자동 열기 (Board에서 처리)
        }
    }

    @Override
    public void onRightClick() {
        nextFlagState(); // 상태 순환
    }
}
