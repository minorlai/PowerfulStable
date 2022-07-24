package com.hzzt.powerful.activity.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.common.utils.AndroidBugWeb
import com.hzzt.common.widget.web.ComWebView
import com.hzzt.powerful.R
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivityCommonWebBinding
import com.hzzt.powerful.vmodel.MainVm
import kotlinx.android.synthetic.main.activity_common_web.*
import me.goldze.mvvmhabit.utils.KLog

/**
 * @Author: Allen
 * @CreateDate: 2022/7/22
 * @Description: 公用wv
 */
class WebComActivity : BaseA<ActivityCommonWebBinding, MainVm>() {

    companion object {
        @JvmStatic
        fun getInstanceActivity(mContext: Context, path: String?, title: String?) {
            val intent = Intent(mContext, WebComActivity::class.java)
            intent.putExtra(ComWebView.WEB_URL, path)
            intent.putExtra(ComWebView.WEB_TITLE, title)
            mContext.startActivity(intent)
        }

        fun getInstanceForResult(activity: Activity, path: String?, title: String?) {
            val intent = Intent(activity, WebComActivity::class.java)
            intent.putExtra(ComWebView.WEB_URL, path)
            intent.putExtra(ComWebView.WEB_TITLE, title)
            activity.startActivityForResult(intent, 0x111)
        }
    }


    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_common_web
    }

    override fun initArgument() {
    }

    override fun initFlow() {
        val path = intent.getStringExtra(ComWebView.WEB_URL)
        val title = intent.getStringExtra(ComWebView.WEB_TITLE)
        web_view.setmContext(this)
        head_view.setTitle(title)
        path?.let { web_view.loadUrl(it) }
        AndroidBugWeb.assistActivity(activity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        KLog.d("onShowFileChooser onActivityResult")
//        var results: Array<Uri?>? = null
//        var dataString = ""
//        val localMediaList = PictureSelector.obtainMultipleResult(data)
//        if (localMediaList != null && localMediaList.size > 0) {
//            for (media in localMediaList) {
//                dataString = media.path
//                if (!TextUtils.isEmpty(dataString) && dataString.contains("content")) {
//                    break
//                }
//            }
//        }
//        if (!TextUtils.isEmpty(dataString)) {
//            if (dataString.contains("content")) {
//                results = arrayOf(Uri.parse(dataString))
//            } else {
//                val uri: Uri = AppUtil.getMediaUriFromPath(this, dataString)
//                if (uri != null) {
//                    results = arrayOf(uri)
//                }
//            }
//        }
//        ComWebView.mFilePathCallbacks.onReceiveValue(results)
        ComWebView.mFilePathCallbacks = null
    }
}