package com.hzzt.powerful.activity.home

import android.os.Bundle
import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.hzzt.common.data.cache.CacheData
import com.hzzt.common.dialog.ConnectHintDialog
import com.hzzt.common.entity.req.CommonUI
import com.hzzt.common.entity.resp.CommonResponse
import com.hzzt.common.entity.resp.CurrentServerResp
import com.hzzt.common.entity.resp.ServerResp
import com.hzzt.common.utils.AppUtil
import com.hzzt.powerful.MainActivity
import com.hzzt.powerful.R
import com.hzzt.powerful.adapter.ServerAdapter
import com.hzzt.powerful.app.App
import com.hzzt.powerful.base.BaseA
import com.hzzt.powerful.databinding.ActivityServerBinding
import com.hzzt.powerful.vmodel.MainVm
import de.blinkt.openvpn.core.OpenVPNService
import kotlinx.android.synthetic.main.activity_server.*
import java.lang.reflect.Type


/**
 * @author: Allen
 * @date: 2022/7/23
 * @description:
 */
class ServerActivity : BaseA<ActivityServerBinding, MainVm>() {
    private var serversList = mutableListOf<ServerResp>()
    private var serverAdapter: ServerAdapter? = null
    private var serverResp:ServerResp?=null
    private var serverChild:ServerResp.ServerDTO?=null
    private  var cacheResp:CurrentServerResp?=null
    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_server
    }

    override fun initArgument() {
        val listType: Type = object : TypeToken<MutableList<ServerResp?>?>() {}.type
        var jsonData = AppUtil.getAssetsJson(activity, "server_config.json")
        serversList = App.Gson.fromJson(jsonData, listType)
        cacheResp=CacheData.serverConfig
    }

    override fun initFlow() {
        initServerAdapter()
    }

    //服务器列表
    private fun initServerAdapter() {

        serverAdapter = ServerAdapter(serversList)
        if(cacheResp!=null&&!TextUtils.isEmpty(cacheResp?.country))
            serverAdapter!!.selectCountry= cacheResp?.country!!
        server_recycler.adapter=serverAdapter

        //单击事件
        serverAdapter!!.setOnItemClickListener { adapter, view, position ->
            if(serversList.size>position){
                //如果已经连接，则判断选中的节点是否为已经连接的节点
                if(cacheResp!=null&&cacheResp?.country.equals(serversList[position].country)&&MainActivity.vpnStart){
                    finish()
                }else{
                    if(MainActivity.vpnStart){  //如果正在连接
                        val reconnect= ConnectHintDialog(activity)
                        reconnect.setListener {
                            setServerData(position)
                        }
                        reconnect.show()
                    }else{
                        setServerData(position)
                    }
                }
            }
        }
    }

    //设置服务器
    private fun setServerData(position:Int){
        serverResp=serversList[position]
        serverChild= serverResp!!.server[AppUtil.getRandNum(serverResp!!.server.size)]
        setResult(RESULT_OK)
        finish()
    }

    override fun finish() {
        if(serverResp!=null&&serverChild!=null){
            val currentConfig=CurrentServerResp(serverResp!!.country,serverResp!!.iconUrl,
                serverChild!!.serUrl, serverChild!!.key, serverChild!!.weight)
            CacheData.serverConfig=currentConfig  //存储当前配置
        }
        super.finish()
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