package com.hzzt.common.widget.head

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.hzzt.common.R
import com.hzzt.common.databinding.ViewMainHeadBinding
import com.hzzt.common.utils.AppUtil
import kotlinx.android.synthetic.main.view_main_head.view.*

/**
 * @author: Allen
 * @date: 2022/7/18
 * @description: Head
 */
class HeaderMainView : LinearLayout {
    private var binding: ViewMainHeadBinding? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_main_head,
            null,
            false
        )
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        binding!!.root.layoutParams = params
        addView(binding!!.root)
        //toolbar 沉浸式设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(0, AppUtil.getStatusBarHeight(context), 0, 0)
        }
    }


    fun setToolbarBg(bgColor: Int) {
        toolbar.setBackgroundColor(bgColor)
    }

    fun getToolbar(): Toolbar? {
        return binding?.toolbar
    }

    /**
     * 设置返回按钮监听
     *
     * @param listener
     */
    fun setLeftMenuListener(listener: OnClickListener?) {
        layout_menu.setOnClickListener(listener)
    }

    /**
     * 条件筛选点击事件
     *
     * @param listener
     */
    fun setConditionListener(listener: OnClickListener?) {
        layout_condition.setOnClickListener(listener)
    }

}

