package com.github.rhychel.tooltipnatorlibrary

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.addListener
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.github.rhychel.tooltipnatorlibrary.enums.TooltipMaskShape
import com.github.rhychel.tooltipnatorlibrary.models.TooltipDialogItem
import kotlinx.android.synthetic.main.tooltip_dialog.view.*
import kotlin.math.min

class TooltipDialog private constructor() {

    private var onTooltipClosed: () -> Unit = {}
    private var onContentLoaded: (View, TooltipDialog, Int) -> Unit = { _, _, _->}

    private var sequencedDialogItems: MutableList<TooltipDialogItem> = mutableListOf()
    private var displayDialogItemIndex: Int = 0

    @LayoutRes
    private var textContentLayout: Int = R.layout.tooltip_text_content
    @LayoutRes
    private var closeButtonId: Int? = null

    private lateinit var resources: Resources
    private lateinit var rootContentArea: ViewGroup
    private lateinit var flTooltip: FrameLayout
    private lateinit var textTooltipDialog: View
    private lateinit var layoutInflater: LayoutInflater


    class Builder (
        private val activity: Activity
    ) {

        @LayoutRes
        private var bTextContentLayout: Int = R.layout.tooltip_text_content
        @LayoutRes
        private var bCloseButtonId: Int? = null
        private var bOnTooltipClosed: () -> Unit = {}
        private var bOnContentLoaded: (View, TooltipDialog, Int) -> Unit = { _, _, _->}
        private var disableTapAnywhere = false

        fun closeButtonId(id: Int): Builder {
            this.bCloseButtonId = id
            return this
        }

        fun onTooltipClosedListener(onTooltipClosed: () -> Unit): Builder {
            this.bOnTooltipClosed = onTooltipClosed
            return this
        }

        fun textContentLayout(@LayoutRes layout: Int): Builder {
            this.bTextContentLayout = layout
            return this
        }

        fun onContentLoadedListener(onContentLoaded: (View, TooltipDialog, Int) -> Unit): Builder {
            this.bOnContentLoaded = onContentLoaded
            return this
        }

        fun disableTapAnywhere(shouldBeDisabled: Boolean): Builder {
            this.disableTapAnywhere = shouldBeDisabled
            return this
        }

        fun build(window: Window? = null): TooltipDialog {
            val tooltipHelper = TooltipDialog()
            with(tooltipHelper) {
                layoutInflater = activity.layoutInflater
                resources = activity.resources
                onTooltipClosed = bOnTooltipClosed
                onContentLoaded = bOnContentLoaded
                textContentLayout = bTextContentLayout
                closeButtonId = bCloseButtonId

                flTooltip = FrameLayout(activity)
                flTooltip.id = R.id.flTooltip
                flTooltip.visibility = View.INVISIBLE
                flTooltip.alpha = 0.0f
                flTooltip.setOnClickListener {
                    if(!disableTapAnywhere) { // Tap anywhere!
                        dismissTooltipDialog()
                    }
                }

                val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                rootContentArea = (activity.window.takeUnless { window != null } ?: window!!).decorView.findViewById(android.R.id.content) as ViewGroup
                rootContentArea.addView(flTooltip, layoutParams)
            }

            return tooltipHelper
        }
    }

