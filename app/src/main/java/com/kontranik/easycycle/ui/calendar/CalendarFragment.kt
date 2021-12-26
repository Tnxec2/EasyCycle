package com.kontranik.easycycle.ui.calendar

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kontranik.easycycle.components.mycalendar.Constants
import com.kontranik.easycycle.R
import com.kontranik.easycycle.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.util.TypedValue
import android.view.*
import android.widget.ImageButton
import androidx.annotation.ColorInt
import com.kontranik.easycycle.components.circularTextView.CircularTextView
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.components.mycalendar.MarkedDate
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.helper.TimeHelper
import com.kontranik.easycycle.models.Note
import kotlin.collections.HashMap


class CalendarFragment : Fragment() {

    private lateinit var databaseService: DatabaseService

    private val sdfISO = SimpleDateFormat(Constants.MyCalendarMarkedDateFormat, Locale.US)
    private val sdftitle = SimpleDateFormat(Constants.MyCalendarTitleFormat, Locale.getDefault())
    private val sdftitleDay = SimpleDateFormat(Constants.MyCalendarTitleDayFormat, Locale.getDefault())
    private val sdfweekday = SimpleDateFormat(Constants.MyCalendarWeekdayFormat, Locale.getDefault())

    private var activeDate: Calendar = Calendar.getInstance()
    private var lastCycle: LastCycle? = null

    private var markedData: HashMap<String, MarkedDate> = hashMapOf()
    private var notes: HashMap<String, Note> = hashMapOf()

    private lateinit var title: TextView
    private lateinit var tableLayout: TableLayout

    private lateinit var infocardBadge: TextView
    private lateinit var infocardTitle: TextView
    private lateinit var infocardDescription: TextView
    private lateinit var infocardAddButton: ImageButton
    private lateinit var todayTextView: TextView

    private var _binding: FragmentCalendarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        databaseService = DatabaseService(requireContext())

        val prevImageButton = binding.ibPrevMonth
        prevImageButton.setOnClickListener {
            activeDate.add(Calendar.MONTH, -1)
            loadCycleData()
        }
        val nextImageButton = binding.ibNextMonth
        nextImageButton.setOnClickListener {
            activeDate.add(Calendar.MONTH, 1)
            loadCycleData()
        }
        val todayImageButton = binding.ibCalendarToday
        todayImageButton.setOnClickListener {
            activeDate.time = Date()
            loadCycleData()
        }
        todayTextView = binding.tvCalendarTodaytext

        val calendar = Calendar.getInstance()
        calendar.time = Date()
        todayTextView.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        title = binding.tvTitleCalendar
        tableLayout = binding.tableCalendar
        infocardBadge = binding.tvCalendarInfocardTitleBadge
        infocardTitle = binding.tvCalendarInfocardTitleText
        infocardDescription = binding.tvCalendarInfocardDescription
        infocardAddButton = binding.ibCaledarInfocardAddStartDay

        infocardAddButton.setOnClickListener {
            showDatePicker(activeDate)
        }

