package com.twinkle.chat.app

import com.hzzt.common.data.http.HttpDataSourceImpl
import com.hzzt.common.data.http.RetrofitHelper

/**
 * @author: Allen
 * @date: 2022/7/21
 * @description: 注入全局的数据仓库，后期可以考虑使用Dagger2。（根据项目实际情况搭建，千万不要为了架构而架构）
 */
object Injection {
    fun provideDemoRepository(): HttpDataSourceImpl {
        //网络API服务
        var apiService = RetrofitHelper.service()
        //两条分支组成一个数据仓库
        return HttpDataSourceImpl.getInstance(apiService)
    }
}