package com.hzzt.common.interfaces;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;

/**
 * @Author: Allen
 * @CreateDate: 2022/7/21
 * @Description: 适配器回调
 */
public interface RvAdapterConvert<T> {
    void convert(BaseViewHolder viewHolder, T item);

}
