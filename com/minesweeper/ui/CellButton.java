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
    private final Cell cell; // final은 재할당 방지
    private final Board board; // [1103_am11 추가 연쇄오픈용 보드 선언]
    private final GameWindow window; // ← 추가: 뷰 갱신을 창에 위임
    
    public CellButton(Cell cell, Board board, GameWindow window) { // [1103_am11 추가 연쇄오픈용 보드 선언]
        this.cell = cell;
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
        	updateAppearance();
    }

    // 🔹 좌클릭: 셀 열기
    private void handleLeftClick() {
    	boolean moveCompleted = false;
        try {
        	List<Point> opened = board.openCell(cell.getRow(), cell.getCol());
            moveCompleted = true;
            window.refreshButtons(opened); // 연쇄 여부 무관 — 열린 칸만 부분 갱신
        } catch (GameExceptions.BoomException ex) {
            window.onGameOver(ex.getMessage()); // 지뢰 클릭시 게임오버(윈도우에서 실행)
        }
        updateAppearance();
        if (moveCompleted) {
            window.checkForVictory();
        }
    }

    // 🔹 우클릭: 깃발/물음표 상태 변경
    private void handleRightClick() {
        cell.onRightClick();
        refreshFromModel();
        window.checkForVictory();
    }

    // 🔹 셀 상태에 따라 버튼 외형 갱신
    private void updateAppearance() {
    		// 열린 상태일 때
        if (cell.isOpened()) {
            setEnabled(false);
            if (cell instanceof EmptyCell emptyCell) {
                int count = emptyCell.getNearMineCount();
                setText(count == 0 ? "" : String.valueOf(count));
            } else if (cell.isMine()) {
                setText("💣");
            }
        } else {
            // 닫힌 상태일 때 깃발/물음표 표시
            FlagState flagstate = cell.getFlagState();
            switch (flagstate) {
                case FLAGGED -> setText("🚩");
                case QUESTION -> setText("❓");
                default -> setText(""); // 기본값(NONE 포함)
            }
        }
    }
    
    void refreshFromModel() {
        updateAppearance(); // 기존 1개 갱신 로직 재사용
    }
    public Cell getCell() {
        return cell;
    }
}
