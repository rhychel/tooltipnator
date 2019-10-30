package com.github.rhychel.tooltipnator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.github.rhychel.tooltipnatorlibrary.TooltipDialog
import com.github.rhychel.tooltipnatorlibrary.enums.TooltipMaskShape
import com.github.rhychel.tooltipnatorlibrary.models.TooltipDialogItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tbActionbar)
        supportActionBar?.title = "Tooltipnator"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        clickForSequence(
            TooltipDialogItem(
                tvTarget1,
                TooltipMaskShape.RECTANGLE
            ),
            TooltipDialogItem(
                btnTarget2,
                TooltipMaskShape.RECTANGLE
            ),
            TooltipDialogItem(
                btnTarget3,
                TooltipMaskShape.RECTANGLE
            ),
            TooltipDialogItem(
                btnTarget4,
                TooltipMaskShape.RECTANGLE
            ),
            TooltipDialogItem(
                btnTarget5,
                TooltipMaskShape.RECTANGLE
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu)
//        Handler().post {
//            /**
//             * When you have targets from toolbar, you need to call the initialization here.
//             * Alternatively, you can call the initialization in onCreate
//             * without putting it inside a handler depending on what you need.
//             */
//            clickForSequence(
//                TooltipDialogItem(
//                    tvTarget1,
//                    TooltipMaskShape.RECTANGLE
//                ),
//                TooltipDialogItem(
//                    btnTarget2,
//                    TooltipMaskShape.RECTANGLE
//                ),
//                TooltipDialogItem(
//                    btnTarget3,
//                    TooltipMaskShape.RECTANGLE
//                ),
//                TooltipDialogItem(
//                    btnTarget4,
//                    TooltipMaskShape.RECTANGLE
//                ),
//                TooltipDialogItem(
//                    btnTarget5,
//                    TooltipMaskShape.RECTANGLE
//                ),
//                TooltipDialogItem(
//                    findViewById(R.id.mTarget2),
//                    TooltipMaskShape.CIRCLE
//                ),
//                TooltipDialogItem(
//                    findViewById(R.id.mTarget1),
//                    TooltipMaskShape.CIRCLE
//                ),
//                TooltipDialogItem(
//                    TooltipDialog.getUpButtonFromToolbar(tbActionbar),
//                    TooltipMaskShape.CIRCLE
//                )
//            )
//        }
        return true
    }

    fun clickForSequence(vararg dialogItems: TooltipDialogItem) {
        dialogItems.forEach {
            it.targetView.setOnClickListener {
                if(rg.checkedRadioButtonId == R.id.rb3) {
                    TooltipDialog.Builder(this@MainActivity)
                        .onContentLoadedListener { view, _, index ->
                            view.findViewById<TextView>(R.id.tvSequenceTextContent).text = "Dialog ${index + 1}"
                        }
                        .build()
                        .showSequenceTooltipDialog(
                            dialogItems.toMutableList()
                        )
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(rg.checkedRadioButtonId) {
            R.id.rb1 -> {
                when(item.itemId) {
                    android.R.id.home -> {
                        TooltipDialog.Builder(this@MainActivity)
                            .onContentLoadedListener { view, _, _ ->
                                (view as TextView).text = "This is a sample text tooltip dialog for up button"
                            }
                            .build()
                            .showTextTooltipDialog(TooltipDialog.getUpButtonFromToolbar(tbActionbar), TooltipMaskShape.CIRCLE)
                    }
                    else -> {
                        TooltipDialog.Builder(this@MainActivity)
                            .onContentLoadedListener { view, _, _ ->
                                (view as TextView).text = "This is a sample text tooltip dialog for menu items"
                            }
                            .build()
                            .showTextTooltipDialog(findViewById(item.itemId), TooltipMaskShape.CIRCLE)
                    }
                }
            }
            R.id.rb2 -> {
                if(item.itemId != android.R.id.home) {
                    TooltipDialog.Builder(this@MainActivity)
                        .textContentLayout(R.layout.custom_tooltip_text_dialog)
                        .closeButtonId(R.id.btnCustomButton)
                        .onContentLoadedListener { view, _, _ ->
                            view.findViewById<TextView>(R.id.tvCustomText).text = "This is a custom text view in from a custom layout"
                        }
                        .build()
                        .showTextTooltipDialog(findViewById(item.itemId), TooltipMaskShape.CIRCLE)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
