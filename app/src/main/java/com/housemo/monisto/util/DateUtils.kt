package com.housemo.monisto.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val fullDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    private val shortDateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    fun formatDate(timestamp: Long): String = fullDateFormat.format(Date(timestamp))
    fun formatShortDate(timestamp: Long): String = shortDateFormat.format(Date(timestamp))
    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))

    fun parseDate(dateString: String): Long? = try {
        inputDateFormat.parse(dateString)?.time
    } catch (e: Exception) { null }

    fun isOverdue(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        return timestamp < System.currentTimeMillis()
    }

    fun isDueSoon(timestamp: Long?, daysThreshold: Int = 7): Boolean {
        if (timestamp == null) return false
        val threshold = System.currentTimeMillis() + daysThreshold * 24 * 60 * 60 * 1000L
        return timestamp in System.currentTimeMillis()..threshold
    }

    fun today(): Long = System.currentTimeMillis()
    fun todayString(): String = inputDateFormat.format(Date())
}
