package com.kontranik.easycycle.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.easycycle.MainActivity
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.databinding.FragmentInfoBinding
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.models.CDay
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.models.Settings
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.ui.settings.SettingsFragment
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), SettingsFragment.SettingsListener {

    private var _binding: FragmentInfoBinding? = null

    private val cDays: MutableList<CDay> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var settings: Settings = DefaultSettings.settings

    lateinit var recyclerView: RecyclerView
    lateinit var noDataLinearLayout: ScrollView
    lateinit var cycleStartTextView: TextView
    lateinit var cycleLengthTextView: TextView
    lateinit var btnSave: Button

    var cycleStartDate: Calendar? = null
    var lastCycleLength = DefaultSettings.defaultCycleLength

    private val sdf = SimpleDateFormat("dd. MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)

        (activity as MainActivity?)?.supportActionBar?.title = resources.getString(R.string.title_home)

        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tempSettings = SettingsService.loadSettings(requireContext())
        if ( tempSettings != null) settings = tempSettings

        recyclerView = binding.rvHomeInfoList
        val adapter = HomeListAdapter(context, cDays)
        recyclerView.adapter = adapter

        noDataLinearLayout = binding.scrollViewNoData
        noDataLinearLayout.visibility = View.GONE

        cycleStartTextView = binding.tvHomeNodateStartdate
        cycleLengthTextView = binding.tvHomeNodateAverageCycleLength
        btnSave = binding.btnHomeNodateSave
        btnSave.isEnabled = false

        val imgBtnEditDate = binding.ibHomeNodateEditDate
        val imgBtnEditLength = binding.ibHomeNodateEditLength

        cycleStartTextView.setOnClickListener {
            openDatePicker()
        }

        cycleLengthTextView.setOnClickListener {
            openLengthPicker()
        }

        imgBtnEditDate.setOnClickListener {
            openDatePicker()
        }

        imgBtnEditLength.setOnClickListener {
            openLengthPicker()
        }
        btnSave.setOnClickListener {
            saveData()
        }

        setDate()
        setLength()

        loadInfo()

        return root
    }


    private fun loadInfo() {
        val lastCycle = SettingsService.loadLastCycleStart(requireContext())
        if ( lastCycle != null) {
            val result = PhasesHelper.getDaysInfo(requireContext(), settings.daysOnHome, lastCycle)
            cDays.clear()
            recyclerView!!.adapter!!.notifyDataSetChanged()
            if (result.isEmpty()) {
                noDataLinearLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noDataLinearLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                cDays.addAll(result)
                recyclerView!!.adapter!!.notifyDataSetChanged()
            }
        } else {
            noDataLinearLayout.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                openSettingsFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openSettingsFragment() {
        val fragment2 = SettingsFragment()
        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment2, "Home")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSettingsChanged(settings: Settings) {
        this.settings = settings
        loadInfo()
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val dialog = DatePickerDialog(
            requireContext(),
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )

        dialog.datePicker.maxDate = Date().time
        dialog.show()

    }

    private fun openLengthPicker() {
        val d = Dialog(requireContext())
        d.setTitle("Last cycle length")
        d.setContentView(R.layout.numberpicker_dialog)
        val b1: Button = d.findViewById(R.id.button1) as Button
        val b2: Button = d.findViewById(R.id.button2) as Button
        val np = d.findViewById<NumberPicker> (R.id.numberPicker1)
        np.maxValue = 100
        np.value = lastCycleLength
        np.minValue = 0
        np.wrapSelectorWheel = true
        b1.setOnClickListener{
            lastCycleLength = np.value
            setLength()
            d.dismiss()
        }
        b2.setOnClickListener{
            d.dismiss()
        }
        d.show()
    }

    private var datePickerListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        if ( cycleStartDate == null)
            cycleStartDate = Calendar.getInstance()

        cycleStartDate!!.set(Calendar.YEAR, year)
        cycleStartDate!!.set(Calendar.MONTH, monthOfYear)
        cycleStartDate!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        setDate()
        btnSave.isEnabled = true
    }

    private fun saveData() {
        if (cycleStartDate != null) {
            val lastCycle =
                LastCycle(cycleStart = cycleStartDate!!.time, lengthOfLastCycle = lastCycleLength)
            SettingsService.saveLastCycleStart(lastCycle, requireContext())
            val databaseService = DatabaseService(requireContext())
            databaseService.add(lastCycle)
            loadInfo()
        }
    }

    private fun setDate() {
        if ( cycleStartDate != null)
            cycleStartTextView.text = sdf.format(cycleStartDate!!.time)
        else
            cycleStartTextView.text = getString(R.string.no_date_set_date_placeholder)
    }

    private fun setLength() {
        cycleLengthTextView.text = lastCycleLength.toString()
    }
}