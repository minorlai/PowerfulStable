package com.hzzt.common.widget.head

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.hzzt.common.R
import com.hzzt.common.databinding.ViewCommonHeadBinding
import com.hzzt.common.utils.AppUtil
import kotlinx.android.synthetic.main.view_common_head.view.*

/**
 * @author: Allen
 * @date: 2022/7/18
 * @description: Head
 */
class HeaderView : LinearLayout {
    private var binding: ViewCommonHeadBinding? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
        setViewInfo(context, attrs) //title
        isShowBack(context, attrs) //back img
        isShowCondition(context, attrs) //condition img
        isShowConditionTxt(context, attrs) //condition img
    }

    private fun init(context: Context) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_common_head,
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
        backFinish()
    }

    //返回
    private fun backFinish() {
        setLeftBackBtn { v: View ->
            val act = v.context
            if (act is Activity) {
                act.finish()
            } else {
                v.isClickable = false
            }
        }
    }

    fun setToolbarBg(bgColor: Int) {
        toolbar.setBackgroundColor(bgColor)
    }

    /**
     * 设置返回按钮监听
     *
     * @param listener
     */
    fun setLeftBackBtn(listener: OnClickListener?) {
        layout_back.setOnClickListener(listener)
    }

    /**
     * 条件筛选点击事件
     *
     * @param listener
     */
    fun setConditionListener(listener: OnClickListener?) {
        iv_condition.setOnClickListener(listener)
    }

    /**
     * 条件筛选点击事件
     *
     * @param listener
     */
    fun setConditionTxtListener(listener: OnClickListener?) {
        tv_condition.setOnClickListener(listener)
    }

    /**
     * 方法名：  setViewInfo	<br></br>
     * 方法描述：设置自定义style<br></br>
     *
     * @param context
     * @param attrs
     */
    private fun setViewInfo(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HeadView)
        val title = ta.getString(R.styleable.HeadView_titleName)
        val titleColor = ta.getColor(
            R.styleable.HeadView_titleColor,
            ContextCompat.getColor(context, R.color.white)
        )
        val bgColor = ta.getColor(
            R.styleable.HeadView_bgColor,
            ContextCompat.getColor(context, R.color.common_trans)
        )
        setTitle(title)  //title
        setTitleColor(titleColor)
        setToolbarBg(bgColor)   //背景色
        ta.recycle()
    }

    /**
     * 条件img
     *
     * @param context
     * @param attrs
     */
    private fun isShowCondition(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HeadView)
        val img = ta.getResourceId(R.styleable.HeadView_conditionImg, R.drawable.icon_online_green)
        val isShowImg = ta.getBoolean(R.styleable.HeadView_isConditionImg, false)
        iv_condition.visibility = if (isShowImg) VISIBLE else GONE
        setConditionImg(img)
        ta.recycle()
    }

    /**
     * 条件text
     *
     * @param context
     * @param attrs
     */
    private fun isShowConditionTxt(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HeadView)
        val text: String? = ta.getString(R.styleable.HeadView_conditionText)
        val conditionColor = ta.getColor(
            R.styleable.HeadView_conditionTextColor,
            ContextCompat.getColor(context, R.color.white)
        )
        val isShowText = ta.getBoolean(R.styleable.HeadView_isCondition, false)
        tv_condition.visibility = if (isShowText) VISIBLE else GONE
        text?.let { setConditionTxt(it) }
        setConditionColor(conditionColor)
        ta.recycle()
    }

    /**
     * 返回Img
     *
     * @param context
     * @param attrs
     */
    private fun isShowBack(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HeadView)
        val backImg =
            ta.getResourceId(R.styleable.HeadView_backImg, R.drawable.icon_white_back)
        val isBack = ta.getBoolean(R.styleable.HeadView_isBack, true)
        layout_back.visibility = if (isBack) VISIBLE else GONE
        setBackImg(backImg)
        ta.recycle()
    }

    //设置返回图标
    private fun setBackImg(resource: Int) {
        iv_back.setImageResource(resource)
    }

    fun showHideImg(isShow: Boolean){
        layout_back.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    //设置条件img
    fun setConditionImg(resImg: Int) {
        iv_condition.setImageResource(resImg)
    }

    //设置条件text
    fun setConditionTxt(resTxt: String) {
        tv_condition.text = resTxt
    }

    //隐藏/展示条件
    fun showHideBtnImg(isShow: Boolean) {
        iv_condition.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    //隐藏/展示条件
    fun showHideTxt(isShow: Boolean) {
        tv_condition.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    /**
     * 设置标题文字内容
     *
     * @param title
     */
    fun setTitle(title: String?) {
        tv_title.text = title
    }

    fun setTitleColor(color: Int?) {
        color?.let { tv_title.setTextColor(it) }
    }

    fun setConditionColor(color: Int?) {
        color?.let { tv_condition.setTextColor(it) }
    }
}

