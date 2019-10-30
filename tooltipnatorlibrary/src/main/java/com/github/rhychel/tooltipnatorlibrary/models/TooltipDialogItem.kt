package com.github.rhychel.tooltipnatorlibrary.models

import android.view.View
import androidx.annotation.LayoutRes
import com.github.rhychel.tooltipnatorlibrary.R
import com.github.rhychel.tooltipnatorlibrary.enums.TooltipMaskShape

data class TooltipDialogItem (
    val targetView: View,
    val tooltipMaskShape: TooltipMaskShape,
    @LayoutRes
    val layout: Int = R.layout.tooltip_sequence_content
)