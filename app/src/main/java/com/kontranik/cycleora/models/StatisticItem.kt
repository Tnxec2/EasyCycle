package com.kontranik.cycleora.models

class StatisticItem(
    val year: String,
    val items: MutableList<LastCycle>,
    val averageCycleLength: Int,
) {
}