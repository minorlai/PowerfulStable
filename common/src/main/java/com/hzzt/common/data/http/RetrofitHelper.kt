package  com.hzzt.common.data.http

import android.text.TextUtils
import android.util.Log.INFO
import com.hzzt.common.BuildConfig
import com.hzzt.common.data.api.ApiService
import com.hzzt.common.data.api.ApiUrl
import com.hzzt.common.data.cache.Preference
import com.hzzt.common.utils.MainUtil
import com.twinkle.commonlib.data.constants.Constant
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.goldze.mvvmhabit.http.interceptor.logging.Level
import me.goldze.mvvmhabit.http.interceptor.logging.LoggingInterceptor
import me.goldze.mvvmhabit.utils.KLog
import me.goldze.mvvmhabit.utils.Utils
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    const val MAX_CACHE_SIZE: Long = 1024 * 1024 * 50 // 50M 的缓存大小

    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null
    private val httpCacheDirectory: File? = null
    private var cache: Cache? = null

    fun service(): ApiService {
        synchronized(RetrofitHelper::class.java) {
            if (apiService != null && retrofit != null) {
                return apiService as ApiService
            }
            retrofit = null
            apiService = null
            apiService = getRetrofit()!!.create(ApiService::class.java) as ApiService
            return apiService as ApiService
        }
    }

    fun reset() {
        apiService = null
        retrofit = null
    }

    private fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            synchronized(RetrofitHelper::class.java) {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(ApiUrl.baseUrl)
                        .client(getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                }
            }
        }
        return retrofit
    }

    /**
     * 获取 OkHttpClient
     */
    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        //设置 请求的缓存位置和大小
        try {
            if (cache == null) {
                cache = httpCacheDirectory?.let { Cache(it, MAX_CACHE_SIZE) }
            }
        } catch (e: Exception) {
            KLog.e("Could not create http cache", e)
        }

        builder.run {
            addInterceptor(
                LoggingInterceptor.Builder() //构建者模式
                    .loggable(BuildConfig.DEBUG) //是否开启日志打印
                    .setLevel(Level.BASIC) //打印的等级
                    .log(INFO) // 打印类型
                    .request("Request") // request的Tag
                    .response("Response") // Response的Tag
                    .addHeader("Header", "I am the request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                    .build()
            )
            addInterceptor(headInterceptor())
                .addInterceptor(CacheInterceptor(Utils.getContext()))
                .cache(cache)
                .connectTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//错误重连
                .connectionPool(ConnectionPool(8, 5, TimeUnit.MILLISECONDS))
            //如果线程数少，连接池大了会造成阻塞
            // 这里8个，和每个保持时间为5min
        }
        return builder.build()
    }

    //头部拦截器
    private fun headInterceptor(): Interceptor? {
        return Interceptor { chain: Interceptor.Chain ->
            val builder = chain.request().newBuilder()
            var token: String by Preference(Constant.USER_TOKEN, "")
            if (!TextUtils.isEmpty(token)) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            builder.addHeader("client_id", "yuany")
                .addHeader("client_secret", "yuany_password")
                .build()
            chain.proceed(builder.build())
        }
    }

    fun <T> execute(observable: Observable<T>, subscriber: Observer<T>?): T? {
        observable.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscriber!!)
        return null
    }
}