package com.minesweeper.common;

//기능: 셀의 우클릭 상태(없음/깃발/물음표)를 표현한다.
//구조: 세 가지 열거형 상수만 가진 단순 enum.
//관계: Cell 및 Board 로직에서 깃발 제한 및 표시를 제어하는 데 사용된다 (의존/uses).

public enum FlagState {
	NONE, // // 세번 우클릭시 원상태로
	FLAGGED, // 한번 우클릭시 깃발 표시
	QUESTION // 두번 우클릭시 ? 표시
}
