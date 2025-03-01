package com.shinjaehun.winternotesfirebase.common

import android.annotation.SuppressLint
import android.util.AndroidException
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "AndroidException"
internal fun Fragment.makeToast(value: String) {
    Toast.makeText(activity, value, Toast.LENGTH_SHORT).show()
}

internal fun currentTime() = SimpleDateFormat(
    "yyyy MMMM dd, EEEE, HH:mm a",
    Locale.getDefault()).format(Date())

internal fun simpleDate(dateString: String): String {
    val toDate = SimpleDateFormat("yyyy MMMM dd, EEEE, HH:mm a").parse(dateString)
    Log.i(TAG, "to date: $toDate")
    // 장치의 locale 때문에 문제가 발생할 수 있음
    // 에뮬레이터에서는 영어 기반으로 날짜를 표시해서 FB에 document reference로 사용되었는데
    // 장치에서는 한글 기반으로 받아오려다보니 충돌이 발생했던 것 같음
    return SimpleDateFormat("yyyy MMMM dd", Locale.getDefault()).format(toDate!!)
}

internal fun currentTimeInfo() = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(
    Date(System.currentTimeMillis())
)