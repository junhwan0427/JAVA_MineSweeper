# JAVA 지뢰찾기

Java 기반으로 구현한 지뢰찾기 게임 프로젝트입니다. 

난이도에 따라 보드 크기와 지뢰 개수가 달라지고, 첫 클릭은 항상 지뢰를 피하도록 설계되었습니다. 

## 1. 주요 기능
- **난이도 선택**: 초급/중급/고급 난이도를 메뉴에서 선택하거나 종료 후 다시 선택 가능
- **첫 클릭 보호**: 첫 좌클릭 시 해당 칸과 주변 8칸에 지뢰 미배치
- **자동 연쇄 오픈**: 빈 칸을 열면 BFS 방식으로 인접한 빈 칸을 자동 연쇄 오픈
- **깃발/물음표 표시**: 우클릭할 때마다 `🚩 → ❓ → (빈칸)` 순환으로 상태 변경
- **타이머 및 상태 표시줄**: 현재 난이도와 경과 시간을 상단 패널에 표기
- **반응형 보드**: 창 크기에 따라 각 칸의 크기를 자동 조정

## 2. 핵심 기술
- 객체지향 프로그래밍(OOP)
  - 상속, 다형성, 캡슐화
- 인터페이스
- 예외 처리
- 컬렉션 프레임워크
- 열거형(Enum)
  - Difficulty, FlagState enum을 통해 난이도/깃발 상태를 상수로 관리해 가독성과 유지보수성 향상
- Swing UI 컴포넌트 활용

## 3. 구현 내용

### 3-1. try-catch 예외 처리문

- **폭발 처리**
  - 발생: MineCell.onLeftClick() → throw new GameExceptions.BoomException("지뢰를 클릭했습니다!")
  - 처리: CellButton.handleLeftClick() catch(BoomException) → GameWindow.onGameOver(message) 
- **잘못된 조작(규칙 위반)**
  - 발생: Board.checkFlagLimit()(깃발 과다), 경계 체크 등 → InvalidActionException
  - 처리:
    - 우클릭 시 CellButton.handleRightClick() catch(InvalidActionException) → JOptionPane 경고
    - 좌클릭/기타 로직에서는 조용히 무시(보드 밖, 이미 열린 칸 등은 빈 리스트 반환)

### 3-2. 상속과 인터페이스 설계
- Click 인터페이스를 정의해 모든 셀이 공통적으로 좌클릭/우클릭 동작 규약을 정의
- Cell은 지뢰(MineCell)와 빈 칸(EmptyCell)이 **공통으로 수행해야 하는 클릭 동작과 상태 관리(열림, 좌표, 깃발 상태 등)**를 정의한 추상 부모 클래스
- 두 하위 클래스는 이를 상속받아 좌클릭 시 동작을 다형적으로 구현하며, MineCell은 폭발 예외를 발생시키고 EmptyCell은 인접 칸을 BFS로 연쇄 오픈하도록 동작을 구체화함.

### 3-3. 사용 프레임워크 및 선택 이유
- ArrayList는 셀 오픈 시 열린 칸을 순차적으로 저장하고 한 번에 UI로 전달하기 위해 사용
- LinkedList는 BFS 연쇄 오픈 과정에서 앞·뒤 삽입과 삭제가 O(1)로 처리되어, 큐(Queue) 구조로 활용할 때 탐색 효율이 높임

## 4. 문제 해결(이슈 -> 해결)
- 첫 클릭에서 지뢰가 나타나는 문제
  - 지뢰를 초기화 시점에 배치하지 않고 safeMinesPlaced(r,c) 도입해 첫 클릭 위치 3×3을 제외해 배치
- 창 크기 변경 시 칸이 일그러지는 문제
  - 보드 패널을 중앙 배치 컨테이너로 감싸고, 리사이즈 이벤트마다 셀 크기를 계산해 정사각형을 유지하도록 적용
- 연쇄 오픈 성능 문제
  - 재귀 대신 큐 기반 BFS로 변경해 연쇄 오픈을 안정적으로 처리
- 갱신 최적화
  - refreshCells(List<Point>)로 변경된 칸만 UI 갱신하여 깜빡임 현상 감소

## 5. 클래스 다이어그램
<img width="1332" height="1768" alt="ClassDiagram" src="https://github.com/user-attachments/assets/731f202d-a67a-49ec-bc01-06955906e668" />

## 6. 작동 화면
### 6-1. 메뉴 조작 화면
![menu_gif](https://github.com/user-attachments/assets/112067f2-c815-43b6-9aef-6491aaeb5df8)

### 6-2. 플레이 화면(게임 오버 포함)
![play_gif](https://github.com/user-attachments/assets/541c7fe0-d174-4919-bcfd-1f6fc71ad2d6)

### 6-3. 클리어 화면
![clear_gif](https://github.com/user-attachments/assets/cadf3efd-1bb8-44b6-8fef-622f06da9439)

## 7. 스켈레톤 구조 및 기능 요약
```text
JAVA_MineSweeper/
├── README.md
└── com/
    └── minesweeper/ 
        ├── MinesweeperMain.java -> 애플리케이션 진입점과 공용 패키지에 대한 루트를 제공합니다.
        ├── common/ -> 전역적으로 공유되는 정의를 모아둔 패키지입니다.
        │   ├── Click.java
        │   ├── Difficulty.java
        │   ├── FlagState.java
        │   └── GameExceptions.java 
        ├── game/ -> 보드 생성·지뢰 배치·승리 판정과 같은 핵심 게임 로직을 담당합니다.
        │   ├── Board.java
        │   └── cells/ -> 셀 모델을 세분화해 타입별 상태와 동작을 제공합니다.
        │       ├── Cell.java
        │       ├── EmptyCell.java
        │       └── MineCell.java
        └── ui/ 사용자 UI를 담당합니다.(Swing 기반)
            ├── CellButton.java
            └── GameWindow.java
