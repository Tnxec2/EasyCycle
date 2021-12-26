package com.kontranik.easycycle.constants

import com.kontranik.easycycle.R
import com.kontranik.easycycle.models.Settings

class DefaultSettings {
    companion object {

        val settings = Settings(
            showOnStart = R.id.navigation_info,
            daysOnHome = 7,
            yearsOnStatistic = 3,
        )
        const val defaultCycleLength: Int = 28

    }
}