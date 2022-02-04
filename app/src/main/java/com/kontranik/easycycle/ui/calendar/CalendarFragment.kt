package com.kontranik.easycycle.ui.calendar

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kontranik.easycycle.MainViewModel
import com.kontranik.easycycle.R
import com.kontranik.easycycle.components.circularTextView.CircularTextView
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.databinding.FragmentCalendarBinding
import com.kontranik.easycycle.helper.TimeHelper
import com.kontranik.easycycle.models.LastCycle
import java.util.*


class CalendarFragment : Fragment() {


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

    private lateinit var viewModel: CalendarFragmentViewModel
    private lateinit var sharedModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(requireActivity()).get(CalendarFragmentViewModel::class.java)
        sharedModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val prevImageButton = binding.ibPrevMonth
        prevImageButton.setOnClickListener {
            viewModel.activeDateSetToPreviousMonth()
            loadCycleData(viewModel.getActiveDate())
        }
        val nextImageButton = binding.ibNextMonth
        nextImageButton.setOnClickListener {
            viewModel.activeDateSetToNextMonth()
            loadCycleData(viewModel.getActiveDate())
        }
        val todayImageButton = binding.ibCalendarToday
        todayImageButton.setOnClickListener {
            viewModel.activeDateSetToday()
            loadCycleData(viewModel.getActiveDate())
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
            showDatePicker(viewModel.getActiveDate())
        }

        loadCycleData(viewModel.getActiveDate())

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

    private fun loadCycleData(activeDate: Calendar) {
        infocardTitle.text = viewModel.sdftitleDay.format(activeDate.time)
        infocardBadge.text = ""
        infocardDescription.text = ""
        infocardAddButton.visibility = View.VISIBLE

        viewModel.loadCycleData(sharedModel.lastCycle.value, sharedModel.averageCycleLength.value!!)


        val key = viewModel.sdfISO.format(activeDate.time)
        val notes = viewModel.getNote(key)
        if ( notes != null ) {
            infocardBadge.text = notes.day.toString()
            val desc = notes.notes.joinToString("\n")
            infocardDescription.text = desc
        }
        if ( ( sharedModel.lastCycle.value != null
                    && ( TimeHelper.isLess(activeDate.time, sharedModel.lastCycle.value!!.cycleStart)
                    || TimeHelper.isEqual(activeDate.time, sharedModel.lastCycle.value!!.cycleStart) )
                    )
            || TimeHelper.isGreat(activeDate.time, Date())  )
            infocardAddButton.visibility = View.INVISIBLE

        initCalendar(activeDate)
    }

    private fun initCalendar(activeDate: Calendar) {

        val calendar = Calendar.getInstance()

        title.text = viewModel.sdftitle.format( activeDate.time)

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

        val matrix = viewModel.generateMatrix()

        val rowHeader = TableRow(context)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)
        rowHeader.layoutParams = lp

        for (i in 0..6) {
            val cel = layoutInflater.inflate(R.layout.mycalendar_header, null) as TextView
            if (viewModel.isSunday(i)) {
                cel.setTextColor(colSundayActiv)
            } else {
                cel.setTextColor(colDayActiv)
            }
            cel.text = viewModel.sdfweekday.format(matrix[0][i])
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
                    loadCycleData(activeDate)
                }
                if (viewModel.isSunday(j)) {
                    if (viewModel.isCurrentMonth(calendar))
                        cel.setTextColor(colSundayActiv)
                    else
                        cel.setTextColor(colSundayInactiv)
                } else {
                    if (viewModel.isCurrentMonth(calendar))
                        cel.setTextColor(colDayActiv)
                    else
                        cel.setTextColor(colDayActivInactiv)
                }
                if (viewModel.isActiveDay(calendar)) {
                    cel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                    cel.setTypeface(null, Typeface.BOLD)
                }
                val dateString = viewModel.sdfISO.format(d)
                if (viewModel.containtMarkedDataKey(dateString)) {
                    val markedDate = viewModel.getMarkedData(dateString)
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

    private var datePickerListener =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.setActiveDate(dayOfMonth, monthOfYear, year)

            var length = DefaultSettings.defaultCycleLength
            if ( sharedModel.lastCycle.value != null)
                length = TimeHelper.getDifferenceInDays(viewModel.getActiveDate().time, sharedModel.lastCycle.value!!.cycleStart)

            val lastCycle = LastCycle(cycleStart = viewModel.getActiveDate().time, lengthOfLastCycle = length)

            sharedModel.saveCycleItem(lastCycle)
            loadCycleData(viewModel.getActiveDate())
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
            dialog.setTitle(getString(R.string.set_next_cycle_start))
            if (sharedModel.lastCycle.value != null) dialog.datePicker.minDate = sharedModel.lastCycle.value!!.cycleStart.time
            dialog.datePicker.maxDate = Date().time
            dialog.show()
        }
    }
}