    fun showNextDialog() {
        ObjectAnimator.ofFloat(
            flTooltip,
            "alpha",
            1.0f, 0.0f
        ).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { va ->
                val alpha = (va.animatedValue as Float)
                flTooltip.alpha = alpha
                flTooltip.requestLayout()
                println("HERE! Alpha: $alpha")
            }
            addListener(
                onEnd = {
                    displayDialogItemIndex++
                    if(displayDialogItemIndex < sequencedDialogItems.size) {
                        flTooltip.removeAllViews()
                        showDialogItem(sequencedDialogItems[displayDialogItemIndex])
                    }
                    else
                        resetRootContent()
                }
            )
            start()
        }
    }

    fun showPreviousDialog() {
        ObjectAnimator.ofFloat(
            flTooltip,
            "alpha",
            1.0f, 0.0f
        ).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { va ->
                val alpha = (va.animatedValue as Float)
                flTooltip.alpha = alpha
                flTooltip.requestLayout()
                println("HERE! Alpha: $alpha")
            }
            addListener(
                onEnd = {
                    displayDialogItemIndex--
                    if(displayDialogItemIndex > -1) {
                        flTooltip.removeAllViews()
                        showDialogItem(sequencedDialogItems[displayDialogItemIndex])
                    }
                    else
                        resetRootContent()
                }
            )
            start()
        }
    }

    fun dismissTooltipDialog() {
        ObjectAnimator.ofFloat(
            flTooltip,
            "alpha",
            1.0f, 0.0f
        ).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { va ->
                val alpha = (va.animatedValue as Float)
                flTooltip.alpha = alpha
                flTooltip.requestLayout()
                println("HERE! Alpha: $alpha")
            }
            addListener(
                onEnd = {
                    resetRootContent()
                }
            )
            start()
        }
    }

    private fun resetRootContent() {
        displayDialogItemIndex = 0
        rootContentArea.removeView(flTooltip)
        onTooltipClosed()
    }

    fun showTextTooltipDialog(targetView: View, tooltipMaskShape: TooltipMaskShape) {
        textTooltipDialog = layoutInflater.inflate(R.layout.tooltip_dialog, null)
        textTooltipDialog.layoutDialogContent.layoutResource = textContentLayout
        val content = textTooltipDialog.layoutDialogContent.inflate()
        onContentLoaded(content, this, 0)
        if(closeButtonId != null) {
            try {
                content.findViewById<View>(closeButtonId!!).setOnClickListener {
                    dismissTooltipDialog()
                }
            } catch (e: Exception) {
                throw IllegalStateException("Unable to find button with that id")
            }
        }
        else {
            try {
                content.findViewById<View>(R.id.btnDialogClose).setOnClickListener {
                    dismissTooltipDialog()
                }
            } catch (e: Exception) {
                closeButtonId = null
                println("Unable to find close button")
            }
        }

        textTooltipDialog.post {
            println("TextTooltip: width=${textTooltipDialog.width}, height=${textTooltipDialog.height}")

            repositionDialogBasedOnTarget(targetView, content, tooltipMaskShape)

            flTooltip.visibility = View.VISIBLE
            flTooltip.alpha = 0.0f

            ObjectAnimator.ofFloat(
                flTooltip,
                "alpha",
                0.0f, 1.0f
            ).apply {
                duration = ANIMATION_DURATION
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { va ->
                    val alpha = (va.animatedValue as Float)
                    flTooltip.alpha = alpha
                    flTooltip.requestLayout()
                    println("Alpha: $alpha")
                }
                start()
            }

        }

        flTooltip.addView(textTooltipDialog, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    fun showSequenceTooltipDialog(dialogItems: MutableList<TooltipDialogItem>) {
        this.sequencedDialogItems = dialogItems

        displayDialogItemIndex = 0

        showDialogItem(dialogItems[displayDialogItemIndex])
    }

    fun showDialogItem(item: TooltipDialogItem) {
        textTooltipDialog = layoutInflater.inflate(R.layout.tooltip_dialog, null)
        textTooltipDialog.layoutDialogContent.layoutResource = item.layout
        val content = textTooltipDialog.layoutDialogContent.inflate()
        onContentLoaded(content, this, displayDialogItemIndex)
        closeButtonId = R.id.btnDialogClose
        try {
            content.findViewById<View>(closeButtonId!!).setOnClickListener {
                dismissTooltipDialog()
            }
        } catch (e: Exception) {
            closeButtonId = null
            println("Unable to find close button")
        }

        try {
            content.findViewById<View>(R.id.btnDialogBack).setOnClickListener {
                showPreviousDialog()
            }
        } catch (e: Exception) {
            println("Unable to find back button")
        }

        try {
            content.findViewById<View>(R.id.btnDialogNext).setOnClickListener {
                showNextDialog()
            }
        } catch (e: Exception) {
            println("Unable to find next button")
        }

        textTooltipDialog.post {
            println("TextTooltip: width=${textTooltipDialog.width}, height=${textTooltipDialog.height}")

            repositionDialogBasedOnTarget(item.targetView, content, item.tooltipMaskShape)

            flTooltip.visibility = View.VISIBLE
            flTooltip.alpha = 0.0f

            ObjectAnimator.ofFloat(
                flTooltip,
                "alpha",
                0.0f, 1.0f
            ).apply {
                duration = ANIMATION_DURATION
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { va ->
                    val alpha = (va.animatedValue as Float)
                    flTooltip.alpha = alpha
                    flTooltip.requestLayout()
                    println("Alpha: $alpha")
                }
                start()
            }

        }

        flTooltip.addView(textTooltipDialog, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun repositionDialogBasedOnTarget(targetView: View, content: View, tooltipMaskShape: TooltipMaskShape) {

        val (positionX, positionY) = getTargetViewPosition(targetView)

        val tooltipX = positionX + (targetView.width / 2) - (textTooltipDialog.width / 2)
        val tooltipY = positionY - CLIP_PADDING_OFFSET - textTooltipDialog.height

        val pointerX = positionX + (targetView.width / 2)
        var pointerWidth = (textTooltipDialog.layoutPointerBottom.width / 2) + content.marginStart

        println("TextTooltip startMargin=${content.marginStart}, pointerWidth=$pointerWidth")

        when {
            (tooltipX < 0) or (tooltipX + textTooltipDialog.width > flTooltip.width) -> { // Check whether the dialog over shoots to left or right
                textTooltipDialog.x = 0.0f + content.marginStart
            }
            else -> {
                println("TextTooltip is in its usual location")
                pointerWidth -= content.marginStart

                textTooltipDialog.x = tooltipX
            }
        }

        if(tooltipY < 0) {
            textTooltipDialog.layoutPointerTop.visibility = View.VISIBLE
            textTooltipDialog.layoutPointerBottom.visibility = View.GONE
            textTooltipDialog.layoutPointerTop.x = pointerX - pointerWidth
        }
        else {
            textTooltipDialog.layoutPointerTop.visibility = View.GONE
            textTooltipDialog.layoutPointerBottom.visibility = View.VISIBLE
            textTooltipDialog.layoutPointerBottom.x = pointerX - pointerWidth
        }

        textTooltipDialog.y = tooltipY.takeIf {
            it > -1// Check if tooltip.y is way above its parent layout
        } ?: (positionY + CLIP_PADDING_OFFSET + targetView.height)

        /**
         * Content
         */
        repositionContentOfDialog(positionX, targetView, content)

        println("TextTooltip: tooltipY=$tooltipY")
        flTooltip.background = when(tooltipMaskShape) {
            TooltipMaskShape.RECTANGLE -> rectangleMaskBackground(targetView)
            TooltipMaskShape.CIRCLE -> circleMaskBackground(targetView)
        }
    }

    private fun repositionContentOfDialog(positionX: Float, targetView: View, content: View) {
        val contentX = positionX - (content.width / 2) + (targetView.width / 2)
        when {
            (contentX < 0) -> {
                println("TextTooltip content is in (contentX < 0)")
                content.x = 0.0f
            }
            (contentX + content.width > flTooltip.width) -> {
                println("TextTooltip content is in (contentX + content.width > flTooltip.width) ${content.marginStart}")
                content.x = flTooltip.width.toFloat() - (content.width + (content.marginEnd * 2))
            }
            else -> { // content.width < textTooltipDialog.width
                println("TextTooltip content is in else")
                content.x = contentX
            }
        }
    }

    private fun rectangleMaskBackground(targetView: View) : Drawable {
        val bitmap = Bitmap.createBitmap(
            rootContentArea.width, rootContentArea.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.parseColor("#66000000"))

        /**
         * Mask
         */
        val maskPaint = Paint()
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        val (positionX, positionY) = getTargetViewPosition(targetView)
        println("TextTooltip(x,y) = ($positionX, $positionY)")

        canvas.drawBitmap(drawRectangle(targetView.width, targetView.height),
            positionX - (CLIP_PADDING / 2),
            positionY - (CLIP_PADDING / 2),
            maskPaint)

        return BitmapDrawable(resources, bitmap)
    }

    private fun drawRectangle(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            width + CLIP_PADDING,
            height + CLIP_PADDING,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // Initialize a new Paint instance to draw the Rectangle
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.RED
        paint.isAntiAlias = true

        // Finally, draw the circle on the canvas
        canvas.drawRect(
            canvas.width / 2.0f,
            canvas.width / 2.0f,
            canvas.width / 2.0f,
            canvas.width / 2.0f,
            paint
        )

        return bitmap
    }

    private fun circleMaskBackground(targetView: View) : Drawable {
        val bitmap = Bitmap.createBitmap(
            rootContentArea.width, rootContentArea.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.parseColor("#66000000"))

        /**
         * Mask
         */
        val maskPaint = Paint()
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        val (positionX, positionY) = getTargetViewPosition(targetView)
        println("TextTooltip(x,y) = ($positionX, $positionY)")

        canvas.drawBitmap(drawCircle(targetView.width, targetView.height),
            positionX - (CLIP_PADDING / 2),
            positionY - (CLIP_PADDING / 2),
            maskPaint)

        return BitmapDrawable(resources, bitmap)
    }

    private fun drawCircle(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            width + CLIP_PADDING,
            height + CLIP_PADDING,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // Initialize a new Paint instance to draw the Circle
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.RED
        paint.isAntiAlias = true

        // Calculate the available radius of canvas
        val radius = min(canvas.width.toFloat(), canvas.height / 2.0f)

        // Finally, draw the circle on the canvas
        canvas.drawCircle(
            canvas.width / 2.0f,
            canvas.height / 2.0f,
            radius,
            paint // Paint
        )

        return bitmap
    }

    private fun getTargetViewPosition(targetView: View): TargetViewPosition {
        val offsetViewBounds = Rect()
        targetView.getDrawingRect(offsetViewBounds)

        val locationInWindow = IntArray(2) // [x, y]
        targetView.getLocationInWindow(locationInWindow)
//        rootContentArea.offsetDescendantRectToMyCoords(targetView, offsetViewBounds)
        /*

            offsetViewBounds.left.toFloat(),
            offsetViewBounds.top.toFloat()
         */
        return TargetViewPosition(
            locationInWindow[0].toFloat(),
            locationInWindow[1].toFloat()
        )
    }

    private data class TargetViewPosition (
        val x: Float,
        val y: Float
    )

    companion object {
        private const val CLIP_PADDING = 20
        private const val CLIP_PADDING_OFFSET = 13
        private const val ANIMATION_DURATION = 250L

        fun getUpButtonFromToolbar(toolbar: Toolbar): View {
            toolbar.navigationContentDescription = "up"
            val outViews = ArrayList<View>()
            toolbar.findViewsWithText(
                outViews,
                "up",
                View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
            )

            if(outViews.isEmpty())
                throw IllegalStateException("You need to call `supportActionBar?.setDisplayHomeAsUpEnabled(true)` first before using this method.")
            return outViews[0]
        }

    }
}