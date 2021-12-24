package com.kontranik.cycleora.models

import java.io.Serializable

class Phase  (
    var key: Long,
    var from: Long,
    var to: Long? = null,
    var desc: String? = null,
    var color: String? = null,  // color of current cycle
    var colorP: String? = null, // color of followed cycles (future)
    var markwholephase: Boolean? = false // has effect only in tabcalendar
) : Serializable {}