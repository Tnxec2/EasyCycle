package com.kontranik.easycycle.models

import java.util.*

class CDay (
    var id: Long,
    var date: Date,
    var cyclesDay: Int,
    var phases: List<Phase>,
    var color: String?) {}