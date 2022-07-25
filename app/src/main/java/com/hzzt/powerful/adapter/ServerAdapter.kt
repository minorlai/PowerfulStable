package com.hzzt.powerful.adapter

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hzzt.common.entity.resp.ServerResp
import com.hzzt.powerful.R

/**
 * @Author: Allen
 * @CreateDate: 2022/7/23
 * @Description: 服务器列表
 */
class ServerAdapter(data: MutableList<ServerResp>) :
    BaseQuickAdapter<ServerResp, BaseViewHolder>(R.layout.recycler_server_item, data) {
    var selectCountry=""
    override fun convert(holder: BaseViewHolder, item: ServerResp) {
        //icon
        Glide.with(context).load(item.iconUrl).centerCrop().into(holder.getView(R.id.iv_server_logo))
        //country
        holder.setText(R.id.tv_server_country,item.country)

        //选中
        if(item.country.equals(selectCountry)){
            holder.setTextColor(R.id.tv_server_country,ContextCompat.getColor(context,R.color.orange))
            holder.setImageResource(R.id.icon_select,R.drawable.icon_select_press)
            holder.setGone(R.id.icon_yes,false)
            holder.setBackgroundResource(R.id.layout_item,R.drawable.bg_rect_main_6dp)
        }else{
            holder.setTextColor(R.id.tv_server_country,ContextCompat.getColor(context,R.color.white))
            holder.setImageResource(R.id.icon_select,R.drawable.icon_select_normal)
            holder.setGone(R.id.icon_yes,true)
            holder.setBackgroundResource(R.id.layout_item,R.drawable.bg_rect_black_6dp)
        }
    }
}