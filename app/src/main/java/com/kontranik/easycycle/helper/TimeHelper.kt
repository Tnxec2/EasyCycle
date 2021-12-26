package com.kontranik.easycycle.helper

import java.util.*

class TimeHelper {

    companion object {
        fun getDifferenceInDays(date1: Date, date2: Date): Int {
            val timeDifference = date1.time - date2.time;
            return (timeDifference / 1000 / 60 / 60 / 24).toInt()
        }

        fun isEqual(date1: Date, date2: Date): Boolean{
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = date1
            cal2.time = date2
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }

        fun isLess(date1: Date, date2: Date): Boolean{
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = date1
            cal2.time = date2
            return cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)
                    || ( cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                         && cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH) )
                    || ( cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                         && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                         && cal1.get(Calendar.DAY_OF_MONTH) < cal2.get(Calendar.DAY_OF_MONTH))
        }

        fun isGreat(date1: Date, date2: Date): Boolean{
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = date1
            cal2.time = date2
            return cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)
                    || ( cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH) )
                    || ( cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DAY_OF_MONTH) > cal2.get(Calendar.DAY_OF_MONTH))
        }
    }
}