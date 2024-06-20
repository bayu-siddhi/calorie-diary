package com.example.calorie_diary.util

import java.util.Locale

class StringFunction {

    fun titleCase(text: String): String {
        return text.split(" ").joinToString(" ") { it ->
            it.replaceFirstChar { that ->
                if (that.isLowerCase()) that.titlecase(
                    Locale.getDefault()
                ) else that.toString()
            }
        }
    }

}