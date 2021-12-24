package com.kontranik.cycleora.ui.settings

import android.os.Bundle

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import android.widget.EditText

import androidx.fragment.app.Fragment
import com.kontranik.cycleora.constants.DefaultSettings
import com.kontranik.cycleora.databinding.FragmentSettingsBinding
import com.kontranik.cycleora.models.Settings
import com.kontranik.cycleora.storage.SettingsService
import com.kontranik.cycleora.MainActivity
import com.kontranik.cycleora.R


class SettingsFragment : Fragment() {

    private var listener: SettingsListener? = null

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

        val tempSettings = SettingsService.loadSettings(requireContext())
        if ( tempSettings != null) settings = tempSettings

        val cancelButton = binding.btnSettingsCancel
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val saveButton = binding.btnSettingsSave
        saveButton.setOnClickListener {
            val newSettings = Settings(
                daysOnHome = textDaysOnHome.text.toString().toInt(),
                yearsOnStatistic = textYearsOnStatistic.text.toString().toInt(),
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