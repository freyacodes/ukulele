package dev.arbjerg.ukulele.utils

import java.util.concurrent.TimeUnit

object TextUtils {
    fun humanReadableTime(length: Long): String {
        return if (length < 3600000) {
            String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(length),
                    TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1))
        } else {
            String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(length),
                    TimeUnit.MILLISECONDS.toMinutes(length) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1))
        }
    }
}