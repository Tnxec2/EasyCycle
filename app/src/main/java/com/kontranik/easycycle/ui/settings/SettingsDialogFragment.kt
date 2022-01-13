package com.kontranik.easycycle.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kontranik.easycycle.MainViewModel
import com.kontranik.easycycle.R
import com.kontranik.easycycle.databinding.FragmentSettingsBinding
import com.kontranik.easycycle.models.Settings


class SettingsDialogFragment : DialogFragment() {

    private val showOnStartItemsIds = arrayListOf<Int>(
        R.id.navigation_info,
        R.id.navigation_calendar,
        R.id.navigation_statistic,
        R.id.navigation_phases)

    private var showOnStartItemsTitle = arrayListOf<String>( )

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var textDaysOnHome: EditText
    private lateinit var textYearsOnStatistic: EditText

    fun newInstance(): SettingsDialogFragment {
        val frag = SettingsDialogFragment()
        val args = Bundle()
        //args.putString("title", title)
        frag.arguments = args
        return frag
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        showOnStartItemsTitle = arrayListOf<String>(
            getString(R.string.title_home),
            getString(R.string.title_calendar),
            getString(R.string.title_statistic),
            getString(R.string.title_phases)
        )

        val cancelButton = binding.btnSettingsCancel
        cancelButton.setOnClickListener {
            dismiss()
        }

        val spinnerShowOnStart = binding.spinnerSettingsShowOnStart
        val aa =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                showOnStartItemsTitle)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerShowOnStart.adapter = aa

        textDaysOnHome = binding.edittextSettingsDaysonhome
        textYearsOnStatistic = binding.edittextSettingsYearsinstatistic

        sharedModel.settings.observe(viewLifecycleOwner, Observer {
            var pos = 0
            showOnStartItemsIds.forEachIndexed { index, item ->
                if (item == it.showOnStart) pos = index
            }
            spinnerShowOnStart.setSelection(pos)
            textDaysOnHome.setText(it.daysOnHome.toString())
            textYearsOnStatistic.setText(it.yearsOnStatistic.toString())
        })

        val saveButton = binding.btnSettingsSave
        saveButton.setOnClickListener {
           val newSettings = Settings(
                daysOnHome = textDaysOnHome.text.toString().toInt(),
                yearsOnStatistic = textYearsOnStatistic.text.toString().toInt(),
                showOnStart = showOnStartItemsIds[spinnerShowOnStart.selectedItemPosition]
            )
            sharedModel.saveSettings(newSettings)
            dismiss()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}