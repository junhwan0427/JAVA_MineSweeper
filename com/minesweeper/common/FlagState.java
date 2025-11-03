package com.minesweeper.common;

public enum FlagState {
	NONE, // // 세번 우클릭시 원상태로
	FLAGGED, // 한번 우클릭시 깃발 표시
	QUESTION // 두번 우클릭시 ? 표시
}
