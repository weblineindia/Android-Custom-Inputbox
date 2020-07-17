package com.wkb.custominputbox2.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.wkb.custominputbox2.R
import java.text.NumberFormat
import java.util.*


object CommonUtils : AppCompatActivity() {

    /**
     * Show validation Dialog.
     */

    fun showAlertDialog(context: Context, title: String, message: String?) {
        (context as Activity).runOnUiThread {
            val mAlertDialog = AlertDialog.Builder(context)
            val mDialog = mAlertDialog.create()
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.setTitle(title)
            if (null != message && !"".equals(message, ignoreCase = true)) {
                mDialog.setMessage(message)
            } else {
                mDialog.setMessage(context.getString(R.string.something_wrong))
            }

            mDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getResources().getString(R.string.dialog_btn_ok)) { dialog, which -> dialog.dismiss() }
            mDialog.show()
        }
    }

    /**
     * setCurrencyFormat method is use for convert currency input to respected country format.
     */

    fun setCurrencyFormat(context: Context,amount: String): String {

        val deviceLang = Resources.getSystem().configuration.locale.language

        val locale: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.resources.configuration.locales.get(0).country
        } else {
            locale = context.resources.configuration.locale.country
        }


        val currentLocale = Locale(deviceLang, locale)
        val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(currentLocale)

        return currencyFormatter.format(amount.toDouble())
    }



}