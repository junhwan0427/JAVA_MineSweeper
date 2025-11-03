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

    private CellButton[][] buttons; // 버튼 빠른 접근을 위해 2차원 배열 보관

    private JRadioButtonMenuItem easyMenuItem;
    private JRadioButtonMenuItem normalMenuItem;
    private JRadioButtonMenuItem hardMenuItem;
    
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
        newGameItem.addActionListener(e -> restartCurrentGame());
        gameMenu.add(newGameItem);

        JMenu difficultyMenu = new JMenu("난이도");
        ButtonGroup diffBtnGroup = new ButtonGroup();

        easyMenuItem = new JRadioButtonMenuItem("초급 (9x9, 10)");
        normalMenuItem = new JRadioButtonMenuItem("중급 (16x16, 40)");
        hardMenuItem = new JRadioButtonMenuItem("고급 (16x30, 99)");

        // 초기 체크 상태
        easyMenuItem.setSelected(currentDifficulty == Difficulty.EASY);
        normalMenuItem.setSelected(currentDifficulty == Difficulty.NORMAL);
        hardMenuItem.setSelected(currentDifficulty == Difficulty.HARD);

        // 선택 시 즉시 적용 + 새 게임
        easyMenuItem.addActionListener(e -> startGameWithDifficulty(Difficulty.EASY));
        normalMenuItem.addActionListener(e -> startGameWithDifficulty(Difficulty.NORMAL));
        hardMenuItem.addActionListener(e -> startGameWithDifficulty(Difficulty.HARD));

        diffBtnGroup.add(easyMenuItem);
        diffBtnGroup.add(normalMenuItem);
        diffBtnGroup.add(hardMenuItem);

        difficultyMenu.add(easyMenuItem);
        difficultyMenu.add(normalMenuItem);
        difficultyMenu.add(hardMenuItem);

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

        int R = board.getRows(), C = board.getCols();
        buttons = new CellButton[R][C]; // 2차원 배열 준비
        
        boardPanel = new JPanel(new GridLayout(R, C));
        Cell[][] cells = board.getCells();

        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                CellButton b = new CellButton(cells[r][c], board, this); // GameWindow 참조 전달
                buttons[r][c] = b; // ← 보관
                boardPanel.add(b);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // 부분 갱신 전용
    public void refreshButtons(java.util.List<Point> opened) {
        for (Point p : opened) {
            buttons[p.x][p.y].refreshFromModel();
        }
    }

    private void restartCurrentGame() {
        startGameWithDifficulty(currentDifficulty);
    }

    private void startGameWithDifficulty(Difficulty difficulty) {
        currentDifficulty = difficulty;
        initBoard();
        renderBoard();
        updateDifficultyMenuSelection();
    }

    private void updateDifficultyMenuSelection() {
        if (easyMenuItem == null) {
            return;
        }
        easyMenuItem.setSelected(currentDifficulty == Difficulty.EASY);
        normalMenuItem.setSelected(currentDifficulty == Difficulty.NORMAL);
        hardMenuItem.setSelected(currentDifficulty == Difficulty.HARD);
    }

    public void onGameOver(String message) {
        disableBoardInteraction();
        Object[] options = {"새게임", "난이도 선택"};
        String messageLine = (message == null || message.isBlank()) ? "지뢰를 클릭 했습니다!" : message;
        if ("지뢰를 클릭했습니다!".equals(messageLine)) {
            messageLine = "지뢰를 클릭 했습니다!";
        }
        String displayMessage = messageLine + "\n       Game Over";
        int choice = JOptionPane.showOptionDialog(
                this,
                displayMessage,
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            restartCurrentGame();
        } else if (choice == 1) {
            showDifficultySelectionDialog();
        }
    }

    private void disableBoardInteraction() {
        if (buttons == null) {
            return;
        }
        for (CellButton[] row : buttons) {
            for (CellButton button : row) {
                button.setEnabled(false);
            }
        }
    }

    private void showDifficultySelectionDialog() {
        Object[] options = {
                difficultyLabel(Difficulty.EASY),
                difficultyLabel(Difficulty.NORMAL),
                difficultyLabel(Difficulty.HARD)
        };

        int selection = JOptionPane.showOptionDialog(
                this,
                "난이도를 선택하세요.",
                "난이도 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selection >= 0 && selection < options.length) {
            Difficulty chosen = switch (selection) {
                case 0 -> Difficulty.EASY;
                case 1 -> Difficulty.NORMAL;
                case 2 -> Difficulty.HARD;
                default -> currentDifficulty;
            };
            startGameWithDifficulty(chosen);
        } else {
            // 사용자가 다이얼로그를 닫은 경우 게임을 재시작하여 멈춘 상태를 방지
            restartCurrentGame();
        }
    }

    private String difficultyLabel(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> "초급 (9x9, 10)";
            case NORMAL -> "중급 (16x16, 40)";
            case HARD -> "고급 (16x30, 99)";
        };
    }
}
