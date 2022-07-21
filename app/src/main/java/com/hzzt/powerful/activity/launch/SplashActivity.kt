package com.hzzt.powerful.activity.launch

import android.os.Bundle
import android.util.Log
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.powerful.MainActivity
import com.hzzt.powerful.R
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivitySplashBinding
import com.hzzt.powerful.vmodel.SplashVm

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description: 启动页
 */
class SplashActivity : BaseA<ActivitySplashBinding, SplashVm>() {
    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_splash
    }

    override fun initArgument() {
        initHandler()
    }

    override fun initFlow() {
        initPageToFinish()
    }

    //跳转首页
    private fun initPageToFinish() {
        handler.postDelayed({
            startActivity(MainActivity::class.java)
            finish()
        }, 2500)
    }

    override fun initLayoutUpdate(common: CommonUI): Int {
        return 0
    }

    override fun initServerResponse(common: CommonResponse<*>?): Int {
        return 0
    }

    override fun initVM(): Class<SplashVm> {
        return SplashVm::class.java
    }

}