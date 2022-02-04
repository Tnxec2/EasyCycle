package com.kontranik.easycycle.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.NumberPicker
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.easycycle.MainActivity
import com.kontranik.easycycle.MainViewModel
import com.kontranik.easycycle.R
import com.kontranik.easycycle.databinding.FragmentInfoBinding
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.ui.settings.SettingsDialogFragment
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var noDataLinearLayout: ScrollView
    private lateinit var cycleStartTextView: TextView
    private lateinit var cycleLengthTextView: TextView
    private lateinit var btnSave: Button

    private lateinit var sharedModel: MainViewModel
    private lateinit var viewModel: HomeViewModel

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

        sharedModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        recyclerView = binding.rvHomeInfoList

        sharedModel.settings.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.setDaysOnHome(it.daysOnHome)
            viewModel.loadCycleDays(sharedModel.lastCycle.value)
        })

        viewModel.cDays.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val adapter = HomeListAdapter(context, it)
            recyclerView.adapter = adapter
            if ( it.isEmpty()) {
                noDataLinearLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noDataLinearLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        })

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

            if (viewModel.cycleStartDate.value != null) {
                val lastCycle =
                    LastCycle(
                        cycleStart = viewModel.cycleStartDate.value!!.time,
                        lengthOfLastCycle = viewModel.getLastCycleLength()
                    )
                sharedModel.saveCycleItem(lastCycle)
            }
        }

        viewModel.cycleStartDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if ( it != null)
                cycleStartTextView.text = sdf.format(it.time)
            else
                cycleStartTextView.text = getString(R.string.no_date_set_date_placeholder)
        })

        sharedModel.lastCycle.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.loadCycleDays(it)
        })

        setLength()

        return root
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

    private fun openSettingsFragment() {
        val fragment2 = SettingsDialogFragment().newInstance()
        val fragmentManager: FragmentManager = parentFragmentManager
        fragment2.show(fragmentManager, "fragment_settings")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        np.value = viewModel.getLastCycleLength()
        np.minValue = 0
        np.wrapSelectorWheel = true
        b1.setOnClickListener{
            viewModel.setLastCycleLength(np.value)
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
            viewModel.setCycleStartDate(year, monthOfYear, dayOfMonth)

        btnSave.isEnabled = true
    }

    private fun setLength() {
        cycleLengthTextView.text = viewModel.getLastCycleLength().toString()
    }
}