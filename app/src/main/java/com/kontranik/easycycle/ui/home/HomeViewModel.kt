package com.kontranik.easycycle.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.models.CDay
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.storage.SettingsService
import java.util.*

class HomeViewModel(val app: Application): AndroidViewModel(app) {
    val cDays = MutableLiveData<MutableList<CDay>>(mutableListOf())

    val cycleStartDate = MutableLiveData<Calendar>()

    private var lastCycleLength = DefaultSettings.defaultCycleLength

    fun loadCdays(daysOnHome: Int) {
        val lastCycle = SettingsService.loadLastCycleStart(app)
        if ( lastCycle != null) {
            val result = PhasesHelper.getDaysInfo(
                app,
                daysOnHome,
                lastCycle
            )
            if (result.isNotEmpty())  {
                cDays.value = result.toMutableList()
            }
        } else {
            cDays.value = mutableListOf()
        }
    }

    fun getLastCycleLength(): Int {
        return lastCycleLength
    }

    fun setLastCycleLength(length: Int) {
        lastCycleLength = length
    }

    fun setCycleStartDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (cycleStartDate.value == null) cycleStartDate.value = Calendar.getInstance()
        cycleStartDate.value!!.set(Calendar.YEAR, year)
        cycleStartDate.value!!.set(Calendar.MONTH, monthOfYear)
        cycleStartDate.value!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    fun saveLastCycleStart() {
        if (cycleStartDate.value != null) {
            val lastCycle =
                LastCycle(
                    cycleStart = cycleStartDate.value!!.time,
                    lengthOfLastCycle = lastCycleLength
                )
            SettingsService.saveLastCycleStart(lastCycle, app)
            val databaseService = DatabaseService(app)
            databaseService.add(lastCycle)
        }
    }
}