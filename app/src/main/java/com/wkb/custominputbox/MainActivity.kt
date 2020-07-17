package com.wkb.custominputbox

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wkb.custominputbox2.utils.AmountInput
import com.wkb.custominputbox2.utils.DrawableClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Right click event of drawable using DrawableClickListener.
         */
        edtUserName.setDrawableClickListener(object : DrawableClickListener {

            override fun onRightClick() {
                Toast.makeText(this@MainActivity, "Right Drawable click", Toast.LENGTH_SHORT).show()
            }
        })

        /**
         * set amount format in input box with specific device language.
         */

        AmountInput(this,main_view, etAmount)

    }
}
