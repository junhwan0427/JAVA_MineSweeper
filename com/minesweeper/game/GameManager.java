package com.minesweeper.game;

import com.minesweeper.common.Difficulty;
import com.minesweeper.common.GameState;

public class GameManager {
    private Board board;
    private GameState state;
    private Difficulty difficulty;

    public GameManager(Difficulty diff) {
        this.difficulty = diff;
        this.board = new Board(diff);
        this.state = GameState.READY;
    }

    public void startGame() {
        board.initBoard();
        state = GameState.PLAYING;
    }

    public GameState getState() {
        return state;
    }

    public Board getBoard() {
        return board;
    }
}
