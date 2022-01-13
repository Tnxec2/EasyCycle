package com.kontranik.easycycle.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kontranik.easycycle.components.mycalendar.Constants
import com.kontranik.easycycle.components.mycalendar.MarkedDate
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.helper.TimeHelper
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.models.Note
import com.kontranik.easycycle.storage.SettingsService
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragmentViewModel(val app: Application): AndroidViewModel(app) {
    val sdfISO = SimpleDateFormat(Constants.MyCalendarMarkedDateFormat, Locale.US)
    val sdftitle = SimpleDateFormat(Constants.MyCalendarTitleFormat, Locale.getDefault())
    val sdftitleDay = SimpleDateFormat(Constants.MyCalendarTitleDayFormat, Locale.getDefault())
    val sdfweekday = SimpleDateFormat(Constants.MyCalendarWeekdayFormat, Locale.getDefault())

    private var activeDate: Calendar = Calendar.getInstance()
    private var lastCycle: LastCycle? = null
    private var markedData: HashMap<String, MarkedDate> = hashMapOf()
    private var notes: HashMap<String, Note> = hashMapOf()

    private val databaseService = DatabaseService(app)

    fun getActiveDate(): Calendar {
        return activeDate
    }

    fun setActiveDate(dayOfMonth: Int, monthOfYear: Int, year: Int) {
        activeDate.set(Calendar.YEAR, year)
        activeDate.set(Calendar.MONTH, monthOfYear)
        activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    fun getLastCycle(): LastCycle? {
        return lastCycle
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

        for ( row in  0 until 6) {
            matrix.add(mutableListOf())
            for (col in  0 until 7) {
                matrix[row].add(prevMonthDate.time)
                prevMonthDate.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return matrix
    }

    fun loadCycleData() {
        lastCycle = SettingsService.loadLastCycleStart(app)

        if ( lastCycle == null ) {
            markedData.clear()
            notes.clear()
            return
        }

        val averageLength: Int = databaseService.getAverageLength() ?: lastCycle!!.lengthOfLastCycle
        val workCalendar = Calendar.getInstance()
        workCalendar.time = lastCycle!!.cycleStart

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = activeDate.time
        tempCalendar.set(Calendar.DAY_OF_MONTH, 0)

        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.time = tempCalendar.time
        maxDateCalendar.add(Calendar.DAY_OF_YEAR, 42)

        val tempMarkDate: HashMap<String, MarkedDate> = hashMapOf()
        val tempNotes: HashMap<String, Note> = hashMapOf()
        var lastCycleStart = lastCycle!!.cycleStart
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

    fun saveLastCycle() {
        var length = DefaultSettings.defaultCycleLength
        if ( lastCycle != null)
            length = TimeHelper.getDifferenceInDays(activeDate.time, lastCycle!!.cycleStart)

        val lastCycle = LastCycle(cycleStart = activeDate.time, lengthOfLastCycle = length)
        SettingsService.saveLastCycleStart(lastCycle, app)
        databaseService.add(lastCycle)
    }
}