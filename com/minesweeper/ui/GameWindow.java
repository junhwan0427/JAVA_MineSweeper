package com.minesweeper.ui;

import javax.swing.*;
import java.awt.*;
import com.minesweeper.common.Difficulty;
import com.minesweeper.game.Board;
import com.minesweeper.game.cells.Cell;

public class GameWindow extends JFrame {
	
    private Difficulty currentDifficulty = Difficulty.EASY; // 초기화면 수정해야 함
    
    private Board board;
    private JPanel boardPanel;

    public GameWindow() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(700, 800);
        setLocationRelativeTo(null);
        setJMenuBar(buildMenuBar()); // 상단 메뉴바
        
        initBoard();
        renderBoard();

        setVisible(true);
    }

    // 메뉴바: 새 게임 + 난이도(라디오 버튼)
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("게임");
        JMenuItem newGameItem = new JMenuItem("새 게임");
        newGameItem.addActionListener(e -> newGame());
        gameMenu.add(newGameItem);

        JMenu difficultyMenu = new JMenu("난이도");
        ButtonGroup diffBtnGroup = new ButtonGroup();

        JRadioButtonMenuItem easy = new JRadioButtonMenuItem("초급 (9x9, 10)");
        JRadioButtonMenuItem normal = new JRadioButtonMenuItem("중급 (16x16, 40)");
        JRadioButtonMenuItem hard = new JRadioButtonMenuItem("고급 (16x30, 99)");

        // 초기 체크 상태
        easy.setSelected(currentDifficulty == Difficulty.EASY);
        normal.setSelected(currentDifficulty == Difficulty.NORMAL);
        hard.setSelected(currentDifficulty == Difficulty.HARD);

        // 선택 시 즉시 적용 + 새 게임
        easy.addActionListener(e -> { currentDifficulty = Difficulty.EASY; newGame(); });
        normal.addActionListener(e -> { currentDifficulty = Difficulty.NORMAL; newGame(); });
        hard.addActionListener(e -> { currentDifficulty = Difficulty.HARD; newGame(); });

        diffBtnGroup.add(easy);
        diffBtnGroup.add(normal);
        diffBtnGroup.add(hard);

        difficultyMenu.add(easy);
        difficultyMenu.add(normal);
        difficultyMenu.add(hard);

        menuBar.add(gameMenu);
        menuBar.add(difficultyMenu);
        return menuBar;
    }

    private void initBoard() {
        board = new Board(currentDifficulty);
        board.initBoard();
    }

    private void renderBoard() {
        if (boardPanel != null) remove(boardPanel);

        boardPanel = new JPanel(new GridLayout(board.getRows(), board.getCols()));
        Cell[][] cells = board.getCells();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                boardPanel.add(new CellButton(cells[r][c], board));// 1103_am11 보드 추가
            }
        }
        add(boardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void newGame() {
        initBoard();
        renderBoard();
    }
}
