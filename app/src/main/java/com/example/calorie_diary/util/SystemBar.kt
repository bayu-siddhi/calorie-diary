package com.example.calorie_diary.util

import android.app.Activity
import android.content.res.Configuration
import androidx.core.content.ContextCompat
import com.example.calorie_diary.R

class SystemBar {

    private fun isDarkTheme(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun setSystemBarColor(activity: Activity) {
        if (this.isDarkTheme(activity)) {
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.primary)
        }
    }

}