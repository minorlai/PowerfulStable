package com.hzzt.powerful.activity.launch

import android.os.Bundle
import android.os.Message
import android.util.Log
import com.hzzt.common.data.cache.CacheData
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.powerful.MainActivity
import com.hzzt.powerful.R
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivitySplashBinding
import com.hzzt.powerful.vmodel.SplashVm
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description: 启动页
 */
class SplashActivity : BaseA<ActivitySplashBinding, SplashVm>() {
    private var downTime = 5
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
        handler.sendEmptyMessageDelayed(5, 1000)
    }

    override fun handleMsg(msg: Message) {
        super.handleMsg(msg)
        when(msg.what){
            5->{
                --downTime
                pro_bar.progress = 5-downTime
                if(downTime==0){
                    if (CacheData.isAgreePrivacy) {
                        startActivity(MainActivity::class.java)
                    } else
                        startActivity(PrivacyActivity::class.java)
                    finish()
                }else{
                    handler.sendEmptyMessageDelayed(5, 1000)
                }
            }
        }
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