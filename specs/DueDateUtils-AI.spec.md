# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: PDF 53쪽과 PR 설명의 기능 의도에 따라, 완료 여부와 마감일을 작업 목록의 D-day 라벨로 표현한다. 날짜 차이는 월·연도·윤일을 포함한 전체 달력 일수이며, 완료된 작업은 마감일 유무와 관계없이 `CLEAR`를 반환한다.

**Pre-conditions**
- `today`는 유효한 `LocalDate`이며, `dueDate`는 유효한 `LocalDate` 또는 `null`이다. 과거 마감일도 유효하다.
- `isCompleted`는 작업의 현재 완료 여부다.
- 날짜 계산의 기준은 인자로 전달한 `today`다. 시각, 시간대 또는 실행 기기의 현재 날짜에 따라 결과가 달라지지 않는다.
- 아래 Tip의 날짜는 모두 ISO 형식의 실제 입력값이다. 기대 문자열은 이 함수나 다른 앱 함수를 호출하여 계산하지 않고 명세에 적힌 값으로 확인한다.

**Post-conditions**
- Success cases
  - 완료된 작업은 마감일이 과거·오늘·미래 중 어느 경우라도 정확히 `"CLEAR"`를 반환한다. `[Unit]`
    - Tip: Given `today=2026-10-20`, `isCompleted=true`, `dueDate`가 각각 `2026-10-19`, `2026-10-20`, `2026-10-21` / When 각 입력으로 `dueDateLabel()` 호출 / Then 세 결과 모두 `"CLEAR"`.
  - 완료된 작업은 마감일이 없어도 정확히 `"CLEAR"`를 반환한다. `[Unit]`
    - Tip: Given `dueDate=null`, `today=2026-10-20`, `isCompleted=true` / When `dueDateLabel()` 호출 / Then `"CLEAR"`.
  - 미완료 작업에 마감일이 없으면 빈 문자열을 반환한다. `[Unit]`
    - Tip: Given `dueDate=null`, `today=2026-10-20`, `isCompleted=false` / When `dueDateLabel()` 호출 / Then 정확히 `""`.
  - 미완료 작업의 마감일이 미래이면 전체 남은 달력 일수 N을 `"D-N"` 형식으로 반환한다. 공백이나 불필요한 앞자리 0을 붙이지 않는다. `[Unit]`
    - Tip: Given `dueDate=2026-10-23`, `today=2026-10-20`, `isCompleted=false` / When `dueDateLabel()` 호출 / Then 정확히 `"D-3"`.
  - 미완료 작업의 마감일이 오늘이면 정확히 `"D-Day"`를 반환한다. `[Unit]`
    - Tip: Given `dueDate=2026-10-20`, `today=2026-10-20`, `isCompleted=false` / When `dueDateLabel()` 호출 / Then 정확히 `"D-Day"`.
  - 미완료 작업의 마감일이 과거이면 전체 지난 달력 일수 N을 양수로 표시한 `"D+N"`을 반환한다. `[Unit]`
    - Tip: Given `dueDate=2026-10-17`, `today=2026-10-20`, `isCompleted=false` / When `dueDateLabel()` 호출 / Then 정확히 `"D+3"`.
- Error cases
  - None. 위 타입과 사전조건을 만족하는 모든 입력은 유효하며, `dueDate=null`이나 과거 날짜를 오류로 취급하지 않는다.
- Edge cases
  - 지난 날짜·오늘·미래 날짜의 경계인 -1일, 0일, +1일을 구분한다. `[Unit]`
    - Tip: Given `today=2026-10-20`, `isCompleted=false`, `dueDate`가 각각 `2026-10-19`, `2026-10-20`, `2026-10-21` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D+1"`, `"D-Day"`, `"D-1"`.
  - 마감일 유무의 경계에서 완료 여부를 우선 적용한다. `[Unit]`
    - Tip: Given `today=2026-10-20`, `dueDate`가 각각 `null`, `2026-10-21` / When 각 마감일에 대해 `isCompleted=false`와 `true`로 각각 호출 / Then `null`인 경우 `""`, `"CLEAR"`, 날짜가 있는 경우 `"D-1"`, `"CLEAR"`.
  - 월말의 전날·당일·다음 날에도 전체 달력 일수와 부호가 정확하다. `[Unit]`
    - Tip: Given `today=2026-10-31`, `isCompleted=false`, `dueDate`가 각각 `2026-10-30`, `2026-10-31`, `2026-11-01` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D+1"`, `"D-Day"`, `"D-1"`.
  - 한 달 차이의 바로 전·당일·바로 다음에서도 월 단위를 버리지 않고 남은 전체 일수를 표시한다. `[Unit]`
    - Tip: Given `today=2026-10-20`, `isCompleted=false`, `dueDate`가 각각 `2026-11-19`, `2026-11-20`, `2026-11-21` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D-30"`, `"D-31"`, `"D-32"`.
  - 한 달 이상 지난 날짜도 월 단위를 버리지 않고 지난 전체 일수를 표시한다. `[Unit]`
    - Tip: Given `today=2026-10-20`, `isCompleted=false`, `dueDate`가 각각 `2026-09-21`, `2026-09-20`, `2026-09-19` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D+29"`, `"D+30"`, `"D+31"`.
  - 연말의 전날·당일·다음 날에도 날짜 차이를 유지한다. `[Unit]`
    - Tip: Given `today=2026-12-31`, `isCompleted=false`, `dueDate`가 각각 `2026-12-30`, `2026-12-31`, `2027-01-01` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D+1"`, `"D-Day"`, `"D-1"`.
  - 1년 차이의 전날·당일·다음 날에도 연 단위를 버리지 않는다. `[Unit]`
    - Tip: Given `today=2026-01-01`, `isCompleted=false`, `dueDate`가 각각 `2026-12-31`, `2027-01-01`, `2027-01-02` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D-364"`, `"D-365"`, `"D-366"`.
  - 윤년의 2월 말에는 2월 29일을 실제 하루로 포함한다. `[Unit]`
    - Tip: Given `today=2028-02-28`, `isCompleted=false`, `dueDate`가 각각 `2028-02-28`, `2028-02-29`, `2028-03-01` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D-Day"`, `"D-1"`, `"D-2"`.
  - 윤일을 기준일로 삼아도 전날·당일·다음 날을 구분한다. `[Unit]`
    - Tip: Given `today=2028-02-29`, `isCompleted=false`, `dueDate`가 각각 `2028-02-28`, `2028-02-29`, `2028-03-01` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D+1"`, `"D-Day"`, `"D-1"`.
  - 평년의 2월 말에는 존재하지 않는 윤일을 더하지 않는다. `[Unit]`
    - Tip: Given `today=2026-02-28`, `isCompleted=false`, `dueDate`가 각각 `2026-02-27`, `2026-02-28`, `2026-03-01` / When 각 입력으로 `dueDateLabel()` 호출 / Then 순서대로 `"D+1"`, `"D-Day"`, `"D-1"`.

**Undefined behavior** (optional)
- `today=null` 등 Kotlin의 비-null 타입 계약을 강제로 위반하는 호출은 범위 밖이다.
- `2026-02-30`처럼 존재하지 않는 날짜는 `LocalDate` 생성 단계에서 거부되므로 이 함수에 대한 직접 입력 사례로 정의하지 않는다.
