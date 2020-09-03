package com.example.mvvmapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.example.mvvmapplication.R

fun Context.displayToast(@StringRes message: Int){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.displayToast(message: String?){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.displayDialogError(msg: String){
    MaterialDialog(this).show {
        title(R.string.text_error)
        message(text = msg)
        positiveButton(R.string.text_ok)
    }
}

fun Context.displayDialogSuccess(msg: String){
    MaterialDialog(this).show {
        title(R.string.text_success)
        message(text = msg)
        positiveButton(R.string.text_ok)
    }
}