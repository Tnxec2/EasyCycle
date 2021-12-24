package com.kontranik.cycleora.constants

import com.kontranik.cycleora.models.Settings

class DefaultSettings {
    companion object {

        val settings = Settings(
            daysOnHome = 7,
            yearsOnStatistic = 3,
        )
        const val defaultCycleLength = 28
    }
}