# JAVA 지뢰찾기

Java 기반으로 구현한 지뢰찾기 게임 프로젝트입니다. 
난이도에 따라 보드 크기와 지뢰 개수가 달라지고, 첫 클릭은 항상 지뢰를 피하도록 설계되었습니다. 
UI는 창 크기에 맞춰 자동으로 리사이징되며 각 칸은 정사각형 비율을 유지합니다.

## 1. 주요 기능
- **난이도 선택**: 초급/중급/고급 난이도를 메뉴에서 선택하거나 종료 후 다시 선택할 수 있습니다.
- **첫 클릭 보호**: 첫 좌클릭 시 주변 3×3 안전 구역에 지뢰가 배치되지 않도록 보장이 들어갑니다.
- **자동 연쇄 오픈**: 빈 칸을 열면 BFS 방식으로 인접한 빈 칸을 자동으로 확장해서 엽니다.
- **깃발/물음표 표시**: 우클릭할 때마다 `🚩 → ❓ → (빈칸)` 순환으로 상태를 바꿀 수 있습니다.
- **타이머 및 상태 표시줄**: 현재 난이도와 경과 시간을 상단 패널에 표기합니다.
- **반응형 보드**: 창 크기에 따라 각 칸의 크기를 32~80px 범위로 자동 조정하고, 프레임 최소/최대 크기도 함께 갱신합니다.

## 2. 구현 내용
- `GameWindow`가 UI 전체를 관리하며 메뉴, 상태 패널, 보드 패널을 구성합니다.
  - `updateBoardSizing()`에서 현재 창 크기를 기준으로 셀 크기를 계산하고, 최소/최대 크기를 제한합니다.
  - `setupWindowResizeHandler()`로 리사이즈 이벤트에 반응해 항상 정사각형을 유지합니다.
- `Board`는 난이도에 따라 행/열/지뢰 수를 설정하고, 첫 클릭 시점에 지뢰를 배치합니다.
  - `placeMines()`가 안전 구역을 피해 지뢰를 배치하고, `calculateNearMines()`가 인접 지뢰 수를 계산합니다.
  - `neighborCellOpen()`에서 큐(BFS)를 사용해 빈 칸 연쇄 오픈을 구현했습니다.
- `CellButton`은 각 칸의 버튼을 담당하며 `Board`와 `GameWindow`를 참조해 모델 상태를 UI에 반영합니다.
- `Difficulty`, `FlagState`, `GameState`는 게임 전반에 사용되는 열거형으로, UI 텍스트와 상태 판별에 활용됩니다.
- `GameManager`는 보드와 게임 상태를 묶어 추후 컨트롤러/서비스 레이어에서 재사용할 수 있도록 캡슐화했습니다.

## 3. 상세 구현 내용

### 3-1. try-catch 예외처리 정리
- `CellButton.handleLeftClick()`에서 `Board.openCell()` 호출을 `try-catch`로 감싸 `GameExceptions.BoomException`을 처리합니다.
  - 지뢰를 클릭해 예외가 발생하면 `GameWindow.onGameOver()`로 위임해 게임 종료 다이얼로그를 표시합니다.
- 지뢰 배치 시 보드 크기 대비 지뢰가 너무 많을 경우 `Board.placeMines()`에서 `IllegalStateException`을 던져 잘못된 난이도 구성을 방지합니다.

### 3-2. 상속과 인터페이스 설계
- `Click` 인터페이스는 셀이 좌클릭/우클릭 동작을 구현하도록 강제합니다.
- `Cell` 추상 클래스는 좌표, 깃발 상태, 열림 여부와 같은 공통 상태와 `nextFlagState()` 로직을 제공합니다.
  - `EmptyCell`과 `MineCell`이 이를 상속해 각 타입별 상태 값을 설정하고, 클릭 동작을 구체화합니다.
- `GameExceptions` 유틸리티 클래스는 커스텀 런타임 예외를 중첩 클래스로 묶어 UI와 로직 사이의 계약을 명확하게 했습니다.

### 3-3. 사용 프레임워크 및 선택 이유
- `ArrayList<Point>` 기반의 `List`로 클릭 결과나 갱신 대상 셀 목록을 가변 길이로 관리해 UI와 로직 간 데이터 전달을 단순화했습니다.
- 연쇄 오픈 로직은 `Queue<Point>`(구현체: `LinkedList`)를 사용해 BFS를 안정적으로 처리하고, 재귀 호출 없이도 대규모 탐색을 수행할 수 있도록 했습니다.
- `List`와 `Queue`의 표준 인터페이스를 사용해 이후 다른 컬렉션 구현체로 교체해야 할 때도 최소 변경으로 대응할 수 있는 유연성을 확보했습니다.

## 4. 문제 해결
- **첫 클릭에서 지뢰가 나타나는 문제**: 지뢰를 초기화 시점에 배치하지 않고, 첫 클릭 좌표를 입력받은 뒤 안전 구역을 피해서 배치하도록 로직을 수정했습니다.
- **창 크기 변경 시 칸이 일그러지는 문제**: 보드 패널을 중앙 배치 컨테이너로 감싸고, 리사이즈 이벤트마다 셀 크기를 계산해 정사각형을 유지하도록 고정 범위(최소 32px, 최대 80px)를 적용했습니다.
- **연쇄 오픈 성능 문제**: 재귀 대신 큐 기반 BFS로 변경해 스택 오버플로우 위험을 줄이고 대량 연쇄 오픈을 안정적으로 처리합니다.

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
