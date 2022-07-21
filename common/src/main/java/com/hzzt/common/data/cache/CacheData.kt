package com.hzzt.common.data.cache

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description: 缓存
 */
object CacheData {

    var accessKey: String by Preference("accessKey", "")
    var accessSecret: String by Preference("accessSecret", "")
    var securitytoken: String by Preference("securitytoken", "")

    var isAutoCheck: Boolean by Preference("isAutoCheck", true) //自动回复开关
    var hasModel: Boolean by Preference("hasModel", false) // 是否第一次进入聊天页面  下载模型
    var isMyVip: Boolean by Preference("isMyVip", false) // 该用户是否是vip
    var phototype: Int by Preference("phototype", 0) // 是否隐藏相册第四张
    var isOutlogin : Boolean by Preference("isOutlogin", false) // 是否隐藏相册第四张
    var isHidePhoto: Boolean by Preference("isHidePhoto", true) // 是否隐藏相册第四张
    var HiNum: Int by Preference("HiNum", 3) // hi没费次数
    var isVip: Boolean by Preference("isVip", false) // 对方是否是vip
    var HiTip: String by Preference("HiTip", "") // 提示文案
    var language: String by Preference("language", "") // 翻译语言
    var languageCode: Int by Preference("languageCode", 0) // 翻译语言
    var isSayHello: Boolean by Preference("isSayHello",true) // 是否弹出sayHello
    var baseServer: String by Preference("baseServer","https://api.ask-spark.com/") // 服务器域名
    var real_str: String by Preference("real_str","") // Appkey
    var real_state: Int by Preference("real_state", -1) // 认证状态
    var rongImAppkey: String by Preference("rongImAppkey","") // Appkey
    var inviter_id: String by Preference("inviter_id","") // 邀请码
    var register_state: Int by Preference("register_state", 0) // 注册三步


    var register_language: String by Preference("register_language", "") // 注册设置语言



    var userToken: String by Preference("userToken", "") // 用户 Token
//    var h5Config:MyUserInfoResp.H5UrlBean by Preference("h5Config",MyUserInfoResp.H5UrlBean())    //H5链接配置
}