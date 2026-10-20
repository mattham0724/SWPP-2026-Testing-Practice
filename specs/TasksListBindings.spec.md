# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `fun setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: PDF 53쪽과 PR 설명의 기능 의도에 따라, 작업의 현재 완료 여부와 마감일을 `TextView`에 표시하고 표시할 라벨이 없으면 이전 문자열을 제거한다. 같은 뷰에 다시 바인딩하거나 완료 상태를 바꿔 다시 전달해도 마지막으로 전달한 작업 상태가 화면에 반영되어야 한다. 이 명세는 바인딩 함수를 직접 호출했을 때의 동작을 다루며, 실제 체크박스 클릭이나 화면 이동은 검증 범위에 포함하지 않는다.

**Pre-conditions**
- 유효한 Android `TextView`를 준비하고 UI 메인 스레드에서 호출한다. `task`는 유효한 `Task` 또는 `null`이다.
- 각 독립 사례를 시작할 때 기기의 기본 시간대와 오늘 날짜를 한 번 읽어 `baseZone`, `baseDate`로 보관한다. 모든 입력 날짜는 이 `baseDate`에 아래 명시한 일수를 더하거나 빼서 만든다. 함수 내부에서 사용하는 오늘 날짜와 기준을 맞추기 위해 사례 전체에서 기기의 날짜와 기본 시간대가 유지되어야 한다.
- 아래 Tip의 `baseDate=2026-10-20`은 정확한 입력 날짜를 보여 주기 위한 예시다. 실제 실행에서는 캡처한 `baseDate`와 명시된 `plusDays`·`minusDays` 식을 사용하고, 기대 문자열은 그대로 사용한다. 호출 전후 날짜·시간대를 확인해 기준이 바뀐 사례는 환경 조건 불충족으로 구분하고, 날짜·시간대가 안정된 상태에서 다시 확인한다.
- 별도 언급이 없는 `TextView`의 초기값은 `text="old label"`, `visibility=View.VISIBLE`이다. 독립 사례마다 새 뷰를 준비한다. 연속 호출 사례 안에서는 같은 인스턴스를 사용하며 호출 사이에 `text`나 `visibility`를 수동으로 초기화하지 않는다.
- 각 Tip에서 생략한 `Task` 필드는 `title="Task Title"`, `description="Task Description"`, `id="task-1"`이다. `isCompleted`와 `dueDate`는 각 Tip에 명시한다.
- 기대 라벨은 명세의 문자열로 확인한다. 앱의 `dueDateLabel()`을 호출한 결과를 기대값으로 재사용하지 않는다.
- `각각`으로 묶은 입력·기대값 조합은 개별 테스트 또는 매개변수화된 독립 사례로 모두 확인하고 각 결과를 따로 식별한다. `차례로` 호출하는 상태 전이 사례는 한 사례 안에서 수행하며, 각 호출 직후 해당 단계의 결과를 확인한다. 마지막 호출의 결과만 확인하지 않는다.

