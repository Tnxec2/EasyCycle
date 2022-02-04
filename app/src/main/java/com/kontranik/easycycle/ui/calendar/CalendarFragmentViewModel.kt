package com.kontranik.easycycle.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kontranik.easycycle.components.mycalendar.Constants
import com.kontranik.easycycle.components.mycalendar.MarkedDate
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.helper.TimeHelper
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.models.Note
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragmentViewModel(val app: Application): AndroidViewModel(app) {
    val sdfISO = SimpleDateFormat(Constants.MyCalendarMarkedDateFormat, Locale.US)
    val sdftitle = SimpleDateFormat(Constants.MyCalendarTitleFormat, Locale.getDefault())
    val sdftitleDay = SimpleDateFormat(Constants.MyCalendarTitleDayFormat, Locale.getDefault())
    val sdfweekday = SimpleDateFormat(Constants.MyCalendarWeekdayFormat, Locale.getDefault())

    private val activeDate = Calendar.getInstance()
    private var markedData: HashMap<String, MarkedDate> = hashMapOf()
    private var notes: HashMap<String, Note> = hashMapOf()

    fun getActiveDate(): Calendar {
        return activeDate
    }

    fun setActiveDate(dayOfMonth: Int, monthOfYear: Int, year: Int) {
        activeDate.set(Calendar.YEAR, year)
        activeDate.set(Calendar.MONTH, monthOfYear)
        activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    fun containtMarkedDataKey(key: String?): Boolean {
        return markedData.containsKey(key)
    }

    fun getMarkedData(key: String?): MarkedDate? {
        return markedData[key]
    }

    fun getNote(key: String?): Note? {
        return notes[key]
    }

    fun activeDateSetToPreviousMonth() {
        activeDate.add(Calendar.MONTH, -1)
    }

    fun activeDateSetToNextMonth() {
        activeDate.add(Calendar.MONTH, 1)
    }

    fun activeDateSetToday() {
        activeDate.time = Date()
    }

    fun generateMatrix(): MutableList<MutableList<Date>> {
        val matrix: MutableList<MutableList<Date>> = mutableListOf()

        val activeDateCal = Calendar.getInstance()
        activeDateCal.time = activeDate.time

        val curMonthDate = Calendar.getInstance()
        curMonthDate.time = activeDate.time
        curMonthDate.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayWeekday = curMonthDate.get(Calendar.DAY_OF_WEEK)

        val prevMonthDate = Calendar.getInstance()
        prevMonthDate.time = activeDate.time
        prevMonthDate.add(Calendar.MONTH, -1)

        val prevMonthLastDay = prevMonthDate.getActualMaximum(Calendar.DAY_OF_MONTH)

        prevMonthDate.set(Calendar.DAY_OF_MONTH, prevMonthLastDay - firstDayWeekday + 3)

        for ( row in  0 until ROWS_IN_CALENDAR) {
            matrix.add(mutableListOf())
            for (col in  0 until DAYS_IN_ROW) {
                matrix[row].add(prevMonthDate.time)
                prevMonthDate.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return matrix
    }

    fun loadCycleData(lastCycle: LastCycle?, averageLength: Int) {

        if ( lastCycle == null ) {
            markedData.clear()
            notes.clear()
            return
        }

        val workCalendar = Calendar.getInstance()
        workCalendar.time = lastCycle.cycleStart

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = activeDate.time
        tempCalendar.set(Calendar.DAY_OF_MONTH, 0)

        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.time = tempCalendar.time

        maxDateCalendar.add(Calendar.DAY_OF_YEAR, Companion.MAX_DAYS_IN_CALENDAR)

        val tempMarkDate: HashMap<String, MarkedDate> = hashMapOf()
        val tempNotes: HashMap<String, Note> = hashMapOf()
        var lastCycleStart = lastCycle.cycleStart
        var repeated = false
        while (workCalendar.timeInMillis < maxDateCalendar.timeInMillis) {
            var dayCycle: Int = TimeHelper.getDifferenceInDays(workCalendar.time, lastCycleStart) + 1
            val markedData = MarkedDate()
            val key: String = sdfISO.format(workCalendar.time)
            var color: String? = null
            if (dayCycle > 0) {
                if (dayCycle > averageLength) {
                    dayCycle = 1
                    lastCycleStart = workCalendar.time
                    repeated = true
                }
                val dayPhases = PhasesHelper.getPhasesByDay(app, dayCycle)
                tempNotes[key] = Note(
                    day = dayCycle,
                    notes = mutableListOf())

                dayPhases.forEach{
                    if ( it.desc != null) {
                        tempNotes[key]!!.notes.add(it.desc!!)
                    }

                    val tempColor = if (repeated) it.colorP  else it.color
                    if (tempColor != null) {
                        if ( it.markwholephase != null && it.markwholephase == true ) {
                            if ( dayCycle >= it.from && (it.to == null || dayCycle <= it.to!!)) color = tempColor
                        } else if (dayCycle == it.from.toInt()) color = tempColor
                    }
                }
            }
            if ( color != null ) {
                markedData.marked = true
                markedData.color = color
            }
            if (markedData.marked) tempMarkDate[key] = markedData
            workCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        markedData = tempMarkDate
        notes = tempNotes
    }

    fun isActiveDay(calendar: Calendar) =
        calendar.get(Calendar.YEAR) == activeDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == activeDate.get(
            Calendar.MONTH
        ) && calendar.get(Calendar.DAY_OF_MONTH) == activeDate.get(Calendar.DAY_OF_MONTH)

    fun isCurrentMonth(calendar: Calendar) =
        calendar.get(Calendar.YEAR) == activeDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == activeDate.get(
            Calendar.MONTH
        )

    fun isSunday(j: Int) = j == 6

    companion object {
        private const val MAX_DAYS_IN_CALENDAR = 42 // maximal 6 Rows by 7 Days
        private const val ROWS_IN_CALENDAR = 6
        private const val DAYS_IN_ROW = 7
    }
}