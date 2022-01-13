package com.kontranik.easycycle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.models.Phase
import com.kontranik.easycycle.models.Settings
import com.kontranik.easycycle.storage.SettingsService

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    val settings = MutableLiveData<Settings>()
    val phases = MutableLiveData<List<Phase>>(listOf())

    init {
        settings.value = SettingsService.loadSettings(app) ?: DefaultSettings.settings
        loadPhases()
    }

    fun saveSettings(newSettings: Settings) {
        SettingsService.saveSettings(newSettings, app)
        settings.value = newSettings
    }

    fun loadPredefindePhases() {
        SettingsService.removeCustomPhases(app)
        loadPhases()
    }

    private fun loadPhases() {
        val list = SettingsService.loadCustomPhases(app)

        if (list.isNotEmpty())  {
            phases.value = list.sortedWith(compareBy({ it.from }, { it.to }))
        } else {
            phases.value = listOf()
        }
    }

    fun removePhase(phase: Phase) {
        val newPhases = phases.value!!.filter { it.key != phase.key }
        SettingsService.saveCustomPhases(app, newPhases)
        phases.value = newPhases
    }

    fun savePhase(phase: Phase) {
        val newPhases = SettingsService.saveCustomPhase( app, phase)
        phases.value = newPhases.sortedWith(compareBy({ it.from }, { it.to }))
    }
}