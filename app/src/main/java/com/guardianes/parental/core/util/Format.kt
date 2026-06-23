package com.guardianes.parental.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/** Utilidades de formato compartidas por la UI. */
object Format {

    /** Convierte milisegundos en algo como "2 h 35 min" o "48 min". */
    fun duration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return when {
            hours > 0 -> "${hours} h ${minutes} min"
            else -> "${minutes} min"
        }
    }

    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFmt = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())

    fun time(epochMillis: Long): String = timeFmt.format(Date(epochMillis))
    fun dateTime(epochMillis: Long): String = dateTimeFmt.format(Date(epochMillis))

    /** "hace 5 min", "hace 2 h", etc. */
    fun relative(epochMillis: Long, now: Long = System.currentTimeMillis()): String {
        val diff = now - epochMillis
        val min = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        return when {
            min < 1 -> "ahora mismo"
            min < 60 -> "hace $min min"
            hours < 24 -> "hace $hours h"
            else -> "hace $days d"
        }
    }
}
