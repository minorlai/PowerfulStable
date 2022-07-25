package com.hzzt.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.hzzt.common.R;
import com.hzzt.common.databinding.DialogExitBinding;
import com.hzzt.common.databinding.DialogRemindDownBinding;


/**
 * @author: Allen
 * @date: 2022/7/22
 * @description: 倒计时提醒对话框
 */
public class RemindDownDialog extends Dialog {
    private DialogRemindDownBinding binding;
    private static RemindDownDialog dialog;
    private ViewItemClickListener listener;
    private Context context;
    private String remindHint;

//    public static RemindDownDialog getInstance(Context context) {
//        if (dialog == null) {
//            synchronized (RemindDownDialog.class) {
//                if (dialog == null) {
//                    dialog = new RemindDownDialog(context);
//                }
//            }
//        }
//        return dialog;
//    }

    public RemindDownDialog(Context context,String remindHint) {
        super(context, R.style.MyDialog2);
        this.remindHint=remindHint;
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_remind_down, null, false);
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (screenWidth * 0.85);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
        getWindow().setWindowAnimations(R.style.anim_style);// 设置动画

        if(!TextUtils.isEmpty(remindHint)){
            binding.tvHintContent.setText(Html.fromHtml(remindHint));
        }

//        binding.animClock.setAnimation("data.json");
//        binding.animClock.playAnimation();

        binding.tvYes.setOnClickListener(v -> {
            if (listener != null)
                listener.clickView();
            dismiss();
        });
        binding.tvNo.setOnClickListener(v -> dismiss());
    }

    public void setListener(ViewItemClickListener listener) {
        this.listener = listener;
    }

    public interface ViewItemClickListener {
        void clickView();
    }
}
