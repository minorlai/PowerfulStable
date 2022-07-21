package com.hzzt.powerful.vmodel

import android.app.Application
import android.net.Uri
import com.hzzt.common.data.http.HttpDataSourceImpl
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.powerful.base.BaseVM
import me.goldze.mvvmhabit.utils.KLog

/**
 * @Author: Allen
 * @CreateDate: 2022/7/18
 * @Description: 主页
 */
class MainVm(application: Application, model: HttpDataSourceImpl?) : BaseVM(application, model) {

    override fun initActType(actType: Int) {
        this.actType = actType
        when (actType) {
            1 -> {
            }
        }
    }
}