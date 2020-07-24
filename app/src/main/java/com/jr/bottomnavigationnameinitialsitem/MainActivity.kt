package com.jr.bottomnavigationnameinitialsitem

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.jr.bottomnavigationnameinitialsitem.textdrawable.TextDrawable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavView()
    }

    private fun initBottomNavView() {
        val textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11f, resources.displayMetrics)
        val td = TextDrawable(
            "JR", //text
            ResourcesCompat.getFont(this, R.font.fira_mono), //font
            textSize, //text size
            Color.WHITE, // text color
            0, //outline color
            ContextCompat.getColor(this, R.color.colorBlack) //background color
        )
        bnv.menu.getItem(3).icon = td
    }
}