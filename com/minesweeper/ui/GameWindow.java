package com.minesweeper.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.minesweeper.common.Difficulty;
import com.minesweeper.game.Board;
import com.minesweeper.game.cells.Cell;

// 전체 게임 창을 구성하고 보드 렌더링, 메뉴, 상태 표시, 종료 처리 등 UI 흐름을 제어
// JFrame을 상속받아 메뉴바/상태패널/보드패널을 포함하며, 난이도 변경과 보드 갱신 로직을 메서드로 분리함
// Board를 합성하여 게임 데이터를 관리하고 CellButton/TimerPanel과 has-a 연관을 맺으며, Difficulty와 Swing 컴포넌트에 의존한다.

public class GameWindow extends JFrame {
	
    private Difficulty currentDifficulty = Difficulty.EASY; // 초기화면
    
    private boolean gameFinished;
    
    private Board board;
    private JPanel boardPanel,boardContainer, statusPanel;
    private CellButton[][] buttons; // 버튼 빠른 접근을 위해 2차원 배열 보관
    
    private JRadioButtonMenuItem easyMenuItem, normalMenuItem, hardMenuItem;
    private JLabel difficultyLabel; // 게임화면 상단 내용 추가 목적
    private TimerPanel timerPanel; // 타이머 추가
    
    
    
    private static final Object[] END_GAME_OPTIONS = {"새게임", "난이도 선택"}; // 게임 종료시 공통 선택지
    private static final int MIN_CELL_SIZE = 32;
    private static final int MAX_CELL_SIZE = 80;
    
    public GameWindow() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(700, 800);
        setLocationRelativeTo(null);
        setJMenuBar(buildMenuBar()); // 상단 메뉴바
        setupStatusPanel();      
        initBoard();
        renderBoard();
        
        setupWindowResizeHandler();// 셀 사이즈 조정
        
        setVisible(true);
        SwingUtilities.invokeLater(this::updateBoardSizing);
    }
    
    private void setupWindowResizeHandler() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateBoardSizing();
            }
        });
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
        if (timerPanel != null) {timerPanel.resetTimer();}
        updateDifficultyLabel();
    }

    // 배치 후 화면 갱신
    private void renderBoard() {
    	if (boardContainer != null) {remove(boardContainer);}

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
        
        // 셀크기 조정
        boardContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        boardContainer.add(boardPanel, gbc);

        add(boardContainer, BorderLayout.CENTER);
        updateBoardSizing();
        
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
        SwingUtilities.invokeLater(this::updateBoardSizing);
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
    	if (timerPanel != null) {timerPanel.stopTimer();}
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
        if (timerPanel != null) {timerPanel.stopTimer();}
        disableAllBtn();
        String timeLine = "경과 시간: " + (timerPanel != null ? timerPanel.getFormattedElapsedTime() : "00:00");
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
    
    // 셀 사이즈 조절
    private void updateBoardSizing() {
        if (boardPanel == null || board == null) {return;}

        int rows = board.getRows();
        int cols = board.getCols();

        Dimension contentSize = getContentPane().getSize();
        int availableWidth = contentSize.width;
        int availableHeight = contentSize.height;

        if (statusPanel != null) {
            availableHeight -= statusPanel.getHeight();
        }

        if (availableWidth <= 0 || availableHeight <= 0) {
            Dimension pref = boardPanel.getPreferredSize();
            availableWidth = Math.max(availableWidth, pref.width);
            availableHeight = Math.max(availableHeight, pref.height);
        }

        int cellWidth = availableWidth / Math.max(1, cols);
        int cellHeight = availableHeight / Math.max(1, rows);
        int cellSize = Math.min(cellWidth, cellHeight);
        if (cellSize <= 0) {cellSize = MIN_CELL_SIZE;}

        cellSize = Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, cellSize));

        Dimension preferred = new Dimension(cellSize * cols, cellSize * rows);
        boardPanel.setPreferredSize(preferred);
        boardPanel.setMinimumSize(new Dimension(MIN_CELL_SIZE * cols, MIN_CELL_SIZE * rows));
        boardPanel.setMaximumSize(new Dimension(MAX_CELL_SIZE * cols, MAX_CELL_SIZE * rows));

        if (boardContainer != null) {
            boardContainer.revalidate();
        }
        boardPanel.revalidate();
        boardPanel.repaint();

        updateFrameMinimumSize();
    }

    private void updateFrameMinimumSize() {
        if (board == null) {return;}

        int rows = board.getRows();
        int cols = board.getCols();

        int minBoardWidth = MIN_CELL_SIZE * cols;
        int minBoardHeight = MIN_CELL_SIZE * rows;

        int statusWidth = 0;
        int statusHeight = 0;
        if (statusPanel != null) {
            Dimension statusPref = statusPanel.getPreferredSize();
            statusWidth = statusPref.width;
            statusHeight = statusPref.height;
        }

        Insets insets = getInsets();
        int minWidth = Math.max(minBoardWidth, statusWidth) + insets.left + insets.right;
        int minHeight = minBoardHeight + statusHeight + insets.top + insets.bottom;

        setMinimumSize(new Dimension(minWidth, minHeight));
    }
        
    // 게임 상단 바
    private void setupStatusPanel() {
        if (statusPanel != null) {remove(statusPanel);}

        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        difficultyLabel = new JLabel();
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        difficultyLabel.setFont(difficultyLabel.getFont().deriveFont(Font.BOLD, 16f));

        timerPanel = new TimerPanel();

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(timerPanel.getPreferredSize());

        statusPanel.add(spacer, BorderLayout.WEST);
        statusPanel.add(difficultyLabel, BorderLayout.CENTER);
        statusPanel.add(timerPanel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.NORTH);
        updateFrameMinimumSize();
    }

    // CellButton에서 첫 좌클릭 감지로 타이머 시작
    void onCellOpenInitiated() {
    	if (gameFinished || timerPanel == null) {return;}
        timerPanel.startTimerIfNeeded();
    }

    private void updateDifficultyLabel() {
        if (difficultyLabel == null) {return;}
        difficultyLabel.setText(currentDifficulty.label());
    }
    
}
