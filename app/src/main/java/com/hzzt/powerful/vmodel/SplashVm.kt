package com.hzzt.powerful.vmodel

import android.app.Application
import com.hzzt.common.data.http.HttpDataSourceImpl
import com.hzzt.powerful.base.BaseVM

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description: 启动页vm
 */
class SplashVm(application: Application, model: HttpDataSourceImpl?) : BaseVM(application, model) {

    override fun initActType(actType: Int) {
        this.actType = actType
        when (actType) {
            1 -> {
            }
        }
    }

}