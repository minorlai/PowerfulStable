package com.hzzt.powerful

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.hzzt.common.data.api.ApiUrl
import com.hzzt.common.data.cache.CacheData
import com.hzzt.common.dialog.AppExitDialog
import com.hzzt.common.dialog.ConnectCreateDialog
import com.hzzt.common.dialog.DisconnectDialog
import com.hzzt.common.dialog.RemindDownDialog
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.common.entity.resp.CurrentServerResp
import com.hzzt.common.utils.AppUtil
import com.hzzt.common.utils.ClickUtil
import com.hzzt.common.utils.MyCountDownTimer
import com.hzzt.powerful.activity.home.AboutActivity
import com.hzzt.powerful.activity.home.ConnectStateActivity
import com.hzzt.powerful.activity.home.ServerActivity
import com.hzzt.powerful.activity.web.WebComActivity
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivityMainBinding
import com.hzzt.powerful.vmodel.MainVm
import com.twinkle.commonlib.data.constants.Constant
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home_drawer_content.*
import me.goldze.mvvmhabit.base.AppManager
import me.goldze.mvvmhabit.bus.RxBus
import me.goldze.mvvmhabit.http.NetworkUtil
import me.goldze.mvvmhabit.utils.KLog
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : BaseA<ActivityMainBinding, MainVm>() {
    private var currentServerResp: CurrentServerResp? = null  //当前连接服务器
    private var countTime: Int = CacheData.downTimer  //3个小时

    //    private var halfHour: Int = (0.5 * 60 * 60).toInt()  //半小时
    private var halfHour: Int = 2400  //半小时
    private var remindDialog: RemindDownDialog? = null  //闹钟提醒
    private var isHandClick: Boolean = false  //是否手动点击
    private var isHandConnectClick: Boolean = false  //是否手动点击
    private var currentState = ""  //当前状态
    private var downTime = 10*1000L  //倒计时10秒，未连接成功，直接显示失败
    private var countDownTimer: MyCountDownTimer? = null //当前tv倒计时

    //当前速度
    companion object {
        var currentSpeedIn: String = ""
        var currentSpeedOut = ""
        var tempSpeedOut = ""
        var useDuration = ""
        var vpnStart: Boolean = false  //是否开始连接
        var reqCode: Int = 0x123
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initArgument() {
        initHandler()
        CacheData.isAgreePrivacy = true
    }

    override fun initFlow() {
        checkCacheConfig()
        initDownTimer()
        // 检查vpn是否已经运行
        isServiceRunning()
        VpnStatus.initLogCache(activity.cacheDir)
        initDrawer()
    }

    //缓存的von配置
    private fun checkCacheConfig() {
        currentServerResp = CacheData.serverConfig
        if (currentServerResp != null && !TextUtils.isEmpty(currentServerResp!!.serUrl)) {
            Glide.with(activity).load(currentServerResp?.iconUrl).centerCrop().into(iv_server_logo)
            tv_server_name.text = currentServerResp?.country
            tv_connect.visibility = View.GONE
        } else {
            tv_connect.visibility = View.VISIBLE
            tv_server_name.text = getResToStr(R.string.home_smart_server)
            iv_server_logo.setImageResource(R.drawable.app_icon)
        }
    }

    //drawer
    private fun initDrawer() {
        val toggle = ActionBarDrawerToggle(
            activity,
            drawer_layout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        //左侧菜单
        head_view.setLeftMenuListener {
            closeOrOpenDrawer()
        }
        //右侧菜单
        head_view.setConditionListener {
            startActivityForResult(Intent(activity, ServerActivity::class.java), reqCode)
        }
    }

    //抽屉
    fun closeOrOpenDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    //倒计时
    private fun initDownTimer() {
        time_view.setCountDownTime(
            if (currentServerResp == null || TextUtils.isEmpty(
                    currentServerResp?.serUrl
                )
            ) 0 else countTime
        )//设置控件的倒计时间(首次安装进入显示00:00:00)
//        if (countTime < halfHour) {
//            time_view.setTextPointColor(getResColor(R.color.red1))
//        } else {
//            time_view.setTextPointColor(getResColor(R.color.white))
//        }
        time_view.setOnCallBackListener { downTime ->  //监听倒计时
//            handler.post {
            this.countTime = downTime
//                if (downTime < halfHour) {
//                    if (remindDialog == null) {
//                        remindDialog =
//                            RemindDownDialog(activity, getResToStr(R.string.connect_down))
//                        remindDialog!!.setListener {
//                            remindDialog = null
//                        }
//                        remindDialog!!.show()
//                    }
//                    time_view.setTextPointColor(getResColor(R.color.red1))
//                } else
//
//                    if (downTime == 0) {  //结束
//                    if (remindDialog == null) {
//                        remindDialog =
//                            RemindDownDialog(activity, getResToStr(R.string.connect_down))
//                        remindDialog!!.setListener {
//                            remindDialog = null
//                        }
//                        remindDialog!!.show()
//                    }
//                } else {
//                    time_view.setTextPointColor(getResColor(R.color.white))
//                }
//            }
        }
    }

    //检测连接状态
    private fun startCheckConnectState(){
        countDownTimer = object : MyCountDownTimer(downTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                super.onTick(millisUntilFinished)
                KLog.d("----check--${millisUntilFinished/1000}")
            }
            override fun onFinish() {
                status(Constant.CONNECT_FAIL)
                stopVpn()
                KLog.d("----->failed")
                cancel()
            }
        }.start(downTime)
    }


    override fun onClickView(view: View?) {
        super.onClickView(view)
        when (view?.id) {
            R.id.layout_ss -> {
                // Vpn 正在运行，用户想断开当前连接。
                if (vpnStart) {
                    confirmDisconnect()  //断开
                } else {
                    isHandConnectClick = true
                    if (currentServerResp != null && !TextUtils.isEmpty(currentServerResp?.serUrl)) {
                        if (!activity.isFinishing) {
                            ConnectCreateDialog.getInstance(activity).show()
                            prepareVpn()
                        }
                    } else {
                        //随机一个
                        if (!activity.isFinishing) {
                            currentServerResp = AppUtil.getRandomServer(activity)
                            if (currentServerResp != null && !TextUtils.isEmpty(currentServerResp?.serUrl)) {
                                checkCacheConfig() //设置底下数据
                                ConnectCreateDialog.getInstance(activity).show()
                                prepareVpn()
                            }
                        }
                    }
                }
            }
            R.id.layout_faq -> {
                WebComActivity.getInstanceActivity(
                    activity,
                    ApiUrl.faqUrl,
                    getResToStr(R.string.menu_faq)
                )
            }
            R.id.layout_pp -> {
                WebComActivity.getInstanceActivity(
                    activity,
                    ApiUrl.privacyUrl,
                    getResToStr(R.string.privacy_text3)
                )
            }
            R.id.layout_au -> {
                startActivity(AboutActivity::class.java)
            }
            R.id.layout_exit -> {
                val exitDialog = AppExitDialog.getInstance(activity)
                exitDialog.setListener {
                    //退出结束连接
                    if (vpnStart) {
                        stopVpn()
                        time_view.stop() //结束倒计时
                        CacheData.downTimer = countTime  //保存结束倒计时
                    }
                    AppManager.getAppManager().AppExit()
                    finish()
                }
                exitDialog.show()
            }
            R.id.layout_server -> {
                startActivityForResult(Intent(activity, ServerActivity::class.java), reqCode)
            }
        }
    }

    //获取服务状态
    fun isServiceRunning() {
        setStatus(OpenVPNService.getStatus())
    }

    /**
     * 状态变化与相应的 vpn 连接状态
     */
    private fun setStatus(connectionState: String) {
        if (!TextUtils.isEmpty(connectionState)) {
            KLog.e("------state---$connectionState")
            when (connectionState) {
                "DISCONNECTED" -> {  //断开
                    vpnStart = false
                    OpenVPNService.setDefaultStatus()
                    KLog.d("-------断开")

                    status(Constant.DISCONNECT)
                }
                "CONNECTED" -> {  //已连接
                    vpnStart = true // it will use after restart this activity
                    KLog.d("-------已连接")
                    status(Constant.CONNECTED)
                }
                "WAIT" -> {   //正在连接
                    KLog.d("-------正在连接")
                    status(Constant.CONNECTING)
                }
                "AUTH" -> {   //需要认证
                    KLog.d("-------需要认证")
                    status("auth")
                }
                "RECONNECTING" -> {  //重新连接
                    KLog.d("-------重新连接")
                    status(Constant.RECONNECT)
                }
                "NONETWORK" -> {  //没有网络
                    status(Constant.NONETWORK)
                    KLog.d("-------没有网络")
                    //  showMsg(R.string.app_error_101)
                    ConnectCreateDialog.disDialog()
                }
                "USERPAUSE" -> {  //暂停
                    status(Constant.USERPAUSE)
                }
            }
        }
    }

    /**
     * VPN当前状态:
     */
    private fun status(status: String) {
        if (status == Constant.CONNECT) {
            iv_connect.setImageResource(R.drawable.bg_circle_in_normal)
            layout_ss.setBackgroundResource(R.drawable.bg_circle_gray)
        } else if (status == Constant.CONNECTING) {
            iv_connect.setImageResource(R.drawable.bg_circle_in_normal)
            layout_ss.setBackgroundResource(R.drawable.bg_circle_gray)
        } else if (status == Constant.CONNECTED) {  //已连接
            iv_connect.setImageResource(R.drawable.bg_circle_in_press)
            layout_ss.setBackgroundResource(R.drawable.bg_circle_main)
            countTime = CacheData.downTimer
            time_view.setCountDownTime(countTime)
            time_view.stop()
            time_view.start() //启动倒计时
            //连接结果
            if (ClickUtil.isConnectClick(1000) && isHandConnectClick) {
                isHandConnectClick = false
                if (!activity.isFinishing)  ConnectCreateDialog.disDialog()
                Intent(activity, ConnectStateActivity::class.java).run {
                    putExtra(Constant.CONNECTKEY, Constant.CONNECTED)
                    startActivityForResult(this, reqCode)
                }
            }
            //结束10s倒计时
            countDownTimer?.cancel()
        } else if (status == Constant.DISCONNECT) {  //断开
            iv_connect.setImageResource(R.drawable.bg_circle_in_normal)
            layout_ss.setBackgroundResource(R.drawable.bg_circle_gray)
            //连接结果
            if (ClickUtil.isConnectClick(1000) && isHandClick) {
                isHandClick = false
                Intent(activity, ConnectStateActivity::class.java).run {
                    putExtra(Constant.CONNECTKEY, Constant.DISCONNECT)
                    startActivityForResult(this, reqCode)
                }
                time_view.stop() //结束倒计时
                CacheData.downTimer = countTime  //保存结束倒计时
            } else {
                time_view.stop() //结束倒计时
                CacheData.downTimer = countTime  //保存结束倒计时
            }
            //结束10s倒计时
            countDownTimer?.cancel()
        } else if (status == Constant.NONETWORK) {  //网络问题
            iv_connect.setImageResource(R.drawable.bg_circle_in_normal)
            layout_ss.setBackgroundResource(R.drawable.bg_circle_gray)
            //连接结果
            if (ClickUtil.isFastClick() && isHandClick) {
                isHandClick = false
                Intent(activity, ConnectStateActivity::class.java).run {
                    putExtra(Constant.CONNECTKEY, Constant.DISCONNECT)
                    startActivityForResult(this, reqCode)
                }
                time_view.stop() //结束倒计时
                CacheData.downTimer = countTime  //保存结束倒计时
            //结束10s倒计时
            countDownTimer?.cancel()
            }
        } else if (status == Constant.CONNECT_FAIL) {  //失败
            Intent(activity, ConnectStateActivity::class.java).run {
                putExtra(Constant.CONNECTKEY, Constant.CONNECT_FAIL)
                startActivityForResult(this, reqCode)
            }
            //结束10s倒计时
            countDownTimer?.cancel()
            if (!activity.isFinishing) {
                ConnectCreateDialog.disDialog()
                time_view.stop() //结束倒计时
                CacheData.downTimer = countTime  //保存结束倒计时
                iv_connect.setImageResource(R.drawable.bg_circle_in_normal)
                layout_ss.setBackgroundResource(R.drawable.bg_circle_gray)
            }
        } else {
            time_view.stop() //结束倒计时
            CacheData.downTimer = countTime  //保存结束倒计时
            iv_connect.setImageResource(R.drawable.bg_circle_in_normal)
            layout_ss.setBackgroundResource(R.drawable.bg_circle_gray)
        }
    }


    /**
     * Start the VPN
     */
    private fun startVpn() {
        //开始倒计时，10s结束后未连接上则跳转失败
        startCheckConnectState()
        currentServerResp = CacheData.serverConfig
        if (currentServerResp == null || TextUtils.isEmpty(currentServerResp?.serUrl)) return
        currentServerResp?.serUrl?.let { Log.d("URL--", it) }
        Thread {
            try {
                // .ovpn file
                val conf = URL(currentServerResp?.serUrl).openStream()
                val isr = InputStreamReader(conf)
                val br = BufferedReader(isr)
                var config = ""
                var line: String?
                while (true) {
                    line = br.readLine()
                    if (line == null) break
                    config += line + "\n"
                }
                br.readLine()
                OpenVpnApi.startVpn(activity, config, currentServerResp?.country, "", "")

                // Update log
                vpnStart = true
            } catch (e: Exception) {
                handler.post {
                    showMsg(e.message)
                    ConnectCreateDialog.disDialog()
                    stopVpn()
                }
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * Stop vpn
     *
     * @return boolean: VPN status
     */
    fun stopVpn(): Boolean {
        try {
            OpenVPNThread.stop()
            vpnStart = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 断开对话框
     */
    fun confirmDisconnect() {
        if (AppUtil.isMultiClickClick(1000) && !activity.isFinishing) {
            val disconnectDialog = DisconnectDialog(activity)
            disconnectDialog.setListener {
                isHandClick = true
                tempSpeedOut = currentSpeedOut
                stopVpn()
            }
            disconnectDialog.show()
        }
    }

    /**
     * 准备使用所需权限的 VPN 连接
     */
    private fun prepareVpn() {
        if (!vpnStart) {
            // 检查网络监视器的权限
            if (NetworkUtil.isNetworkAvailable(activity)) {
                val intent = VpnService.prepare(activity)
                if (intent != null) {
                    startActivityForResult(intent, 1)
                } else startVpn()
                status(Constant.CONNECTING)
            } else {
                //没有网络的情况下也需要跳失败页面
                stopVpn()
                startCheckConnectState()
                // 没有可用的互联网连接
                showMsg(R.string.app_error_101)
            }
        } else if (stopVpn()) {
            // VPN 已停止，显示 Toast 消息。
            showMsg("Disconnect Successfully")
        }
    }


    /**
     * 接收广播消息
     */
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                useDuration = intent.getStringExtra("duration").toString()
                currentSpeedIn = intent.getStringExtra("byteIn").toString()
                currentSpeedOut = intent.getStringExtra("byteOut").toString()

                //  KLog.d("----------duration=$useDuration-----------byteIn=$currentSpeedIn------------byteOut=$currentSpeedOut")
                RxBus.getDefault().postSticky(currentSpeedOut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                var resultState = intent.getStringExtra("state")
                if (!TextUtils.isEmpty(resultState) && currentState != resultState) {
                    setStatus(resultState!!)
                    KLog.d("--???currentState=$currentState--->$resultState")
                    currentState = resultState!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    override fun onResume() {
        LocalBroadcastManager.getInstance(activity)
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        isHandClick=false
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
        CacheData.downTimer = countTime  //保存结束倒计时
        time_view.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //授权时，开始连接
        if (requestCode == 1 && resultCode == RESULT_OK) {
            startVpn()
        } else if (resultCode == RESULT_OK) {
            isHandConnectClick=true
            checkCacheConfig()
            // 停止之前的连接
            if (vpnStart) {
                stopVpn()
                time_view.stop() //结束倒计时
                CacheData.downTimer = countTime  //保存结束倒计时
            }
            if (!activity.isFinishing) {
                ConnectCreateDialog.getInstance(activity).show()
                prepareVpn()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode === KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_DOWN) {
//            if (AppUtil.doubleClickExit()) { //2000毫秒内
//                AppManager.getAppManager().AppExit()
//                finish()
//            }
            layout_exit.callOnClick()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}
