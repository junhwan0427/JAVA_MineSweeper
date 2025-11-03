package com.minesweeper.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.minesweeper.common.Difficulty;
import com.minesweeper.common.FlagState;
import com.minesweeper.common.GameExceptions;
import com.minesweeper.game.cells.Cell;
import com.minesweeper.game.cells.EmptyCell;
import com.minesweeper.game.cells.MineCell;


public class Board {
	private int rows, cols, mineCount;
    private Cell[][] cells; // 2차원 셀 배열
    private Random random = new Random();

    public Board(Difficulty diff) {
        this.rows = diff.getRows();
        this.cols = diff.getCols();
        this.mineCount = diff.getMines();
        this.cells = new Cell[rows][cols];
    }

    // 전체 보드 초기화
    public void initBoard() {
        placeMines();      // 1. 지뢰 배치
        fillEmptyCells();  // 2. 나머지 빈 칸 채우기
        calculateNearMines(); // 3. 인접 지뢰 수 계산
    }

    // ✅ 1. 지뢰 배치
    private void placeMines() {
        int placed = 0;
        while (placed < mineCount) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (cells[r][c] == null) { // 중복 방지
                cells[r][c] = new MineCell(r, c);
                placed++;
            }
        }
    }

    // ✅ 2. 빈 칸 채우기
    private void fillEmptyCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == null) {
                    cells[r][c] = new EmptyCell(r, c);
                }
            }
        }
    }

    // ✅ 3. 인접 지뢰 수 계산
    private void calculateNearMines() {
    	// 주변 8칸 탐색 용도
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof EmptyCell emptyCell) {
                    int count = 0;
                    for (int i = 0; i < 8; i++) {
                        int nr = r + dr[i];
                        int nc = c + dc[i];
                        if (isInBoard(nr, nc) && cells[nr][nc].isMine()) {
                            count++;
                        }
                    }
                    emptyCell.setNearMineCount(count);
                }
            }
        }
    }

    
 // ✅ 셀 열기 (좌클릭 처리)
    public List<Point> openCell(int r, int c) {
    	    	
    	List<Point> opened = new ArrayList<>();
    	Cell cell = cells[r][c];
    	
    	if (!isInBoard(r, c)) return opened; // // 보드 밖 좌표 
        if (cell.isOpened() || cell.getFlagState() != FlagState.NONE) return opened; // // 이미 열렸거나 깃발/물음표
        
        // 지뢰 클릭 시
        if (cell.isMine()) {
            cell.setOpened(true);
            throw new GameExceptions.BoomException("지뢰를 클릭했습니다!");
        }
        
        // 정상 오픈 시
        cell.setOpened(true);
        opened.add(new Point(r, c)); // am11 추가
        
        // 주변 지뢰가 0이면 연쇄 오픈 시작
        if (cell instanceof EmptyCell empty && empty.getNearMineCount() == 0) {
        	bfsOpen(r, c,opened);
        }
        return opened;
    }

    // ✅ 연쇄 오픈 (BFS_너비우선탐색 방식)
    private void bfsOpen(int r, int c, List<Point> opened) {
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(r, c));

        while (!queue.isEmpty()) {
        	Point cur = queue.poll();        	            
            for (int i = 0; i < 8; i++) {
                int nr = cur.x + dr[i], nc = cur.y + dc[i];
                if (!isInBoard(nr, nc)) continue;

                Cell neighbor = cells[nr][nc];
                if (neighbor.isOpened() || neighbor.isMine() || neighbor.getFlagState() != FlagState.NONE) continue;

                neighbor.setOpened(true);
                opened.add(new Point(nr, nc));
                
                // 주변 지뢰가 0이면 계속 확장
                if (neighbor instanceof EmptyCell e && e.getNearMineCount() == 0) {
                    queue.add(new Point(nr, nc));
                }
            }
        }
    }    
    
    
    
    // 유효 범위 확인
    private boolean isInBoard(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    // 보드 가져오기 (UI나 Controller에서 접근용)
    public Cell[][] getCells() {return cells;}
    // 행/열 반환 (UI용)
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    
    
    
    
    
    
    
}
