package com.minesweeper.common;

import java.awt.Point;
import java.util.List;

//기능: 셀이 처리해야 하는 좌클릭/우클릭 동작을 추상화하는 인터페이스.
//구조: 두 개의 입력 처리 메서드를 선언하여 구현 클래스가 클릭 결과를 정의하도록 강제한다.
//관계: Cell 계층이 이를 실체화(implements)하여 UI에서 발생한 입력을 모델 동작으로 연결한다.

public interface Click {
    void onLeftClick(List<Point> openedCells);
    void onRightClick();
}