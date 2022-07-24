package com.hzzt.common.data.cache

import com.hzzt.common.entity.resp.CurrentServerResp
import com.hzzt.common.entity.resp.ServerResp

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description: 缓存
 */
object CacheData {

    //是否同意协议
    var isAgreePrivacy: Boolean by Preference("isAgreePrivacy", false)

    var userToken: String by Preference("userToken", "") // 用户 Token
    var serverConfig: CurrentServerResp by Preference("serverConfig", CurrentServerResp())    //当前连接对象配置

    //倒计时时间
    var downTimer:Int by Preference("downTimer",3*60*60)  //默认3个小时
}