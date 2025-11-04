package com.minesweeper.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.minesweeper.common.FlagState;
import com.minesweeper.common.GameExceptions;
import com.minesweeper.game.Board;
import com.minesweeper.game.cells.Cell;
import com.minesweeper.game.cells.EmptyCell;


public class CellButton extends JButton {
    
    // 첫 클릭 이후 지뢰를 배치하면 일부 칸의 Cell 인스턴스가 MineCell 등으로 교체된다.
    // 초기 Cell 참조를 계속 들고 있으면 최신 상태를 반영하지 못하므로 좌표만 저장해
    // 매번 보드에서 현재 셀을 조회한다.
    private final int row,col; // final은 재할당 방지    
    private final Board board; // [1103_am11 추가 연쇄오픈용 보드 선언]
    private final GameWindow window; // ← 추가: 뷰 갱신을 창에 위임
    
    public CellButton(Cell cell, Board board, GameWindow window) { // [1103_am11 추가 연쇄오픈용 보드 선언]
    	this.row = cell.getRow();
        this.col = cell.getCol();
        this.board = board;
        this.window = window;
        
        setFocusPainted(false);
        setFont(getFont().deriveFont(14f));
        setMargin(new java.awt.Insets(0,0,0,0));

        // 좌클릭 / 우클릭 이벤트 처리
        addMouseListener(new MouseAdapter() {
        	@Override
            public void mousePressed(MouseEvent e) {
                // 좌클릭
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleLeftClick();
                }
                // 우클릭
                else if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick();
                }
            }
        });
        updateCellState();
    }

    // 🔹 좌클릭: 셀 열기
    private void handleLeftClick() {
    	boolean moveCompleted = false;
        try {
        	List<Point> opened = board.openCell(row, col);
            moveCompleted = true;
            window.onCellOpenInitiated(); // 첫 클릭 시 타이머 실행
            window.refreshCells(opened); // 연쇄 여부 무관 — 열린 칸만 부분 갱신
        } catch (GameExceptions.BoomException ex) {
            window.onGameOver(ex.getMessage()); // 지뢰 클릭시 게임오버(윈도우에서 실행)
        }
        updateCellState();
        if (moveCompleted) {
            window.checkForWin();
        }
    }

    // 🔹 우클릭: 깃발/물음표 상태 변경
    private void handleRightClick() {
    	getCell().onRightClick();
        refreshFromModel();
        window.checkForWin();
    }

    // 🔹 셀 상태에 따라 버튼 외형 갱신
    private void updateCellState() {
    		// 열린 상태일 때
    	Cell cell = getCell();
        if (cell.isOpened()) {
            setEnabled(false);
            if (cell instanceof EmptyCell emptyCell) {
                int count = emptyCell.getNearMineCount();
                setText(count == 0 ? "" : String.valueOf(count));
            } else if (cell.isMine()) {
                setText("💣");
            }
        } else {
        	setEnabled(true);
            // 닫힌 상태일 때 깃발/물음표 표시
            FlagState flagState = cell.getFlagState();
            switch (flagState) {
                case FLAGGED -> setText("🚩");
                case QUESTION -> setText("❓");
                default -> setText(""); // 기본값(NONE 포함)
            }
        }
    }
    
    void refreshFromModel() {updateCellState();} // 기존 1개 갱신 로직 재사용
    public Cell getCell() {return board.getCells()[row][col];}
}
