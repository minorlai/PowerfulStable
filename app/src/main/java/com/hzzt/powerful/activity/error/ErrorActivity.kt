package com.hzzt.powerful.activity.error

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.powerful.R
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivityErrorBinding
import com.hzzt.powerful.vmodel.MainVm
import kotlinx.android.synthetic.main.activity_error.*
import me.goldze.mvvmhabit.crash.CaocConfig
import me.goldze.mvvmhabit.crash.CustomActivityOnCrash

/**
 * @Author: Allen
 * @CreateDate: 2022/6/6
 * @Description:
 */
class ErrorActivity: BaseA<ActivityErrorBinding, MainVm>() {
    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_error
    }

    override fun initArgument() {
    }

    override fun initFlow() {
        initCatchInfo()
    }

    
    private fun initCatchInfo(){

        val config: CaocConfig = CustomActivityOnCrash.getConfigFromIntent(intent)

        if (config.isShowRestartButton && config.restartActivityClass != null) {
            error_activity_restart_button.text = getResToStr(R.string.error_activity_restart_app)
            error_activity_restart_button.setOnClickListener {
                CustomActivityOnCrash.restartApplication(
                    activity,
                    config
                )
            }
        } else {
            error_activity_restart_button.setOnClickListener {
                CustomActivityOnCrash.closeApplication(
                    activity,
                    config
                )
            }
        }

        if (config.isShowErrorDetails) {
            error_activity_more_info_button.setOnClickListener { //We retrieve all the error data and show it
                val dialog: AlertDialog =
                    AlertDialog.Builder(activity)
                        .setTitle(getResToStr(R.string.error_activity_error_details_title))
                        .setMessage(
                            CustomActivityOnCrash.getAllErrorDetailsFromIntent(
                                activity,
                                intent
                            )
                        )
                        .setPositiveButton(
                            getResToStr(R.string.error_activity_error_details_close),
                            null
                        )
                        .setNeutralButton(getResToStr(R.string.error_activity_error_details_copy),
                            DialogInterface.OnClickListener { dialog, which ->
                                copyErrorToClipboard()
                                showMsg(R.string.error_activity_error_details_copied)
                            })
                        .show()
                val textView =
                    dialog.findViewById<View>(android.R.id.message) as TextView
                textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.text14)
                )
            }
        } else {
            error_activity_more_info_button.visibility = View.GONE
        }

        val defaultErrorActivityDrawableId: Int? = config.errorDrawable

        if (defaultErrorActivityDrawableId != null) {
            error_activity_image.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources, defaultErrorActivityDrawableId,
                    theme
                )
            )
        }
    }

    private fun copyErrorToClipboard() {
        val errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(
            activity,
            intent
        )
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getResToStr(R.string.error_activity_error_details_clipboard_label),errorInformation)
        clipboard.setPrimaryClip(clip)
    }
    
    override fun initLayoutUpdate(common: CommonUI?): Int {
    return 0
    }

    override fun initServerResponse(common: CommonResponse<*>?): Int {
       return 0
    }

    override fun initVM(): Class<MainVm> {
        return MainVm::class.java
    }
}