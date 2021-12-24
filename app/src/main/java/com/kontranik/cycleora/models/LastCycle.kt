package com.kontranik.cycleora.models

import java.util.*

class LastCycle (
    var id: Long? = null,
    var year: Int? = null,
    var month: Int? = null,
    var cycleStart: Date,
    var lengthOfLastCycle: Int
    )  {

    var clickCount = 0
    var lastClickedTime = 0L

    fun clickedToRemove(): Boolean {
        val now = Date().time
        if ( now < lastClickedTime + 1000) {
            clickCount += 1
            lastClickedTime = now
        } else {
            clickCount = 0
            lastClickedTime = now
        }
        if (clickCount > 5) {
            clickCount = 0
            lastClickedTime = now
            return true
        }
        return false
    }
}