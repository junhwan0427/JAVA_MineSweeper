package com.minesweeper.game.cells;

import com.minesweeper.common.FlagState;

public class MineCell extends Cell {

    public MineCell(int row, int col) {
        super(row, col);
        this.isMine = true;
    }

    @Override
    public void onLeftClick() {
        if (!cellOpened && flagState == FlagState.NONE) {
        }
    }

    @Override
    public void onRightClick() {
        nextFlagState(); // 상태 순환
    }
}
