package com.minesweeper.common;

import java.awt.Point;
import java.util.List;

// 셀이 처리해야 하는 좌클릭/우클릭 동작을 추상화하는 인터페이스

public interface Click {
    void onLeftClick(List<Point> openedCells);
    void onRightClick();
}