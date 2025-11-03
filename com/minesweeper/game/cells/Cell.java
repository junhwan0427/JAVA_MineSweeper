package com.minesweeper.game.cells;

import com.minesweeper.common.Click;
import com.minesweeper.common.FlagState;

public abstract class Cell implements Click {
    protected boolean cellOpened;     // 칸이 열렸는가
    protected int row, col;           // 좌표
    protected boolean isMine;         // 지뢰 여부
    protected FlagState flagState;    // 깃발 상태 (NONE, FLAGGED, QUESTION)

    public Cell(int row, int col) {
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
    
    // 우클릭 시 상태 순환: NONE → FLAGGED → QUESTION → NONE
    public void nextFlagState() {
        switch (flagState) {
        // 람다식 스위치문은 break 생략 가능
            case NONE -> flagState = FlagState.FLAGGED;
            case FLAGGED -> flagState = FlagState.QUESTION;
            case QUESTION -> flagState = FlagState.NONE;
        }
    }

    @Override
    public abstract void onLeftClick();

    @Override
    public abstract void onRightClick();
}
