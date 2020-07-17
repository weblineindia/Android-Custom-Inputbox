package com.wkb.custominputbox2.utils

import android.content.Context
import android.preference.PreferenceManager
import java.util.*


class LocaleHelper {

    companion object Factory {
        fun onCreate(): LocaleHelper = LocaleHelper()
        fun getLanguage(): LocaleHelper = LocaleHelper()
    }

    var context : Context? = null
    private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun onCreate(context: Context) {

        this.context = context
        val lang: String?
        if (getLanguage(context)!!.isEmpty()) {
            lang = getPersistedData(context, Locale.getDefault().getLanguage())
        } else {
            lang = getLanguage(context)
        }

        setLocale(context, lang)

        onAttach(context)


    }


    fun onCreate(context: Context, defaultLanguage: String) {
        val lang = getPersistedData(context, defaultLanguage)
        setLocale(context, lang)

    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().getLanguage())
    }

    fun setLocale(context: Context, language: String?): Context {
        persist(context, language)
        return updateResources(context, language)
    }


    fun onAttach(context: Context): Context {
        val locale = getPersistedData(context, getLanguage(context)!!)
        return setLocale(context, locale)
    }


    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }


    private fun updateResources(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.getResources()

        val configuration = resources.getConfiguration()
        configuration.locale = locale

        resources.updateConfiguration(configuration, resources.getDisplayMetrics())
        return  context

    }


}