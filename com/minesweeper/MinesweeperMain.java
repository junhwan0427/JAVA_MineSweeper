package com.minesweeper;

import javax.swing.SwingUtilities;
import com.minesweeper.ui.GameWindow;

public class MinesweeperMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameWindow(); // UI 창 실행
        });
    }
}