        loadCycleData()

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.calendar_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_calendar_add -> {
                val calendar = Calendar.getInstance()
                calendar.time = Date()
                showDatePicker(calendar)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initCalendar() {

        val calendar = Calendar.getInstance()

        title.text = sdftitle.format(activeDate.time)

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        @ColorInt val colPrimary = typedValue.data
        theme.resolveAttribute(R.attr.colorOnSunday, typedValue, true)
        @ColorInt val colSundayActiv = typedValue.data
        theme.resolveAttribute(R.attr.colorOnSundayInactiv, typedValue, true)
        @ColorInt val colSundayInactiv = typedValue.data
        theme.resolveAttribute(R.attr.colorOnDay, typedValue, true)
        @ColorInt val colDayActiv = typedValue.data
        theme.resolveAttribute(R.attr.colorOnDayInactiv, typedValue, true)
        @ColorInt val colDayActivInactiv = typedValue.data

        tableLayout.removeAllViews()


        val matrix = generateMatrix()

        val rowHeader = TableRow(context)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)
        rowHeader.layoutParams = lp



        for (i in 0..6) {
            val cel = layoutInflater.inflate(R.layout.mycalendar_header, null) as TextView
            if ( i == 6) {
                cel.setTextColor(colSundayActiv)
            } else {
                cel.setTextColor(colDayActiv)
            }
            cel.text = sdfweekday.format(matrix[0][i])
            cel.layoutParams = lp
            rowHeader.addView(cel)
        }
        tableLayout.addView(rowHeader)




        for (i in matrix.indices) {
            val row = TableRow(context)
            row.layoutParams = lp
            for(j in matrix[i].indices) {
                val d = matrix[i][j]

                calendar.time = d

                val cel = layoutInflater.inflate(R.layout.mycalendar_cell, null) as CircularTextView
                cel.setOnClickListener {
                    activeDate.time = d
                    loadCycleData()
                }
                if ( j == 6) {
                    if ( calendar.get(Calendar.YEAR) == activeDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == activeDate.get(Calendar.MONTH))
                        cel.setTextColor(colSundayActiv)
                    else
                        cel.setTextColor(colSundayInactiv)
                } else {
                    if ( calendar.get(Calendar.YEAR) == activeDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == activeDate.get(Calendar.MONTH))
                        cel.setTextColor(colDayActiv)
                    else
                        cel.setTextColor(colDayActivInactiv)
                }
                if ( calendar.get(Calendar.YEAR) == activeDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == activeDate.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == activeDate.get(Calendar.DAY_OF_MONTH)) {
                    cel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                    cel.setTypeface(null, Typeface.BOLD)
                }
                val dateString = sdfISO.format(d)
                if (markedData.containsKey(dateString)) {
                    val markedDate = markedData[dateString]
                    if ( markedDate != null && markedDate.marked && markedDate.color != null) {
                        cel.setSolidColor(markedDate.color)
                    }
                }
                cel.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
                cel.layoutParams = lp
                row.addView(cel)
            }
            tableLayout.addView(row, i+1)
        }
    }

    private fun generateMatrix(): MutableList<MutableList<Date>> {
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

    private fun loadCycleData() {
        infocardTitle.text = sdftitleDay.format(activeDate.time)
        infocardBadge.text = ""
        infocardDescription.text = ""
        infocardAddButton.visibility = View.VISIBLE

        lastCycle = SettingsService.loadLastCycleStart(requireContext())

        if ( lastCycle == null ) {
            markedData.clear()
            notes.clear()
            initCalendar()
            return
        }
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
                if (dayCycle > lastCycle!!.lengthOfLastCycle) {
                    dayCycle = 1
                    lastCycleStart = workCalendar.time
                    repeated = true
                }
                val dayPhases = PhasesHelper.getPhasesByDay(requireContext(), dayCycle)
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


        val key = sdfISO.format(activeDate.time)
        val notes = notes[key]
        if ( notes != null ) {
            infocardBadge.text = notes.day.toString()
            val desc = StringBuffer()
            notes.notes.forEach{
                desc.append(it)
            }
            infocardDescription.text = desc
        }
        if ( ( lastCycle != null
                    && ( TimeHelper.isLess(activeDate.time, lastCycle!!.cycleStart)
                    || TimeHelper.isEqual(activeDate.time, lastCycle!!.cycleStart) )
                    )
            || TimeHelper.isGreat(activeDate.time, Date())  )
                infocardAddButton.visibility = View.INVISIBLE
        initCalendar()
    }

    private var datePickerListener =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            activeDate.set(Calendar.YEAR, year)
            activeDate.set(Calendar.MONTH, monthOfYear)
            activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            var length = DefaultSettings.defaultCycleLength
            if ( lastCycle != null)
                length = TimeHelper.getDifferenceInDays(activeDate.time, lastCycle!!.cycleStart)

            val lastCycle = LastCycle(cycleStart = activeDate.time, lengthOfLastCycle = length)
            SettingsService.saveLastCycleStart(lastCycle, requireContext())
            databaseService.add(lastCycle)
            loadCycleData()
    }

    private fun showDatePicker(calendar: Calendar) {
        context?.let {
            val dialog = DatePickerDialog(
                it,
                datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            )
            if (lastCycle != null) dialog.datePicker.minDate = lastCycle!!.cycleStart.time
            dialog.datePicker.maxDate = Date().time
            dialog.show()
        }
    }
}