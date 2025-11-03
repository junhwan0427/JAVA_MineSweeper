package com.minesweeper.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.minesweeper.common.Difficulty;
import com.minesweeper.game.Board;
import com.minesweeper.game.cells.Cell;

public class GameWindow extends JFrame {
	
    private Difficulty currentDifficulty = Difficulty.EASY; // 초기화면 수정해야 함
    
    private boolean gameFinished;
    
    private Board board;
    private JPanel boardPanel, statusPanel;
    private CellButton[][] buttons; // 버튼 빠른 접근을 위해 2차원 배열 보관
    
    private JRadioButtonMenuItem easyMenuItem, normalMenuItem, hardMenuItem;
    private JLabel difficultyLabel, timerLabel; // 게임화면 상단 내용 추가 목적
    private Timer gameTimer;
    private long startTimeMillis, elapsedMillis;
    private boolean timerStarted, timerRunning;
    
    
    
    private static final Object[] END_GAME_OPTIONS = {"새게임", "난이도 선택"}; // 게임 종료시 공통 선택지
    
    public GameWindow() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(700, 800);
        setLocationRelativeTo(null);
        setJMenuBar(buildMenuBar()); // 상단 메뉴바
        setupStatusPanel();
        setupTimer();        
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
    
    // 현재 난이도로 게임 초기화
    private void initBoard() {
        board = new Board(currentDifficulty);
        board.initBoard();
        gameFinished = false;
        resetTimer();
        updateDifficultyLabel();
    }

    // 배치 후 화면 갱신
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
    public void refreshCells(List<Point> opened) {
        for (Point p : opened) {
            buttons[p.x][p.y].refreshFromModel();
        }
    }
    // 현재 난이도로 재시작
    private void restartCurrentGame() {
        startGameWithDifficulty(currentDifficulty);
    }

    // 변경된 난이도로 재시작
    private void startGameWithDifficulty(Difficulty difficulty) {
        currentDifficulty = difficulty;
        initBoard();
        renderBoard();
        updateDifficultyMenuSelection();
    }

    // 메뉴의 선택 상태를 현재 난이도와 맞춥니다
    private void updateDifficultyMenuSelection() {
        if (easyMenuItem == null) {return;}
        easyMenuItem.setSelected(currentDifficulty == Difficulty.EASY);
        normalMenuItem.setSelected(currentDifficulty == Difficulty.NORMAL);
        hardMenuItem.setSelected(currentDifficulty == Difficulty.HARD);
    }

    public void onGameOver(String message) {
    	if (gameFinished) {return;}
    	gameFinished = true;
    	stopTimer();
    	openAllMines();
        disableAllBtn();
        String messageLine = (message == null || message.isBlank()) ? "지뢰를 클릭 했습니다!" : message;
        if ("지뢰를 클릭했습니다!".equals(messageLine)) {
            messageLine = "지뢰를 클릭 했습니다!";
        }
        String displayMessage = messageLine + "\n       Game Over";
        handleEndChoice(displayMessage, "Game Over");
    }
    
    public void checkForWin() {
        if (gameFinished || board == null) {return;}
        if (!board.getIsMinePlaced()) {return;}
        if (board.playerWinCheck()) {onGameWin();}
    }

   // 게임 종료 시 모든 버튼 비활성화
    private void disableAllBtn() {
        if (buttons == null) {return;}
        for (CellButton[] row : buttons) {
            for (CellButton button : row) {
                button.setEnabled(false);
            }
        }
    }
    
    private void onGameWin() {
        if (gameFinished) {return;}
        gameFinished = true;
        stopTimer();
        disableAllBtn();
        String timeLine = "경과 시간: " + formatElapsedTime(elapsedMillis);
        String displayMessage = "축하합니다! 승리했습니다!\n" + timeLine + "\n       Victory";
        handleEndChoice(displayMessage, "Victory");
    }

    private int showEndOptions(String displayMessage, String title) {
        return JOptionPane.showOptionDialog(
                this,
                displayMessage,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                END_GAME_OPTIONS,
                END_GAME_OPTIONS[0]
        );
    }
    
