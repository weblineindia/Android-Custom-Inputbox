package com.wkb.custominputbox2.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.wkb.custominputbox2.R
import com.wkb.custominputbox2.numberkeyboard.NumberKeyboardListener
import com.wkb.custominputbox2.numberkeyboard.NumberKeyboardPopup
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException

@SuppressLint("ClickableViewAccessibility")
class AmountInput(val context: Context, val rootView: View, val editText: EditText, val isAmount: Boolean = true) : NumberKeyboardListener, NumberKeyboardPopup.PopupListener {

    private val MAX_ALLOWED_AMOUNT = 9999.99
    private val MAX_ALLOWED_DECIMALS = 2

    private var amountText: String = ""
    public lateinit var popup: NumberKeyboardPopup
    private var groupingSeparator: String? = null
    private lateinit var numberFormat: NumberFormat
    private var groupSeparatorChar = 0.toChar()

    init {
        numberFormat = NumberFormat.getInstance()

        val sym: DecimalFormatSymbols = (numberFormat as DecimalFormat).decimalFormatSymbols
        groupSeparatorChar = sym.groupingSeparator
        groupingSeparator = groupSeparatorChar.toString()


        numberFormat.maximumFractionDigits = MAX_ALLOWED_DECIMALS

        popup = NumberKeyboardPopup.Builder(rootView).setNumberKeyboardListener(this).setPopupListener(this).setKeyboardLayout(
            R.layout.popup_keyboard).build(editText)


        editText.showSoftInputOnFocus = false

        editText.setOnTouchListener { v, event ->
            showKeyPad()
            return@setOnTouchListener false
        }

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                setAmount()
                popup.dismiss()
            }
        }
    }

    fun showKeyPad() {
        if (!editText.isFocusableInTouchMode)
            return
        hideKeyBoard()
        if (!popup.isShowing) {
            popup.toggle()
        }
        if (editText.text.toString().contains("€") && isAmount) {
            editText.setText(editText.text.toString().replace(".", "").replace("€", "").trim())
            /*if (ApiClient.deviceLanguage.equals("en", true))
                editText.setText(editText.text.toString().replace(",", ".").trim())*/
        }
        if (editText.text?.length!! > 0) {
            editText.setSelection(editText.text?.length!!)
            amountText = editText.text.toString()
        }
    }

    override fun onNumberClicked(number: Int) {
        /*if ((amountText.isEmpty() || amountText == "-") && number == 0) {
            return
        }*/

        val selectionStart: Int = editText.selectionStart
        //if(selectionStart == editText.getmax)
        val selectionEnd: Int = editText.selectionEnd
        val sb = StringBuilder()
        sb.append(amountText)
        sb.insert(selectionStart, number)
        /*if (selectionEnd < amountText.length - 1) {
            sb.append(selectionEnd)
        }*/
        if (!isAmount) {
            if (sb.toString().replace(",", ".").toDouble() > 100 || editText.text.length == 5)
                return
        }
        amountText = sb.toString()
        //updateAmount(amountText, selectionStart + 1)
        showAmount(amountText)
        editText.setSelection(selectionStart + 1)
    }

    override fun onLeftAuxButtonClicked() {

        val decimalSymbol: String = ","
        /*if (ApiClient.deviceLanguage.equals("es", true))
            ","
        else
            "."*/
        if (editText.text.toString().contains(decimalSymbol))
            return

        var selectionStart: Int = editText.selectionStart
        val sb = StringBuilder()
        if (amountText.isEmpty()) {
            amountText = "0$decimalSymbol"
            sb.append(amountText)
            selectionStart++
        } else {
            sb.append(amountText)
            sb.insert(selectionStart, decimalSymbol)
        }
        if (!isAmount) {
            if (sb.toString().replace(",", ".").toDouble() >= 100)
                return
        }
        amountText = sb.toString()

        showAmount(amountText)
        editText.setSelection(selectionStart + 1)
    }

    override fun onModifierButtonClicked(number: Int) {
        when (number) {
            0 -> {
                // Minus button
                var currentSelection: Int = editText.selectionStart
                if (!amountText.startsWith("-")) {
                    amountText = "-$amountText"
                    currentSelection++
                } else {
                    amountText = amountText.substring(1)
                    currentSelection--
                }
                showAmount(amountText)
                editText.setSelection(currentSelection)
            }
            1 -> {              // Comma or Period button
                val decimalSymbol: String = if (LocaleHelper.getLanguage().getLanguage(context).equals("es", true))
                    ","
                else
                    "."
                if (editText.text.toString().contains(decimalSymbol))
                    return
                var currentSelection = editText.selectionStart
                // If we are currently at the last position, set cursor after the comma
                if (currentSelection == editText.length()) {
                    currentSelection++
                }
                amountText = if (amountText.isEmpty()) "0$decimalSymbol" else "$amountText$decimalSymbol"
                showAmount(amountText)
                editText.setSelection(currentSelection)
                /*if (!amountText.contains(",")) {
                    var currentSelection = editText.selectionStart
                    // If we are currently at the last position, set cursor after the comma
                    if (currentSelection == editText.length()) {
                        currentSelection++
                    }
                    amountText = if (amountText.isEmpty()) "0," else "$amountText,"
                    showAmount(amountText)
                    editText.setSelection(currentSelection)
                }*/
            }
            2 -> {
                // Delete button
                if (amountText!!.isEmpty()) {
                    return
                }
                val currentPos: Int = editText.getSelectionStart()
                if (currentPos > 0) {
                    editText.text = editText.text?.delete(currentPos - 1, currentPos)
                    editText.setSelection(currentPos - 1)
                }
                /* var newAmountText: String
                 val selectionStart: Int = editText.getSelectionStart()
                 val selectionEnd: Int = editText.getSelectionEnd()
                 if (amountText.length <= 1) {
                     newAmountText = ""
                 } else {
                     // Check if we have a selection
                     // Strip complete selection
                     val sb = StringBuilder()
                     if (selectionStart == selectionEnd) {
                         if (selectionStart > 1) {
                             sb.append(amountText.substring(0, selectionStart - 1))
                         }
                         if (selectionStart < amountText.length) {
                             sb.append(amountText.substring(selectionStart))
                         }
                     } else {
                         if (selectionStart > 0) {
                             sb.append(amountText.substring(0, selectionStart))
                         }
                         if (selectionEnd < amountText.length - 1) {
                             sb.append(selectionEnd)
                         }
                     }
                     newAmountText = sb.toString()
                     if (!newAmountText.isEmpty()) {
                         if (newAmountText[newAmountText.length - 1] == ',' || newAmountText[newAmountText.length - 1] == '.' ||
                                 newAmountText[newAmountText.length - 1] == ' ' || newAmountText[newAmountText.length - 1] == '€') {
                             newAmountText = newAmountText.substring(0, newAmountText.length - 1)
                         }
                         if ("0" == newAmountText) {
                             newAmountText = ""
                         }
                     }
                 }
                 //updateAmount(newAmountText, if (selectionEnd > 0) selectionEnd - 1 else 0)
                 showAmount(newAmountText)
                 editText.setSelection(if (selectionEnd > 0) selectionEnd - 1 else 0)*/
            }
            3 -> {
                // Enter button, close keyboard
                popup.dismiss()
                setAmount()
                hideKeyBoard()
            }
        }
    }

    private fun hideKeyBoard() {
        val inputMethodManager = rootView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun countCommas(haystack: String): Int {
        var count = 0
        for (element in haystack) {
            if (element == groupSeparatorChar) {
                count++
            }
        }
        return count
    }

    override fun onRightAuxButtonClicked() {
        /*if (ApiClient.deviceLanguage.equals("es", true))
            return

        val decimalSymbol = "."
        if(editText.text.toString().contains(decimalSymbol))
            return
        var currentSelection = editText.selectionStart
        // If we are currently at the last position, set cursor after the comma
        if (currentSelection == editText.length()) {
            currentSelection++
        }
        amountText = if (amountText.isEmpty()) "0$decimalSymbol" else "$amountText$decimalSymbol"
        showAmount(amountText)
        editText.setSelection(currentSelection)*/
        // Delete button
        if (amountText!!.isEmpty()) {
            return
        }
        val currentPos: Int = editText.getSelectionStart()
        if (currentPos > 0) {
            editText.text = editText.text?.delete(currentPos - 1, currentPos)
            editText.setSelection(currentPos - 1)
        }
        amountText = editText.text.toString()
    }

    override fun onOkButtonClicked() {
        popup.dismiss()
        setAmount()
        hideKeyBoard()
    }

    /**
     * Update new entered amount if it is valid.
     *
     * @param newAmountText   new text to parse and format
     * @param newSelectionIdx position that should be the new selection if possible
     */
    @Suppress("NAME_SHADOWING")
    private fun updateAmount(newAmountText: String, newSelectionIdx: Int) {
        var newSelectionIdx = newSelectionIdx
        try {
            val numOldCommas = countCommas(newAmountText)
            val newAmount = newAmountText.toDouble()//if (newAmountText.isEmpty()) 0.0 else numberFormat.parse(newAmountText.replace(groupingSeparator!!, "")).toDouble()
            //newAmount = Math.min(newAmount, MAX_ALLOWED_AMOUNT)
            amountText = newAmount.toString()//numberFormat.format(newAmount)
            showAmount(amountText)
            val newLength = amountText.length
            newSelectionIdx -= numOldCommas - countCommas(amountText)
            editText.setSelection(if (newSelectionIdx <= newLength) newSelectionIdx else newLength)
        } catch (e: ParseException) {
            Log.e("Err", "Cannot parse amount '$newAmountText'")
        }
    }

    /**
     * Shows amount in UI.
     */
    private fun showAmount(amount: String?) {
        editText.setText(if (amount!!.isEmpty()) "" else amount)
    }

    override fun onPopupVisibilityChanged(isShown: Boolean) {
        val decimalSymbol = popup.keyboard.findViewById<TextView>(R.id.keyComma)
        popup.keyboard.showLeftAuxButton()
        popup.keyboard.showRightAuxButton()
        if (LocaleHelper.getLanguage().getLanguage(context).equals("es", true))
            decimalSymbol.text = ","
        else
            decimalSymbol.text = "."
    }

    fun setAmount() {
        if (!isAmount)
            return
        try {
            val tmpValue = editText.text.toString()/*.replace(".", "")*/
            val value = tmpValue.replace("€", "").replace(",", ".").trim()
            if (!value.isNullOrEmpty() && !value.equals("")) {
                editText.setText(CommonUtils.setCurrencyFormat(context,value))
                editText.setSelection(editText.text?.length!!)
            }
        } catch (e: Exception) {
            Log.d("exception", e.message)
        }
    }

}