 package com.minesweeper.game.cells;

import java.awt.Point;
import java.util.List;
import com.minesweeper.common.FlagState;
import com.minesweeper.common.GameExceptions;
import com.minesweeper.game.Board;

public class MineCell extends Cell {

	public MineCell(Board board, int row, int col) { // [NEW] 보드 전달
        super(board, row, col);
        this.isMine = true;
    }

    @Override
    public void onLeftClick(List<Point> openedCells) {
        if (cellOpened || flagState != FlagState.NONE) {
            return;
        }
        markOpened(openedCells);
        throw new GameExceptions.BoomException("지뢰를 클릭했습니다!");
    }

    @Override
    public void onRightClick() {
        nextFlagState(); // 상태 순환
    }
}
