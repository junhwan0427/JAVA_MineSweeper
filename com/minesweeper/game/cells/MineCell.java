 package com.minesweeper.game.cells;

import java.awt.Point;
import java.util.List;
import com.minesweeper.common.FlagState;
import com.minesweeper.common.GameExceptions;
import com.minesweeper.game.Board;

//기능: 지뢰 셀을 표현하며 좌클릭 시 폭발 예외를 발생시킨다.
//구조: Cell을 상속하고 추가 상태 없이 동작만 재정의한다.
//관계: Board 참조를 부모로부터 받아 GameExceptions.BoomException을 통해 상위(UI)에 이벤트를 전달한다.

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
