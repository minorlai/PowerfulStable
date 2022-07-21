package com.hzzt.common.data.cache

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonParser
import me.goldze.mvvmhabit.utils.Utils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.reflect.KProperty

class Preference<T>(val name: String, private val default: T) {
    companion object {
        private val file_name = "twinkle_chat_file"

        private val prefs : SharedPreferences by lazy {
            Utils.getContext().getSharedPreferences(file_name, Context.MODE_PRIVATE)
        }

        /**
         * 删除全部数据
         */
        fun clearPreference(){
            prefs.edit().clear().apply()
        }

        /**
         * 根据key删除存储数据
         */
        fun clearPreference(key: String){
            prefs.edit().remove(key).apply()
        }

        /**
         * 查询某个key是否已经存在
         */
        fun contains(key: String):Boolean{
            return prefs.contains(key)
        }

        fun getAll() : Map<String, *>{
            return prefs.all
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) : T{
        return getSharedPreferences(name, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T){
        putSharedPreferences(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun putSharedPreferences(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }

    private fun getSharedPreferences(name: String, default: T): T = with(prefs) {
        val res:Any = when(default){
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(getString(name, serialize(default)).toString())
        }!!
        return res as T
    }

    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }


    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1"))
        )
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream
        )
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }


    /**
     * 保存List
     *
     * @param context
     * @param key
     * @param datalist
     * @param <T>
    </T> */
    fun <T> saveDataList(key: String?, datalist: List<T>?) {
        if (null == datalist || datalist.isEmpty()) {
            return
        }
        val edit = prefs.edit() // 获取编辑器
        val gson = Gson()
        //转换成json数据，再保存
        val strJson = gson.toJson(datalist)
        edit.clear()
        edit.putString(key, strJson)
        edit.commit()
    }

    /**
     * 获取List
     *
     * @param key
     * @return
     */
    fun <T> getDataList(key: String?, tClass: Class<T>?): List<T>? {
        val datalist: MutableList<T> = ArrayList()
        val strJson = prefs.getString(key, null) ?: return datalist
        val array = JsonParser().parse(strJson).asJsonArray
        for (elem in array) {
            datalist.add(Gson().fromJson(elem, tClass))
        }
        return datalist
    }

}