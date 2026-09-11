package com.hotaro.strictclock.utils

import com.hotaro.strictclock.data.AlarmEntity
import java.util.Calendar

object AlarmUtils {
    fun getNextTriggerTime(alarm: AlarmEntity): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.timeHour)
            set(Calendar.MINUTE, alarm.timeMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alarm.daysOfWeek == "Never" || alarm.daysOfWeek == "Daily") {
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            return calendar.timeInMillis
        }

        // Parse days
        val dayMap = mapOf(
            "Sun" to Calendar.SUNDAY,
            "Mon" to Calendar.MONDAY,
            "Tue" to Calendar.TUESDAY,
            "Wed" to Calendar.WEDNESDAY,
            "Thu" to Calendar.THURSDAY,
            "Fri" to Calendar.FRIDAY,
            "Sat" to Calendar.SATURDAY
        )

        val targetDays = mutableSetOf<Int>()
        if (alarm.daysOfWeek == "Mon-Fri") {
            targetDays.addAll(listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY))
        } else if (alarm.daysOfWeek == "Sat-Sun") {
            targetDays.addAll(listOf(Calendar.SATURDAY, Calendar.SUNDAY))
        } else {
            alarm.daysOfWeek.split(", ").forEach {
                dayMap[it]?.let { day -> targetDays.add(day) }
            }
        }

        if (targetDays.isEmpty()) {
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            return calendar.timeInMillis
        }

        // Find the next day
        for (i in 0..7) {
            val checkCalendar = calendar.clone() as Calendar
            checkCalendar.add(Calendar.DAY_OF_YEAR, i)
            if (targetDays.contains(checkCalendar.get(Calendar.DAY_OF_WEEK))) {
                if (checkCalendar.timeInMillis > System.currentTimeMillis()) {
                    return checkCalendar.timeInMillis
                }
            }
        }

        return calendar.timeInMillis
    }

    fun formatTimeUntil(targetTime: Long): String {
        val diff = targetTime - System.currentTimeMillis()
        if (diff <= 0) return "Alarm set for less than a minute from now"
        
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff / (1000 * 60 * 60)) % 24
        val minutes = (diff / (1000 * 60)) % 60
        
        val parts = mutableListOf<String>()
        if (days > 0) parts.add("$days days")
        if (hours > 0) parts.add("$hours hours")
        if (minutes > 0) parts.add("$minutes minutes")
        
        if (parts.isEmpty()) return "Alarm set for less than a minute from now"
        return "Alarm set for " + parts.joinToString(", ") + " from now"
    }
}