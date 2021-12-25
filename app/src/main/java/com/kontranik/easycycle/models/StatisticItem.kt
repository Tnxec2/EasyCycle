package com.kontranik.easycycle.models

class StatisticItem(
    val year: String,
    val items: MutableList<LastCycle>,
    val averageCycleLength: Int,
) {
}