**Post-conditions**
- Success cases
  - 미완료 작업의 미래 마감일은 남은 전체 달력 일수의 라벨로 표시한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `task.isCompleted=false`, `task.dueDate=baseDate.plusDays(3)` 즉 `2026-10-23` / When `setDueDateLabel(textView, task)` 호출 / Then `textView.text="D-3"`, `visibility=View.VISIBLE`.
  - 미완료 작업의 마감일이 오늘이면 `D-Day`를 표시한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `task.isCompleted=false`, `task.dueDate=baseDate` 즉 `2026-10-20` / When `setDueDateLabel(textView, task)` 호출 / Then `textView.text="D-Day"`, `visibility=View.VISIBLE`.
  - 미완료 작업의 지난 마감일은 지난 전체 달력 일수의 라벨로 표시한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `task.isCompleted=false`, `task.dueDate=baseDate.minusDays(3)` 즉 `2026-10-17` / When `setDueDateLabel(textView, task)` 호출 / Then `textView.text="D+3"`, `visibility=View.VISIBLE`.
  - 완료된 작업에 마감일이 있으면 날짜에 관계없이 `CLEAR`를 표시한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `task.isCompleted=true`, `task.dueDate`가 각각 `baseDate.minusDays(1)` 즉 `2026-10-19`, `baseDate` 즉 `2026-10-20`, `baseDate.plusDays(1)` 즉 `2026-10-21` / When 각 작업으로 `setDueDateLabel(textView, task)` 호출 / Then 세 경우 모두 `textView.text="CLEAR"`, `visibility=View.VISIBLE`.
  - 완료된 작업은 마감일이 없어도 `CLEAR`를 표시한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `task.isCompleted=true`, `task.dueDate=null` / When `setDueDateLabel(textView, task)` 호출 / Then `textView.text="CLEAR"`, `visibility=View.VISIBLE`.
  - 미완료 작업에 마감일이 없으면 이전 라벨을 지워 아무 문자열도 표시하지 않는다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 초기 `textView.text="old label"`, `task.isCompleted=false`, `task.dueDate=null` / When `setDueDateLabel(textView, task)` 호출 / Then `textView.text`가 정확히 `""`.
  - 작업이 없으면 이전 라벨을 지워 아무 문자열도 표시하지 않는다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 초기 `textView.text="old label"`, `task=null` / When `setDueDateLabel(textView, null)` 호출 / Then `textView.text`가 정확히 `""`.
- Error cases
  - None. 위 사전조건을 만족하는 입력은 모두 유효하며, `task=null`과 `task.dueDate=null`도 정상 처리 대상이다.
