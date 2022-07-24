package com.hzzt.powerful.activity.home

import android.os.Bundle
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.common.utils.AppUtil
import com.hzzt.powerful.R
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivityAboutMeBinding
import com.hzzt.powerful.vmodel.MainVm
import kotlinx.android.synthetic.main.activity_about_me.*

/**
 * @author: Allen
 * @date: 2022/7/23
 * @description: 关于
 */
class AboutActivity:BaseA<ActivityAboutMeBinding,MainVm>() {
    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_about_me
    }

    override fun initArgument() {
    }

    override fun initFlow() {
        tv_app_info.text=getResToStr(R.string.app_name)+" V${AppUtil.getVersionName(activity)}"
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