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
	private static final int[] NEIGHBOR_ROW = {-1, -1, -1, 0, 0, 1, 1, 1};// 주변 8칸 조회 용도
    private static final int[] NEIGHBOR_COL = {-1, 0, 1, -1, 1, -1, 0, 1};
	private int rows, cols, mineCount;
    private Cell[][] cells; // 2차원 셀 배열
    private Random random = new Random();
    private boolean isMinePlaced;
    
    public Board(Difficulty diff) {
        this.rows = diff.getRows();
        this.cols = diff.getCols();
        this.mineCount = diff.getMines();
        this.cells = new Cell[rows][cols];
    }

    // 전체 보드 초기화
    public void initBoard() {
    	cells = new Cell[rows][cols];
    	fillEmptyCells();
    	isMinePlaced = false; // 지뢰 없음 
        
    	// 첫 클릭은 지뢰를 안나오게 하기위한 initBoard 수정
//        placeMines();      // 1. 지뢰 배치
//        fillEmptyCells();  // 2. 나머지 빈 칸 채우기
//        calculateNearMines(); // 3. 인접 지뢰 수 계산
    }

    // ✅ 1. 지뢰 배치
    private void placeMines(int firstRow, int firstCol) { // 첫 셀 클릭
        int placed = 0;
        int safeZoneSize = countSafeZoneCells(firstRow, firstCol); // safeZone보다 지뢰가 많을 경우 (사용자설정 난이도 추가 대비)
        if (rows * cols - safeZoneSize < mineCount) {
            throw new IllegalStateException("지뢰 개수가 보드 크기 대비 너무 많습니다.");
        }
        
        while (placed < mineCount) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            
            if (isInSafeZone(r, c, firstRow, firstCol)) {continue;}
            Cell current = cells[r][c];
            
            if (current.isMine()) {continue;}
            MineCell mine = new MineCell(r, c);
            
            mine.setFlagState(current.getFlagState());
            mine.setOpened(current.isOpened());
            cells[r][c] = mine;
            placed++;
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
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof EmptyCell emptyCell) {
                	emptyCell.setNearMineCount(0);
                    int count = 0;
                    for (int i = 0; i < NEIGHBOR_ROW.length; i++) {
                        int nr = r + NEIGHBOR_ROW[i];
                        int nc = c + NEIGHBOR_COL[i];
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

        if (!isInBoard(r, c)) return opened; // // 보드 밖 좌표
        Cell cell = cells[r][c];
        if (cell.isOpened() || cell.getFlagState() != FlagState.NONE) return opened; // // 이미 열렸거나 깃발/물음표
        
        // 첫 클릭 반영한 지뢰 설정
        safeMinesPlaced(r, c);
        cell = cells[r][c];
        
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
        	neighborCellOpen(r, c,opened);
        }
        return opened;
    }

    // ✅ 연쇄 오픈 (BFS_너비우선탐색 방식)
    private void neighborCellOpen(int r, int c, List<Point> opened) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(r, c));

        while (!queue.isEmpty()) {
        	Point cur = queue.poll();        	            
        	for (int i = 0; i < NEIGHBOR_ROW.length; i++) {
                int nr = cur.x + NEIGHBOR_ROW[i], nc = cur.y + NEIGHBOR_COL[i];
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
    
    //첫 클릭 위치를 기준으로 3x3 안전 구역(첫 칸과 인접 8칸)을 설정
    private boolean isInSafeZone(int r, int c, int firstRow, int firstCol) {
        return Math.abs(r - firstRow) <= 1 && Math.abs(c - firstCol) <= 1;
    }

    private int countSafeZoneCells(int firstRow, int firstCol) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = firstRow + dr;
                int nc = firstCol + dc;
                if (isInBoard(nr, nc)) {
                    count++;
                }
            }
        }
        return count;
    }

    private void safeMinesPlaced(int firstRow, int firstCol) {
        if (isMinePlaced) {
            return;
        }
        placeMines(firstRow, firstCol);
        calculateNearMines();
        isMinePlaced = true;
    }
    

    // 보드 가져오기 (UI나 Controller에서 접근용)
    public Cell[][] getCells() {return cells;}
    // 행/열 반환 (UI용)
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    
    public boolean playerWinCheck() {
    	if (!isMinePlaced) {return false;}
    	
        boolean allNonMineOpened = true;
        boolean allMinesFlagged = true;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];

                if (cell.isMine()) {
                    if (cell.getFlagState() != FlagState.FLAGGED) {
                        allMinesFlagged = false;
                    }
                } else {
                    if (cell.getFlagState() == FlagState.FLAGGED) {
                        return false; // 지뢰가 아닌 칸에 깃발이 있으면 승리 아님
                    }
                    if (!cell.isOpened()) {
                        allNonMineOpened = false;
                    }
                }
            }
        }

        return allNonMineOpened || allMinesFlagged;
    }
    
    public boolean getIsMinePlaced() {
        return isMinePlaced;
    }
    
    
    
}
