package com.minesweeper.game.cells;

import com.minesweeper.common.FlagState;
//import com.minesweeper.exception.BoomException;

public class MineCell extends Cell {

    public MineCell(int row, int col) {
        super(row, col);
        this.isMine = true;
    }

    @Override
    public void onLeftClick() {
        if (!cellOpened && flagState == FlagState.NONE) {
//            throw new BoomException("💣 지뢰 클릭됨! 게임 오버");
        }
    }

    @Override
    public void onRightClick() {
        nextFlagState(); // 상태 순환
    }
}
