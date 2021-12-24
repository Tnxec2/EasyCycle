package com.kontranik.cycleora.models

import java.util.*

class CDay (
    var id: Long,
    var date: Date,
    var cyclesDay: Long,
    var phases: List<Phase>,
    var color: String?) {}