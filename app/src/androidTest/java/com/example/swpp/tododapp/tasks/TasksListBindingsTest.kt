package com.example.swpp.tododapp.tasks

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.swpp.tododapp.data.Task
import java.time.LocalDate
import java.time.ZoneId
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {
    @get:Rule
    val errors = ErrorCollector()

    private lateinit var baseDate: LocalDate
    private lateinit var baseZone: ZoneId

    @Before
    fun captureDate() {
        baseZone = ZoneId.systemDefault()
        baseDate = LocalDate.now(baseZone)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_threeDaysAhead_displaysDMinus3() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.plusDays(3))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-3")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_dueToday_displaysDDay() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate)

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-Day")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_threeDaysOverdue_displaysDPlus3() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.minusDays(3))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D+3")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedYesterday_displaysClear() {
        // Given
        val view = newTextView()
        val task = newTask(isCompleted = true, dueDate = baseDate.minusDays(1))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "CLEAR")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedToday_displaysClear() {
        // Given
        val view = newTextView()
        val task = newTask(isCompleted = true, dueDate = baseDate)

        // When
        bind(view, task)

        // Then
        assertLabel(view, "CLEAR")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedTomorrow_displaysClear() {
        // Given
        val view = newTextView()
        val task = newTask(isCompleted = true, dueDate = baseDate.plusDays(1))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "CLEAR")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedWithoutDueDate_displaysClear() {
        // Given
        val view = newTextView()
        val task = newTask(isCompleted = true)

        // When
        bind(view, task)

        // Then
        assertLabel(view, "CLEAR")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeWithoutDueDate_clearsOldLabel() {
        // Given
        val view = newTextView()
        val task = newTask()

        // When
        bind(view, task)

        // Then
        assertLabel(view, "", visibility = null)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_nullTask_clearsOldLabel() {
        // Given
        val view = newTextView()

        // When
        bind(view, null)

        // Then
        assertLabel(view, "", visibility = null)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_yesterdayBoundary_displaysDPlus1() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.minusDays(1))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D+1")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_todayBoundary_displaysDDay() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate)

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-Day")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_tomorrowBoundary_displaysDMinus1() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.plusDays(1))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-1")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_thirtyDaysAhead_displaysDMinus30() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.plusDays(30))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-30")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_thirtyOneDaysAhead_displaysDMinus31() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.plusDays(31))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-31")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_thirtyTwoDaysAhead_displaysDMinus32() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.plusDays(32))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-32")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_toggleCompletionWithoutDueDate_clearsClearLabel() {
        // Given
        val view = newTextView()
        val task = newTask()

        // When
        bind(view, task)
        // Then
        assertLabel(view, "", visibility = null)

        // When
        bind(view, task.copy(isCompleted = true))
        // Then
        assertLabel(view, "CLEAR")

        // When
        bind(view, task)
        // Then
        assertLabel(view, "", visibility = null)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_toggleCompletionWithDueDate_restoresDateLabel() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.plusDays(1))

        // When
        bind(view, task)
        // Then
        assertLabel(view, "D-1")

        // When
        bind(view, task.copy(isCompleted = true))
        // Then
        assertLabel(view, "CLEAR")

        // When
        bind(view, task)
        // Then
        assertLabel(view, "D-1")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_addThenRemoveActiveDueDate_clearsDateLabel() {
        // Given
        val view = newTextView()
        val task = newTask()

        // When
        bind(view, task)
        // Then
        assertLabel(view, "", visibility = null)

        // When
        bind(view, task.copy(dueDate = baseDate.plusDays(1)))
        // Then
        assertLabel(view, "D-1")

        // When
        bind(view, task)
        // Then
        assertLabel(view, "", visibility = null)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_changeCompletedDueDate_keepsClearLabel() {
        // Given
        val view = newTextView()
        val task = newTask(isCompleted = true)

        // When
        bind(view, task)
        // Then
        assertLabel(view, "CLEAR")

        // When
        bind(view, task.copy(dueDate = baseDate.plusDays(1)))
        // Then
        assertLabel(view, "CLEAR")

        // When
        bind(view, task)
        // Then
        assertLabel(view, "CLEAR")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_removeThenRestoreTask_clearsThenRestoresLabel() {
        // Given
        val view = newTextView()
        val task = newTask(dueDate = baseDate.plusDays(1))

        // When
        bind(view, task)
        // Then
        assertLabel(view, "D-1")

        // When
        bind(view, null)
        // Then
        assertLabel(view, "", visibility = null)

        // When
        bind(view, task)
        // Then
        assertLabel(view, "D-1")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_reuseViewForUndatedActiveTask_clearsPreviousTaskLabel() {
        // Given
        val view = newTextView()
        val completed = newTask(isCompleted = true)
        val active = newTask(id = "task-2")

        // When
        bind(view, completed)
        // Then
        assertLabel(view, "CLEAR")

        // When
        bind(view, active)
        // Then
        assertLabel(view, "", visibility = null)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_initiallyGone_makesLabelVisible() {
        // Given
        val view = newTextView().apply {
            text = ""
            visibility = View.GONE
        }
        val task = newTask(dueDate = baseDate.plusDays(1))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-1")
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_initiallyInvisible_makesLabelVisible() {
        // Given
        val view = newTextView().apply {
            text = ""
            visibility = View.INVISIBLE
        }
        val task = newTask(dueDate = baseDate.plusDays(1))

        // When
        bind(view, task)

        // Then
        assertLabel(view, "D-1")
    }

    private fun newTextView(): TextView {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return TextView(context).apply {
            text = "old label"
            visibility = View.VISIBLE
        }
    }

    private fun newTask(
        isCompleted: Boolean = false,
        dueDate: LocalDate? = null,
        id: String = "task-1"
    ) = Task("Task Title", "Task Description", isCompleted, id, dueDate)

    private fun bind(view: TextView, task: Task?) {
        assumeStableDate()
        setDueDateLabel(view, task)
    }

    private fun assertLabel(view: TextView, expected: String, visibility: Int? = View.VISIBLE) {
        assumeStableDate()
        errors.checkThat("Expected label: <$expected>", view.text.toString(), equalTo(expected))
        if (visibility != null) {
            errors.checkThat("Visibility for label: <$expected>", view.visibility, equalTo(visibility))
        }
    }

    private fun assumeStableDate() {
        assumeTrue(
            "Device date or time zone changed; rerun with a stable date and time zone.",
            baseZone == ZoneId.systemDefault() && baseDate == LocalDate.now(baseZone)
        )
    }
}
