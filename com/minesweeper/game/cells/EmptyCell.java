package com.minesweeper.game.cells;

import java.awt.Point;
import java.util.List;
import com.minesweeper.common.FlagState;
import com.minesweeper.game.Board;

public class EmptyCell extends Cell {
    private int nearMineCount; // 주변 지뢰 개수

    public EmptyCell(Board board, int row, int col) { // [NEW] 보드 전달
        super(board, row, col);
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
    public void onLeftClick(List<Point> openedCells) {
        if (cellOpened || flagState != FlagState.NONE) {
            return;
        }

        markOpened(openedCells);
        if (nearMineCount == 0) {
            board.cascadeOpen(row, col, openedCells); // [NEW] 빈 칸 연쇄 오픈
        }
    }

    @Override
    public void onRightClick() {
        nextFlagState(); // 상태 순환
    }
}
