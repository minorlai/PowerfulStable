package com.hzzt.powerful.app

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hzzt.common.data.cache.CacheData
import com.hzzt.powerful.MainActivity
import com.hzzt.powerful.R
import com.hzzt.powerful.activity.error.ErrorActivity
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.twinkle.commonlib.data.constants.Constant
import me.goldze.mvvmhabit.base.BaseApplication
import me.goldze.mvvmhabit.crash.CaocConfig
import me.goldze.mvvmhabit.utils.KLog
import me.goldze.mvvmhabit.utils.Utils
import java.util.*
import java.util.concurrent.Executors

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description:
 */
class App : BaseApplication() {

    companion object {
        lateinit var app: App
        @JvmField
        var Gson: Gson = GsonBuilder().disableHtmlEscaping().create() //防止 被转义
    }


    override fun onCreate() {
        super.onCreate()
        app = this
        //默认语言
        setNormalLanguage()
        //是否开启打印日志
        KLog.init(true)   //BuildConfig.DEBUG
        Utils.init(this)
        initCatch()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfiguration(this,"en")
        }
    }

    //系统默认语言
    private fun setNormalLanguage() {
        val languageToLoad = "en"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = resources.configuration
        val metrics = resources.displayMetrics
        config.locale = Locale.ENGLISH
        resources.updateConfiguration(config, metrics)
    }


    override fun getResources(): Resources {
        //禁止app字体大小跟随系统字体大小调节
        val resources = super.getResources()
        if (resources != null && resources.configuration.fontScale != 1.0f) {
            val configuration = resources.configuration
            configuration.fontScale = 1.0f
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        return resources!!
    }


    //配置全局异常崩溃操作
    private fun initCatch() {
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
            .enabled(true) //是否启动全局异常捕获
            .showErrorDetails(true) //是否显示错误详细信息
            .showRestartButton(true) //是否显示重启按钮
            .trackActivities(true) //是否跟踪Activity
            .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
            .errorDrawable(R.drawable.app_icon) //错误图标
            .restartActivity(MainActivity::class.java) //重新启动后的activity
            .errorActivity(ErrorActivity::class.java) //崩溃后的错误activity
            //.eventListener(new YourCustomEventListener()) //崩溃后的错误监听
            .apply()
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun createConfiguration(context: Context, language: String): Context? {
        val resources = context.resources
        val locale = Locale(language)
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
        return context.createConfigurationContext(configuration)
    }


}