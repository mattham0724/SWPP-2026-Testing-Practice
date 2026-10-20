package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DueDateUtilsTest(
    private val caseName: String,
    private val dueDateInput: String?,
    private val todayInput: String,
    private val isCompleted: Boolean,
    private val expected: String
) {
    @Test
    fun dueDateLabel_input_returnsExpectedLabel() {
        // Given
        val dueDate = dueDateInput?.let { LocalDate.parse(it) }
        val today = LocalDate.parse(todayInput)

        // When
        val result = dueDateLabel(dueDate, today, isCompleted)

        // Then
        assertEquals(caseName, expected, result)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases(): Collection<Array<Any?>> = listOf(
            arrayOf("dueDateLabel_completedYesterday_returnsClear", "2026-10-19", "2026-10-20", true, "CLEAR"),
            arrayOf("dueDateLabel_completedToday_returnsClear", "2026-10-20", "2026-10-20", true, "CLEAR"),
            arrayOf("dueDateLabel_completedTomorrow_returnsClear", "2026-10-21", "2026-10-20", true, "CLEAR"),
            arrayOf("dueDateLabel_completedWithoutDueDate_returnsClear", null, "2026-10-20", true, "CLEAR"),
            arrayOf("dueDateLabel_activeWithoutDueDate_returnsEmpty", null, "2026-10-20", false, ""),
            arrayOf("dueDateLabel_threeDaysAhead_returnsDMinus3", "2026-10-23", "2026-10-20", false, "D-3"),
            arrayOf("dueDateLabel_dueToday_returnsDDay", "2026-10-20", "2026-10-20", false, "D-Day"),
            arrayOf("dueDateLabel_threeDaysOverdue_returnsDPlus3", "2026-10-17", "2026-10-20", false, "D+3"),
            arrayOf("dueDateLabel_yesterdayBoundary_returnsDPlus1", "2026-10-19", "2026-10-20", false, "D+1"),
            arrayOf("dueDateLabel_todayBoundary_returnsDDay", "2026-10-20", "2026-10-20", false, "D-Day"),
            arrayOf("dueDateLabel_tomorrowBoundary_returnsDMinus1", "2026-10-21", "2026-10-20", false, "D-1"),
            arrayOf("dueDateLabel_nullDateAndActive_returnsEmpty", null, "2026-10-20", false, ""),
            arrayOf("dueDateLabel_nullDateAndCompleted_returnsClear", null, "2026-10-20", true, "CLEAR"),
            arrayOf("dueDateLabel_presentDateAndActive_returnsDMinus1", "2026-10-21", "2026-10-20", false, "D-1"),
            arrayOf("dueDateLabel_presentDateAndCompleted_returnsClear", "2026-10-21", "2026-10-20", true, "CLEAR"),
            arrayOf("dueDateLabel_dayBeforeMonthEnd_returnsDPlus1", "2026-10-30", "2026-10-31", false, "D+1"),
            arrayOf("dueDateLabel_onMonthEnd_returnsDDay", "2026-10-31", "2026-10-31", false, "D-Day"),
            arrayOf("dueDateLabel_dayAfterMonthEnd_returnsDMinus1", "2026-11-01", "2026-10-31", false, "D-1"),
            arrayOf("dueDateLabel_dayBeforeMonthAnniversary_returnsDMinus30", "2026-11-19", "2026-10-20", false, "D-30"),
            arrayOf("dueDateLabel_monthAnniversary_returnsDMinus31", "2026-11-20", "2026-10-20", false, "D-31"),
            arrayOf("dueDateLabel_dayAfterMonthAnniversary_returnsDMinus32", "2026-11-21", "2026-10-20", false, "D-32"),
            arrayOf("dueDateLabel_dayAfterPreviousMonthAnniversary_returnsDPlus29", "2026-09-21", "2026-10-20", false, "D+29"),
            arrayOf("dueDateLabel_previousMonthAnniversary_returnsDPlus30", "2026-09-20", "2026-10-20", false, "D+30"),
            arrayOf("dueDateLabel_dayBeforePreviousMonthAnniversary_returnsDPlus31", "2026-09-19", "2026-10-20", false, "D+31"),
            arrayOf("dueDateLabel_dayBeforeYearEnd_returnsDPlus1", "2026-12-30", "2026-12-31", false, "D+1"),
            arrayOf("dueDateLabel_onYearEnd_returnsDDay", "2026-12-31", "2026-12-31", false, "D-Day"),
            arrayOf("dueDateLabel_dayAfterYearEnd_returnsDMinus1", "2027-01-01", "2026-12-31", false, "D-1"),
            arrayOf("dueDateLabel_dayBeforeYearAnniversary_returnsDMinus364", "2026-12-31", "2026-01-01", false, "D-364"),
            arrayOf("dueDateLabel_yearAnniversary_returnsDMinus365", "2027-01-01", "2026-01-01", false, "D-365"),
            arrayOf("dueDateLabel_dayAfterYearAnniversary_returnsDMinus366", "2027-01-02", "2026-01-01", false, "D-366"),
            arrayOf("dueDateLabel_dayBeforePreviousYearAnniversary_returnsDPlus366", "2025-12-31", "2027-01-01", false, "D+366"),
            arrayOf("dueDateLabel_previousYearAnniversary_returnsDPlus365", "2026-01-01", "2027-01-01", false, "D+365"),
            arrayOf("dueDateLabel_dayAfterPreviousYearAnniversary_returnsDPlus364", "2026-01-02", "2027-01-01", false, "D+364"),
            arrayOf("dueDateLabel_beforeLeapDayDueToday_returnsDDay", "2028-02-28", "2028-02-28", false, "D-Day"),
            arrayOf("dueDateLabel_beforeLeapDayDueTomorrow_returnsDMinus1", "2028-02-29", "2028-02-28", false, "D-1"),
            arrayOf("dueDateLabel_crossingLeapDayForward_returnsDMinus2", "2028-03-01", "2028-02-28", false, "D-2"),
            arrayOf("dueDateLabel_onLeapDayDueYesterday_returnsDPlus1", "2028-02-28", "2028-02-29", false, "D+1"),
            arrayOf("dueDateLabel_onLeapDayDueToday_returnsDDay", "2028-02-29", "2028-02-29", false, "D-Day"),
            arrayOf("dueDateLabel_onLeapDayDueTomorrow_returnsDMinus1", "2028-03-01", "2028-02-29", false, "D-1"),
            arrayOf("dueDateLabel_crossingLeapDayBackward_returnsDPlus2", "2028-02-28", "2028-03-01", false, "D+2"),
            arrayOf("dueDateLabel_afterLeapDayDueYesterday_returnsDPlus1", "2028-02-29", "2028-03-01", false, "D+1"),
            arrayOf("dueDateLabel_afterLeapDayDueToday_returnsDDay", "2028-03-01", "2028-03-01", false, "D-Day"),
            arrayOf("dueDateLabel_afterLeapDayDueTomorrow_returnsDMinus1", "2028-03-02", "2028-03-01", false, "D-1"),
            arrayOf("dueDateLabel_commonYearDueYesterday_returnsDPlus1", "2026-02-27", "2026-02-28", false, "D+1"),
            arrayOf("dueDateLabel_commonYearDueToday_returnsDDay", "2026-02-28", "2026-02-28", false, "D-Day"),
            arrayOf("dueDateLabel_commonYearDueTomorrow_returnsDMinus1", "2026-03-01", "2026-02-28", false, "D-1")
        )
    }
}
