package com.minesweeper;

import javax.swing.SwingUtilities;
import com.minesweeper.ui.GameWindow;


// 애플리케이션 진입점을 제공
public class MinesweeperMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameWindow(); // UI 창 실행
        });
    }
}
