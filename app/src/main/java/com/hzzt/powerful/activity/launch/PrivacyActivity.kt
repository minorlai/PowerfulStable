package com.hzzt.powerful.activity.launch

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import android.view.View
import com.hzzt.common.data.api.ApiUrl
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.common.listener.MyClickableSpan
import com.hzzt.common.utils.AppUtil
import com.hzzt.common.utils.SpannableStringUtil
import com.hzzt.powerful.MainActivity
import com.hzzt.powerful.R
import com.hzzt.powerful.activity.web.WebComActivity
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivityPrivacyBinding
import com.hzzt.powerful.vmodel.MainVm
import kotlinx.android.synthetic.main.activity_privacy.*

/**
 * @author: Allen
 * @date: 2022/7/22
 * @description:
 */
class PrivacyActivity: BaseA<ActivityPrivacyBinding, MainVm>() {
    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_privacy
    }

    override fun initArgument() {
    }

    override fun initFlow() {
        bottomAgreeText()
    }

    /**
     * 底部协议事件
     */
    private fun bottomAgreeText() {
        //内容编辑
        tv_hint.movementMethod = LinkMovementMethod()
        var builder = SpannableStringUtil.getBuilder(getResToStr(R.string.privacy_text1))
            .setForegroundColor(getResColor(R.color.white))
            .append(" ")
            .append(getResToStr(R.string.privacy_text2))
            .setForegroundColor(getResColor(R.color.main_color1))
            .setClickSpan(object : MyClickableSpan(activity) {
                override fun onClick(widget: View) {   //用户协议
                    WebComActivity.getInstanceActivity(activity, ApiUrl.serviceUrl, getResToStr(R.string.privacy_text2))
                }
            })
            .append(" ")
            .append(getResToStr(R.string.privacy_text4))
            .setForegroundColor(getResColor(R.color.white))
            .append(" ")
            .append(getResToStr(R.string.privacy_text3))
            .setForegroundColor(getResColor(R.color.main_color1))
            .setClickSpan(object : MyClickableSpan(activity) {
                override fun onClick(widget: View) {   //隐私政策
                    WebComActivity.getInstanceActivity(activity, ApiUrl.privacyUrl, getResToStr(R.string.privacy_text3))
                }
            })
            .create()
        tv_hint.text = builder
    }

    override fun onClickView(view: View?) {
        super.onClickView(view)
        when(view?.id){
            R.id.tv_agree->{
                startActivity(MainActivity::class.java)
                finish()
            }
        }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode === KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_DOWN) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}