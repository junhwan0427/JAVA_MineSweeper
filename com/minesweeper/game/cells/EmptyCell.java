package com.minesweeper.game.cells;

import java.awt.Point;
import java.util.List;
import com.minesweeper.common.FlagState;
import com.minesweeper.game.Board;

//기능: 주변 지뢰 수가 표시되는 일반 셀로, 좌클릭 시 연쇄 오픈을 트리거한다.
//구조: Cell을 상속하며 nearMineCount 필드와 getter/setter를 추가 보유한다.
//관계: Board 참조를 통해 cascadeOpen을 호출하고 FlagState 로직을 재사용한다 (상속/has-a).

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
