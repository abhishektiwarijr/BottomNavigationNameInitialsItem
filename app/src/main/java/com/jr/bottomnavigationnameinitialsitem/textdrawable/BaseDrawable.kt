package com.jr.bottomnavigationnameinitialsitem.textdrawable

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint

abstract class BaseDrawable @JvmOverloads constructor(
    color: Int = 0,
    filled: Boolean = true
) : Drawable() {
    @JvmField
    val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    val currentBounds = Rect()
    var id = 0
    private var currentColor = 0
    private var display = true

    protected abstract fun createPath(bounds: Rect?)
    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        createPath(bounds)
        currentBounds.set(bounds)
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    fun setBounds(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        setBounds(
            Math.round(left),
            Math.round(top),
            Math.round(right),
            Math.round(bottom)
        )
    }

    fun setBounds(bounds: RectF) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN //0
    }

    fun setFilled(filled: Boolean) {
        paint.style = if (filled) Paint.Style.FILL else Paint.Style.STROKE
    }

    fun shown(): Boolean {
        return display
    }

    fun display(display: Boolean) {
        this.display = display
    }

    fun getPaint(): Paint {
        return paint
    }

    var color: Int
        get() = paint.color
        set(color) {
            onCancelAnimations()
            animateColor(color)
        }

    fun setOutline(color: Int) {
        onCancelAnimations()
        paint.setShadowLayer(10f, 0f, 0f, color)
    }

    protected fun animateColor(color: Int) {
        currentColor = color
        paint.color = color
    }

    override fun getAlpha(): Int {
        return paint.alpha
    }

    override fun setAlpha(alpha: Int) {
        onCancelAnimations()
        paint.alpha = alpha
    }

    fun contains(x: Float, y: Float): Boolean {
        return RectF(copyBounds()).contains(x, y)
    }

    val alphaRatio: Float
        get() = Math.min(1.0f, alpha * 1.0f / (CharacterUtils.MAX_ARGB * 1.0f))

    fun setAlphaRatio(ratio: Float, fullAlpha: Boolean) {
        onCancelAnimations()
        animateAlphaRatio(ratio, fullAlpha)
    }

    private fun animateAlphaRatio(ratio: Float, fullAlpha: Boolean) {
        val alpha =
            if (fullAlpha) CharacterUtils.MAX_ARGB else Color.alpha(currentColor)
        paint.alpha = (alpha * ratio).toInt()
        paint.alpha
    }

    override fun getConstantState(): ConstantState? {
        return object : ConstantState() {
            override fun newDrawable(): Drawable {
                return this@BaseDrawable
            }

            override fun getChangingConfigurations(): Int {
                return 0
            }
        }
    }

    private fun onCancelAnimations() {
        //Override where necessary
    }

    init {
        setFilled(filled)
        this.color = color
        paint.isAntiAlias = true
        paint.isSubpixelText = true
    }
}