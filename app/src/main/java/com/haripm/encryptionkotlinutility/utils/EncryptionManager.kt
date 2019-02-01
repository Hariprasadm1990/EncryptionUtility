package com.haripm.encryptionkotlinutility.utils

import android.content.Context

class EncryptionManager constructor(var context: Context){

    private var mContext: Context = context
    private lateinit var mKey: String
    private lateinit var preferencesAesManager: SharedPrefsAESManager
    private lateinit var preferencesRsaManager: SharedPrefsRSAManager

    fun putValue(key: String, value: String) {
       mKey = StringUtils.generateSafeToken()
       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
           //for mashmellow and above sdk versions
           preferencesAesManager = SharedPrefsAESManager.getInstance(mContext, key)
           preferencesAesManager.putString(key, value)
       } else{
           preferencesRsaManager = SharedPrefsRSAManager.getInstance(mContext, key)
           preferencesRsaManager.putString(key, value)
       }
   }

    fun getValue(key: String):String? {
        val resultValue: String?
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            // Do something for mashmellow and above versions
            preferencesAesManager = SharedPrefsAESManager.getInstance(mContext, key)
            resultValue = preferencesAesManager.getString()
        } else{
            preferencesRsaManager = SharedPrefsRSAManager.getInstance(mContext, key)
            resultValue = preferencesRsaManager.getString()
        }
        return resultValue
    }
}