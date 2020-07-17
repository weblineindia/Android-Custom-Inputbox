package com.wkb.custominputbox2

import android.content.Context
import android.graphics.Typeface
import java.util.*


object Typefaces {
    private val cache = Hashtable<String, Typeface>()

    operator fun get(c: Context, name: String): Typeface? {
        synchronized(cache) {
            if (!cache.containsKey(name)) {
                val t = Typeface.createFromAsset(c.assets, name)
                cache[name] = t
            }
            return cache[name]
        }
    }

}