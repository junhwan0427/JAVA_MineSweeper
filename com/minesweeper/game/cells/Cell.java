package com.minesweeper.game.cells;

import java.awt.Point;
import java.util.List;
import com.minesweeper.common.Click;
import com.minesweeper.common.FlagState;
import com.minesweeper.game.Board;

public abstract class Cell implements Click {
	protected final Board board; 	  // 클릭 시 보드 조작을 위함
    protected boolean cellOpened;     // 칸이 열렸는가
    protected int row, col;           // 좌표
    protected boolean isMine;         // 지뢰 여부
    protected FlagState flagState;    // 깃발 상태 (NONE, FLAGGED, QUESTION)

    protected Cell(Board board, int row, int col) { // [NEW] 보드 참조 주입
        this.board = board;
        this.row = row;
        this.col = col;
        this.cellOpened = false;
        this.flagState = FlagState.NONE;
    }

    public boolean isOpened() { return cellOpened; }
    public boolean isMine() { return isMine; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    
    public void setOpened(boolean opened) {this.cellOpened = opened;}
    
    public FlagState getFlagState() { return flagState; }
    
    public void setFlagState(FlagState flagState) {
        this.flagState = flagState;
    }
    
    protected void markOpened(List<Point> opened) { // [NEW] 공통 오픈 처리
        cellOpened = true;
        opened.add(new Point(row, col));
    }
    
    // 우클릭 시 상태 순환: NONE → FLAGGED → QUESTION → NONE
    public void nextFlagState() {
        switch (flagState) {
        // 람다식 스위치문은 break 생략 가능
        	case NONE -> {
            board.checkFlagLimit(); // [NEW]
            flagState = FlagState.FLAGGED;
        }
            case FLAGGED -> flagState = FlagState.QUESTION;
            case QUESTION -> flagState = FlagState.NONE;
        }
    }

    @Override
    public abstract void onLeftClick(List<Point> openedCells);

    @Override
    public abstract void onRightClick();
}
