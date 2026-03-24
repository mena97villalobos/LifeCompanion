package com.mena97villalobos.local.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class DateConverter {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(date: String?): LocalDate? = date?.let { LocalDate.parse(it) }
}
