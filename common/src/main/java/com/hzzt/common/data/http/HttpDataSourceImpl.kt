package com.hzzt.common.data.http

import com.hzzt.common.data.api.ApiService
import com.hzzt.common.entity.resp.CommonResponse
import io.reactivex.Observable
import me.goldze.mvvmhabit.base.BaseModel

/**
 * @author: Allen
 * @date: 2022/7/21
 * @description: 接口实现
 */
class HttpDataSourceImpl private constructor(private val apiService: ApiService) : BaseModel(),ApiService {
    companion object {
        @Volatile
        var INSTANCE: HttpDataSourceImpl? = null
        fun getInstance(apiService: ApiService): HttpDataSourceImpl {
            if (INSTANCE == null) {
                synchronized(HttpDataSourceImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = HttpDataSourceImpl(apiService)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun homeListData(req: Map<*, *>): Observable<CommonResponse<Any>> {
        return apiService.homeListData(req)
    }
}