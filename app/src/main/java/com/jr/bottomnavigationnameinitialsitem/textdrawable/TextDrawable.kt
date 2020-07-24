package com.jr.bottomnavigationnameinitialsitem.textdrawable

import android.graphics.*
import android.graphics.Paint.Align
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

class TextDrawable @JvmOverloads constructor(
    text: String? = "",
    typeface: Typeface? = null,
    size: Float = 0f,
    color: Int = 0,
    trimColor: Int = 0,
    backgroundColor: Int = 0
) : BaseDrawable(color, true) {
    private var text: CharSequence
    private val mPaint: Paint
    var isRTL = false
    private var includeFontSpacing = false
    private var sizeText = 0f
    var textScale = 1f
    private var moveX = 0f
    private var moveY = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var textLayout: StaticLayout? = null
    private var align = Layout.Alignment.ALIGN_CENTER
    private fun createLayout(bounds: Rect?) {
        textLayout = StaticLayout(
            text, paint,
            Math.max(bounds!!.width(), 0),
            align, 1.0f, 0.0f, false
        )
    }

    override fun createPath(bounds: Rect?) {
        centerX = bounds!!.exactCenterX()
        centerY = bounds.exactCenterY()
        when (paint.textAlign) {
            Align.RIGHT -> translateX(bounds.width() / 2.toFloat())
            Align.LEFT -> translateX(-bounds.width() / 2.toFloat())
            else -> translateX(0f)
        }
        createLayout(bounds)
    }

    override fun draw(canvas: Canvas) {
        val rect = RectF(bounds)
        if (shown() && textLayout != null) {
            canvas.save()
            canvas.drawOval(rect, mPaint)
            canvas.translate(centerX + moveX, baseline)
            textLayout!!.draw(canvas)
            canvas.restore()
        }
    }

    fun getText(): String {
        return text.toString()
    }

    fun setText(text: CharSequence?): Boolean {
        return setText(text as String?)
    }

    fun setText(text: String?): Boolean {
        var text = text
        if (text == null) {
            text = ""
        }
        val changedText = this.text != text
        this.text = text
        if (changedText || textLayout == null) {
            createLayout(currentBounds)
        }
        return changedText
    }

    var textSize: Float
        get() = sizeText
        set(size) {
            sizeText = size
            paint.textSize = size
        }

    var typeFace: Typeface?
        get() = paint.typeface
        set(font) {
            paint.typeface = font
        }

    fun translateX(x: Float) {
        moveX = x
    }

    val baseline: Float
        get() = centerY + getYPositioning(paint, textScale, sizeText) + moveY

    val rawBaseline: Float
        get() = textLayout!!.getLineBaseline(0) + baseline

    fun translateY(y: Float) {
        moveY = y
    }

    fun setTextAlign(align: Layout.Alignment) {
        this.align = align
    }

    val defaultBounds: Rect
        get() = defaultBounds(
            paint, text.toString(),
            textScale, sizeText, includeFontSpacing
        )

    fun defaultBounds() {
        val bounds = defaultBounds
        setBounds(bounds)
    }

    fun setIncludeFontSpacing(includeFontSpacing: Boolean) {
        this.includeFontSpacing = includeFontSpacing
    }

    val isMultiline: Boolean
        get() = text.toString().split("\r\n|\r|\n".toRegex()).toTypedArray().size > 1

    val textWidth: Int
        get() {
            val bounds = Rect()
            paint.getTextBounds(
                text.toString(), 0,
                text.toString().length, bounds
            )
            return bounds.width()
        }

    var isBold: Boolean
        get() = paint.isFakeBoldText
        set(bold) {
            paint.isFakeBoldText = bold
        }

    fun reset() {
        display(true)
        color = 0
    }

    companion object {
        private fun getYPositioning(
            paint: TextPaint,
            textScale: Float,
            textSize: Float
        ): Int {
            paint.textSize = textSize * textScale
            val `in` =
                Math.round((paint.ascent() - paint.descent()) / (textScale * 2.0f))
            return (`in` / textScale).toInt()
        }

        private fun defaultBounds(
            paint: TextPaint,
            text: String,
            scaleText: Float,
            sizeText: Float,
            includeFontSpacing: Boolean
        ): Rect {
            var text: String? = text
            if (text == null) {
                text = ""
            }
            var maxWidth = paint.measureText(text).toInt()
            var maxHeight = 0
            val lines =
                text.split("\r\n|\r|\n".toRegex()).toTypedArray()
            for (line in lines) {
                val bounds = Rect()
                paint.getTextBounds(line, 0, line.length, bounds)
                maxWidth = Math.max(bounds.width(), maxWidth)
                maxHeight += bounds.height()
            }
            if (includeFontSpacing) {
                maxWidth += paint.fontSpacing.toInt()
            }
            return Rect(
                0, 0, maxWidth, maxHeight +
                        Math.abs(
                            getYPositioning(
                                paint,
                                scaleText,
                                sizeText
                            )
                        )
            )
        }

        fun defaultBounds(text: String, sizeText: Float): Rect {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = sizeText
            return defaultBounds(paint, text, 1f, sizeText, true)
        }

        fun autoScaleText(
            text: String,
            parentW: Float,
            parentH: Float,
            targetTextSize: Float
        ): Float {
            var targetTextSize = targetTextSize
            val wantedSize = targetTextSize
            val bounds = Rect()
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = targetTextSize
            paint.getTextBounds(text, 0, text.length, bounds)
            if (bounds.width() > 0 && bounds.height() > 0) {
                while (!fitsParent(
                        text,
                        paint,
                        parentW,
                        parentH,
                        targetTextSize
                    ) && targetTextSize > 0
                ) {
                    targetTextSize = Math.max(targetTextSize - 1, 0f)
                }
                if (targetTextSize <= 0) {
                    targetTextSize = wantedSize
                    paint.textSize = wantedSize
                }
                return targetTextSize
            }
            return targetTextSize
        }

        private fun fitsParent(
            text: String,
            paint: TextPaint,
            parentW: Float,
            parentH: Float,
            textSize: Float
        ): Boolean {
            paint.textSize = textSize
            val bounds = Rect()
            paint.getTextBounds(text, 0, text.length, bounds)
            val textLayout = StaticLayout(
                text,
                paint,
                bounds.width(),
                Layout.Alignment.ALIGN_NORMAL,
                0.0f,
                0.0f,
                false
            )
            return textLayout.height <= parentH && textLayout.width <= parentW &&
                    Math.abs(parentH - textLayout.height) >= 0 &&
                    Math.abs(parentW - textLayout.width) >= 0
        }
    }

    init {
        this.text = text ?: ""
        typeFace = typeface
        if (trimColor != 0) {
            setOutline(trimColor)
        }
        textSize = size
        defaultBounds()
        mPaint = Paint()
        if (backgroundColor != 0) mPaint.color = backgroundColor
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
    }
}