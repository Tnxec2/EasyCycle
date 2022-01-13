package com.kontranik.easycycle.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kontranik.easycycle.constants.DefaultPhasesData
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.models.Phase
import com.kontranik.easycycle.models.Settings


class SettingsService {
    companion object {
        private val gson = Gson()

        fun saveLastCycleStart(lastCycle: LastCycle, context: Context) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val sharedPreferencesEditor = sharedPreferences.edit()
            val serializedObject = gson.toJson(lastCycle)
            sharedPreferencesEditor.putString(LAST_CYCLE_DATE, serializedObject)
            sharedPreferencesEditor.apply()
        }

        fun loadLastCycleStart(context: Context): LastCycle? {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val lastCycleString = sharedPreferences.getString(LAST_CYCLE_DATE, null)
            return gson.fromJson(lastCycleString, LastCycle::class.java)
        }

        fun saveSettings(settings: Settings, context: Context) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val sharedPreferencesEditor = sharedPreferences.edit()
            val serializedObject = gson.toJson(settings)
            sharedPreferencesEditor.putString(APP_SETTINGS, serializedObject)
            sharedPreferencesEditor.apply()
        }

        fun loadSettings(context: Context): Settings? {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val settings = sharedPreferences.getString(APP_SETTINGS, null)
            return gson.fromJson(settings, Settings::class.java)
        }

        fun saveCustomPhase(context: Context, phase: Phase): MutableList<Phase> {
            var phases = loadCustomPhases(context).toMutableList()
            var updated = false
            for( i in phases.indices) {
                val it = phases[i]
                if ( it.key == phase.key) {
                    it.from = phase.from
                    it.to = phase.to
                    it.desc = phase.desc
                    it.color = phase.color
                    it.colorP = phase.colorP
                    it.markwholephase = phase.markwholephase
                    updated = true
                    break
                }
            }
            if (!updated) phases.add(phase)
            saveCustomPhases(context, phases)
            return phases
        }

        fun saveCustomPhases(context: Context, phases: List<Phase>) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val sharedPreferencesEditor = sharedPreferences.edit()
            val resultSet = mutableSetOf<String>()
            phases.forEach {
                val serializedObject = gson.toJson(it)
                if (serializedObject != null) {
                    resultSet.add(serializedObject)
                }
            }
            sharedPreferencesEditor.putStringSet(CUSTOM_PHASES, resultSet)
            sharedPreferencesEditor.apply()
        }

        fun loadCustomPhases(context: Context): List<Phase> {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val phasesSet = sharedPreferences.getStringSet(CUSTOM_PHASES, null)
            val result = mutableListOf<Phase>()
            return if ( phasesSet != null) {
                phasesSet.forEach {
                    val phase = gson.fromJson(it, Phase::class.java)
                    if ( phase != null) result.add(phase)
                }
                result
            } else {
                DefaultPhasesData.ar
            }
        }

        fun removeCustomPhases(context: Context) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val sharedPreferencesEditor = sharedPreferences.edit()
            sharedPreferencesEditor.remove(CUSTOM_PHASES)
            sharedPreferencesEditor.apply()
        }

        private const val preferenceFileName = "EASYCYCLE_PREFS"
        private const val LAST_CYCLE_DATE = "LAST_CYCLE_DATE"
        private const val APP_SETTINGS = "APP_SETTINGS"
        private const val CUSTOM_PHASES = "CUSTOM_PHASES"
    }
}