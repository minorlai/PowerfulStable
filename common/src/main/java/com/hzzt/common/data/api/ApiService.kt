package com.hzzt.common.data.api

import com.hzzt.common.entity.resp.CommonResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author: Allen
 * @date: 2022/7/21
 * @description: 接口集
 */
interface ApiService {
    /**
     * 首页数据
     *
     * @param req
     * @return
     */
//    @POST(ApiUrl.homeList)
    fun homeListData(@Body req: Map<*, *>): Observable<CommonResponse<Any>>
}