    private void handleEndChoice(String displayMessage, String title) {
        int choice = showEndOptions(displayMessage, title);
        applyEndChoice(choice);
    }
    
    private void applyEndChoice(int choice) {
        if (choice == 0) {restartCurrentGame();
        } else if (choice == 1) {
            chooseDifficulty();
        }
    }
    
    private void openAllMines() {
        if (board == null || buttons == null) {return;}

        Cell[][] cells = board.getCells();
        List<Point> minesToOpen = new ArrayList<>();

        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[r].length; c++) {
                Cell cell = cells[r][c];
                if (cell.isMine()) {
                    cell.setOpened(true);
                    minesToOpen.add(new Point(r, c));
                }
            }
        }
        refreshCells(minesToOpen);
    }
    

    private void chooseDifficulty() {
        Difficulty[] difficulties = Difficulty.values();
        String[] labels = Arrays.stream(difficulties)
                .map(Difficulty::label)
                .toArray(String[]::new);

        int selection = JOptionPane.showOptionDialog(
                this,
                "난이도를 선택하세요.",
                "난이도 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                labels,
                labels[0]
        );

        if (selection >= 0 && selection < difficulties.length) {
            startGameWithDifficulty(difficulties[selection]);
        } else {
            // 사용자가 다이얼로그를 닫은 경우 게임을 재시작하여 멈춘 상태를 방지
            restartCurrentGame();
        }
    }
    
    // 게임 상단 바
    private void setupStatusPanel() {
        if (statusPanel != null) {remove(statusPanel);}

        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        difficultyLabel = new JLabel();
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        difficultyLabel.setFont(difficultyLabel.getFont().deriveFont(Font.BOLD, 16f));

        timerLabel = new JLabel("00:00");
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timerLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

        JLabel spacer = new JLabel(" ");
        spacer.setPreferredSize(timerLabel.getPreferredSize());

        statusPanel.add(spacer, BorderLayout.WEST);
        statusPanel.add(difficultyLabel, BorderLayout.CENTER);
        statusPanel.add(timerLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.NORTH);
    }

    private void setupTimer() {
        gameTimer = new Timer(1000, e -> onTimerTick());
        gameTimer.setRepeats(true);
        resetTimer();
    }

    private void onTimerTick() {
        if (!timerRunning) {return;}
        updateElapsedMillis();
        timerLabel.setText(formatElapsedTime(elapsedMillis));
    }

    private void startTimerIfNeeded() {
        if (timerStarted || gameFinished) {return;}
        timerStarted = true;
        timerRunning = true;
        startTimeMillis = System.currentTimeMillis();
        elapsedMillis = 0L;
        timerLabel.setText(formatElapsedTime(elapsedMillis));
        if (gameTimer != null) {
            if (gameTimer.isRunning()) {
                gameTimer.restart();
            } else {
                gameTimer.start();
            }
        }
    }

    private void stopTimer() {
        if (!timerStarted || !timerRunning) {return;}
        updateElapsedMillis();
        timerRunning = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        timerLabel.setText(formatElapsedTime(elapsedMillis));
    }

    private void resetTimer() {
        timerStarted = false;
        timerRunning = false;
        elapsedMillis = 0L;
        startTimeMillis = 0L;
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        if (timerLabel != null) {
            timerLabel.setText("00:00");
        }
    }

    private void updateElapsedMillis() {
        if (!timerStarted) {
            elapsedMillis = 0L;
            return;
        }
        long now = System.currentTimeMillis();
        elapsedMillis = Math.max(0L, now - startTimeMillis);
    }

    private String formatElapsedTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 셀이 실제로 열릴 준비가 된 순간(첫 좌클릭 등)에 타이머를 시작하기 위해
     * {@link CellButton}에서 호출하는 훅입니다. 첫 클릭 이전에는 지뢰를 배치하지
     * 않으므로 UI 쪽에서 이 신호를 받아 타이머를 안전하게 켜 줍니다.
     */
    void onCellOpenInitiated() {
        startTimerIfNeeded();
    }

    private void updateDifficultyLabel() {
        if (difficultyLabel == null) {return;}
        difficultyLabel.setText(currentDifficulty.label());
    }
    
}
