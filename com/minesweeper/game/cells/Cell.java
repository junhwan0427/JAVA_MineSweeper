package com.minesweeper.game.cells;

import java.awt.Point;
import java.util.List;
import com.minesweeper.common.Click;
import com.minesweeper.common.FlagState;
import com.minesweeper.game.Board;

//기능: 지뢰/빈 칸이 공통으로 따르는 셀 동작과 상태(좌표, 열림, 깃발)를 정의한다.
//구조: 추상 클래스이며 Board 참조와 공통 필드를 보유하고 Click 인터페이스를 구현한다.
//관계: Board와 연관(has-a)되어 보드 연산을 호출하고, EmptyCell/MineCell이 상속(is-a)하며 Click 인터페이스를 실체화(implements)한다.

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
