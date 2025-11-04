package com.minesweeper.common;

import java.awt.Point;
import java.util.List;

public interface Click {
    void onLeftClick(List<Point> openedCells);
    void onRightClick();
}