package com.example.fserv.utils

import android.os.Build
import android.util.Log
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun parseJSDate(dateString: String, locale: Locale?): String{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val odt = OffsetDateTime.parse(dateString)
        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", locale);
        dtf.format(odt);
    } else {
        val dateTimeParts = dateString.split("T")
        val datePart = dateTimeParts[0].split("-")
        val dateRes = "${datePart[2]}/${datePart[1]}/${datePart[0]}"
        val timePart = dateTimeParts[1].split(":")
        val timeRes = "${timePart[0]}:${timePart[1]}"
        return "$dateRes $timeRes"
    }
}