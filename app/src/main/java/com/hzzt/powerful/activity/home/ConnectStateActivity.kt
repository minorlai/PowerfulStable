package com.hzzt.powerful.activity.home

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.hzzt.common.data.cache.CacheData
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.common.entity.resp.CurrentServerResp
import com.hzzt.powerful.MainActivity
import com.hzzt.powerful.R
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivityConnectResultBinding
import com.hzzt.powerful.vmodel.MainVm
import com.twinkle.commonlib.data.constants.Constant
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_connect_result.*
import me.goldze.mvvmhabit.utils.KLog

/**
 * @author: Allen
 * @date: 2022/7/23
 * @description: 连接状态
 */
class ConnectStateActivity : BaseA<ActivityConnectResultBinding, MainVm>() {
    private var connectState: String? = null
    private var currentServerResp: CurrentServerResp? = null
    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_connect_result
    }

    override fun initArgument() {
        currentServerResp = CacheData.serverConfig
        connectState = intent.getStringExtra(Constant.CONNECTKEY)
    }

    override fun initFlow() {
        if (connectState == Constant.CONNECT_FAIL) {
            iv_connect_state.setImageResource(R.drawable.icon_connect_failed)
            tv_connect_result.text = getResToStr(R.string.connect_fail)
            layout_fail.visibility = View.VISIBLE
            layout_suc.visibility = View.GONE
            tv_vpn_speed.text = MainActivity.currentSpeedOut

            tv_server.text = getResToStr(R.string.connect_change)

        } else if (connectState == Constant.CONNECTED) {
            iv_connect_state.setImageResource(R.drawable.icon_connect_suc)
            tv_connect_result.text = getResToStr(R.string.connect_suc)
            layout_fail.visibility = View.GONE
            layout_suc.visibility = View.VISIBLE

            tv_country_vpn.setTextColor(getResColor(R.color.main_color3))
            tv_vpn_speed.setTextColor(getResColor(R.color.main_color3))
            tv_server.text = getResToStr(R.string.connect_select)

            registerBus(String::class.java, Consumer<String> {
                KLog.d("result---->>>$it")
                if (!TextUtils.isEmpty(it))
                    tv_vpn_speed.text = it
            })

        } else if (connectState == Constant.DISCONNECT) {
            iv_connect_state.setImageResource(R.drawable.icon_disconnect)
            tv_connect_result.text = getResToStr(R.string.connect_disconnect)
            layout_fail.visibility = View.GONE
            layout_suc.visibility = View.VISIBLE

            tv_vpn_speed.text = MainActivity.tempSpeedOut
            tv_country_vpn.setTextColor(getResColor(R.color.gray3))
            tv_vpn_speed.setTextColor(getResColor(R.color.gray3))

            tv_server.text = getResToStr(R.string.disconnect_to_home)
        }
        //国家
        if (currentServerResp != null && !TextUtils.isEmpty(currentServerResp?.country))
            tv_country_vpn.text = currentServerResp?.country

    }


    override fun onClickView(view: View?) {
        super.onClickView(view)
        when (view?.id) {
            R.id.tv_server -> {
                if (connectState == Constant.CONNECT_FAIL || connectState == Constant.CONNECTED) {
                    startActivity(ServerActivity::class.java)
                }
                finish()
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
}