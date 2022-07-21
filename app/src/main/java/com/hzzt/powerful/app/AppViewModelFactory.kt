package com.twinkle.chat.app

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hzzt.common.data.http.HttpDataSourceImpl
import java.lang.reflect.Constructor

/**
 * @author: Allen
 * @date: 2022/7/21
 * @description: viewModel工厂
 */
class AppViewModelFactory private constructor(private var mApplication: Application, private var mRepository: HttpDataSourceImpl) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        try {
            var constructor1: Constructor<*> = modelClass.getDeclaredConstructor(Application::class.java, HttpDataSourceImpl::class.java)
            //            constructor1.setAccessible(true);
            return constructor1.newInstance(mApplication, mRepository) as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
         var INSTANCE: AppViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): AppViewModelFactory? {
            if (INSTANCE == null) {
                synchronized(AppViewModelFactory::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AppViewModelFactory(application, Injection.provideDemoRepository())
                    }
                }
            }
            return INSTANCE
        }
    }
}