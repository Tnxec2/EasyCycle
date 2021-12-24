package com.kontranik.cycleora.helper

import java.util.*

class TimeHelper {

    companion object {
        fun getDifferenceInDays(date1: Date, date2: Date): Long {
            val timeDifference = date1.time - date2.time;
            return timeDifference / 1000 / 60 / 60 / 24
        }
    }
}