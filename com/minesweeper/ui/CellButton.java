package com.minesweeper.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.minesweeper.common.FlagState;
import com.minesweeper.game.Board;
import com.minesweeper.game.cells.Cell;
import com.minesweeper.game.cells.EmptyCell;


public class CellButton extends JButton {
    private final Cell cell; // final은 재할당 방지
    private final Board board; // [1103_am11 추가 연쇄오픈용 보드 선언]
    
    public CellButton(Cell cell, Board board) { // [1103_am11 추가 연쇄오픈용 보드 선언]
        this.cell = cell;
        this.board = board;
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
        try {
            cell.onLeftClick();
        } catch (Exception ex) {
            // 지뢰 클릭 시 (BoomException 등)
            setText("💣");
            setEnabled(false);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "게임 종료", JOptionPane.ERROR_MESSAGE);
        }
        updateAppearance();
    }

    // 🔹 우클릭: 깃발/물음표 상태 변경
    private void handleRightClick() {
        cell.onRightClick();
        updateAppearance();
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

    public Cell getCell() {
        return cell;
    }
}