- Edge cases
  - 날짜 차이가 -1일, 0일, +1일인 경계에서 서로 다른 라벨을 표시한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `task.isCompleted=false`, `task.dueDate`가 각각 `baseDate.minusDays(1)` 즉 `2026-10-19`, `baseDate` 즉 `2026-10-20`, `baseDate.plusDays(1)` 즉 `2026-10-21` / When 각 작업으로 `setDueDateLabel(textView, task)` 호출 / Then 순서대로 `textView.text="D+1"`, `"D-Day"`, `"D-1"`이며 각 호출 뒤 `visibility=View.VISIBLE`.
  - 마감일이 30일·31일·32일 남은 작업은 전체 일수의 라벨을 표시한다. 달력상 월·연도·윤일 경계 계산 자체는 `DueDateUtils.spec.md`의 고정 날짜 사례로 확인한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `task.isCompleted=false`, `task.dueDate`가 각각 `baseDate.plusDays(30)` 즉 `2026-11-19`, `baseDate.plusDays(31)` 즉 `2026-11-20`, `baseDate.plusDays(32)` 즉 `2026-11-21` / When 각 작업으로 `setDueDateLabel(textView, task)` 호출 / Then 순서대로 `textView.text="D-30"`, `"D-31"`, `"D-32"`이며 각 호출 뒤 `visibility=View.VISIBLE`.
  - 마감일 없는 작업을 미완료에서 완료로 바꾸면 `CLEAR`를 표시하고, 다시 미완료로 바꾸면 `CLEAR`를 제거한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 같은 `textView`, `id="task-1"`, `dueDate=null`인 작업 상태 세 개 / When `isCompleted=false`, `true`, `false` 순서로 `setDueDateLabel(textView, task)`를 차례로 호출 / Then 각 호출 직후 `textView.text`를 순서대로 `""`, `"CLEAR"`, `""`와 비교하고 두 번째 호출 뒤 `visibility=View.VISIBLE`도 확인한다.
  - 마감일 있는 작업은 완료 체크를 해제하면 날짜 라벨을 복원한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 같은 `textView`, `id="task-1"`, `dueDate=baseDate.plusDays(1)` 즉 `2026-10-21`인 작업 상태 세 개 / When `isCompleted=false`, `true`, `false` 순서로 `setDueDateLabel(textView, task)`를 차례로 호출 / Then 각 호출 직후 `textView.text`를 순서대로 `"D-1"`, `"CLEAR"`, `"D-1"`와 비교하고 매번 `visibility=View.VISIBLE`도 확인한다.
  - 미완료 작업의 마감일을 추가했다가 제거하면 라벨이 나타났다가 사라진다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 같은 `textView`, `id="task-1"`, `isCompleted=false`인 작업 상태 세 개 / When `dueDate=null`, `baseDate.plusDays(1)` 즉 `2026-10-21`, `null` 순서로 `setDueDateLabel(textView, task)`를 차례로 호출 / Then 각 호출 직후 `textView.text`를 순서대로 `""`, `"D-1"`, `""`와 비교하고 두 번째 호출 뒤 `visibility=View.VISIBLE`도 확인한다.
  - 완료된 작업은 마감일을 추가하거나 제거해도 `CLEAR`를 유지한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 같은 `textView`, `id="task-1"`, `isCompleted=true`인 작업 상태 세 개 / When `dueDate=null`, `baseDate.plusDays(1)` 즉 `2026-10-21`, `null` 순서로 `setDueDateLabel(textView, task)`를 차례로 호출 / Then 각 호출 직후 `textView.text="CLEAR"`, `visibility=View.VISIBLE`을 확인한다.
  - 작업이 사라졌다가 다시 들어오면 라벨도 지워졌다가 복원된다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 같은 `textView`, `task.isCompleted=false`, `task.dueDate=baseDate.plusDays(1)` 즉 `2026-10-21` / When `task`, `null`, `task` 순서로 `setDueDateLabel(textView, task)`를 차례로 호출 / Then 각 호출 직후 `textView.text`를 순서대로 `"D-1"`, `""`, `"D-1"`와 비교하고 작업이 있는 첫 번째와 세 번째 호출 뒤 `visibility=View.VISIBLE`도 확인한다.
  - 다른 작업에 뷰를 재사용해도 이전 작업의 라벨이 남지 않는다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, 같은 `textView`, 첫 작업은 `id="task-1"`, `isCompleted=true`, `dueDate=null`, 둘째 작업은 `id="task-2"`, `isCompleted=false`, `dueDate=null` / When 첫 작업과 둘째 작업으로 `setDueDateLabel(textView, task)`를 차례로 호출 / Then 첫 호출 직후 `textView.text="CLEAR"`, `visibility=View.VISIBLE`을 확인하고 둘째 호출 직후 `textView.text=""`를 확인한다.
  - 이전에 숨겨진 뷰라도 표시할 라벨이 생기면 다시 보이게 한다. `[Integration]`
    - Tip: Given `baseDate=2026-10-20`인 예시, `textView.text=""`, 초기 `visibility`가 각각 `View.GONE`, `View.INVISIBLE`, `task.isCompleted=false`, `task.dueDate=baseDate.plusDays(1)` 즉 `2026-10-21` / When 각 초기 상태에서 `setDueDateLabel(textView, task)` 호출 / Then 두 경우 모두 `textView.text="D-1"`, `visibility=View.VISIBLE`.

**Undefined behavior** (optional)
- 라벨이 없는 상태의 `visibility`를 `View.VISIBLE`, `View.INVISIBLE`, `View.GONE` 중 무엇으로 둘지는 PDF와 PR 설명에 명시되어 있지 않으므로 이 명세에서는 확정하지 않는다. 이전 문자열을 지워 `text=""`로 만드는 동작은 필수다.
- 기기의 날짜·시간대가 사례 도중 변경되거나 자정을 넘는 상황의 일관성은 이 명세의 범위 밖이다. 이때는 위 날짜 안정성 사전조건을 적용한다.
- 비-UI 스레드 호출, 해제된 화면의 뷰 접근 또는 Kotlin의 비-null `textView` 계약을 강제로 위반하는 호출의 오류 처리는 정의하지 않는다.
