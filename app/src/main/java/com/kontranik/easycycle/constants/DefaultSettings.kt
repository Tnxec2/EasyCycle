package com.kontranik.easycycle.constants

import com.kontranik.easycycle.models.Settings

class DefaultSettings {
    companion object {

        val settings = Settings(
            daysOnHome = 7,
            yearsOnStatistic = 3,
        )
        const val defaultCycleLength = 28
    }
}