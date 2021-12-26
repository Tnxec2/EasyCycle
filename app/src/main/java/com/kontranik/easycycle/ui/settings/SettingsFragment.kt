package com.kontranik.easycycle.ui.settings

import android.os.Bundle
import android.util.Log

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import android.widget.EditText

import androidx.fragment.app.Fragment
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.databinding.FragmentSettingsBinding
import com.kontranik.easycycle.models.Settings
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.MainActivity
import com.kontranik.easycycle.R
import android.widget.AdapterView

import android.widget.ArrayAdapter





class SettingsFragment : Fragment() {

    private var listener: SettingsListener? = null

    private val showOnStartItemsIds = arrayListOf<Int>(
        R.id.navigation_info,
        R.id.navigation_calendar,
        R.id.navigation_statistic,
        R.id.navigation_phases)
    private var showOnStartItemsTitle = arrayListOf<String>( )

    interface SettingsListener {
        fun onSettingsChanged(settings: Settings)
    }

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var textDaysOnHome: EditText
    private lateinit var textYearsOnStatistic: EditText

    private var settings: Settings = DefaultSettings.settings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity?)?.supportActionBar?.title = resources.getString(R.string.settings)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showOnStartItemsTitle = arrayListOf<String>(
            getString(R.string.title_home),
            getString(R.string.title_calendar),
            getString(R.string.title_statistic),
            getString(R.string.title_phases)
        )

        val tempSettings = SettingsService.loadSettings(requireContext())
        if ( tempSettings != null) settings = tempSettings

        val cancelButton = binding.btnSettingsCancel
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val spinnerShowOnStart = binding.spinnerSettingsShowOnStart
        val aa =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, showOnStartItemsTitle)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerShowOnStart.adapter = aa
        var pos = 0
        showOnStartItemsIds.forEachIndexed { index, item -> if (item == settings.showOnStart) pos = index }
        spinnerShowOnStart.setSelection(pos)
        Log.d("selection", pos.toString())

        val saveButton = binding.btnSettingsSave
        saveButton.setOnClickListener {
           val newSettings = Settings(
                daysOnHome = textDaysOnHome.text.toString().toInt(),
                yearsOnStatistic = textYearsOnStatistic.text.toString().toInt(),
                showOnStart = showOnStartItemsIds[spinnerShowOnStart.selectedItemPosition]
            )
            SettingsService.saveSettings(newSettings, requireContext())
            if ( listener != null) listener!!.onSettingsChanged(newSettings)
            parentFragmentManager.popBackStack()
        }

        textDaysOnHome = binding.edittextSettingsDaysonhome
        textDaysOnHome.setText(settings.daysOnHome.toString())

        textYearsOnStatistic = binding.edittextSettingsYearsinstatistic
        textYearsOnStatistic.setText(settings.yearsOnStatistic.toString())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}