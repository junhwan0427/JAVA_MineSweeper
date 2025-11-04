package com.minesweeper.common;

// 셀의 우클릭 상태(없음/깃발/물음표)를 표현

public enum FlagState {
	NONE, // // 세번 우클릭시 원상태로
	FLAGGED, // 한번 우클릭시 깃발 표시
	QUESTION // 두번 우클릭시 ? 표시
}
