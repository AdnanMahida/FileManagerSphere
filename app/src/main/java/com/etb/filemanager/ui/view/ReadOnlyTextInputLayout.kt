package com.etb.filemanager.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.View

import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.shape.MaterialShapeDrawable

class ReadOnlyTextInputLayout : AppCompatEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        setTextIsSelectable(isTextSelectable)
    }


    override fun setTextIsSelectable(selectable: Boolean) {
        super.setTextIsSelectable(selectable)

        if (selectable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusable = View.FOCUSABLE_AUTO
            }
        } else {
            isClickable = false
            isFocusable = false
        }
        background = background
    }

    @Deprecated("Deprecated in Java")
    override fun setBackgroundDrawable(background: Drawable?) {
        var background = background
        if (isTextSelectable) {
            if (background is RippleDrawable) {
                val content = background.findDrawableByLayerId(0)
                if (content is MaterialShapeDrawable) {
                    background = content
                }
            }
        } else {
            if (background is MaterialShapeDrawable) {
                background = addRippleEffect(background)
            }
        }
        super.setBackgroundDrawable(background)
    }

    @SuppressLint("ResourceType")
    private fun addRippleEffect(boxBackground: MaterialShapeDrawable): Drawable {
        val rippleColor = context.getColorStateList(androidx.appcompat.R.attr.colorControlHighlight)
        val mask = MaterialShapeDrawable(boxBackground.shapeAppearanceModel)
            .apply { setText(Color.WHITE) }
        return RippleDrawable(rippleColor, boxBackground, mask)
    }
}