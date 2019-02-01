package com.haripm.encryptionkotlinutility.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log

import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.MessageDigest

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SharedPrefsAESManager
constructor(
    context: Context,
    secureKey: String, encryptKeys: Boolean
) {

    private var mContext: Context
    private val encryptKeys: Boolean
    private val cipherWriter: Cipher
    private val cipherReader: Cipher
    private val cipherKeyWriter: Cipher
    private val preferences: SharedPreferences
    private val TAG: String = "SharedPrefsAESManager"

    class SecurePreferencesException(e: Throwable) : RuntimeException(e)

    init {
        try {
            this.mContext = context
            this.cipherWriter = Cipher.getInstance(TRANSFORMATION) // requires ivSpec
            this.cipherReader = Cipher.getInstance(TRANSFORMATION) //requires ivSpec
            this.cipherKeyWriter = Cipher.getInstance(KEY_TRANSFORMATION)

            val ivSpec = iv
            val secretKey = getSecretKey(secureKey)
            cipherWriter.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            cipherReader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            cipherKeyWriter.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

            this.preferences = context.getSharedPreferences(
                SHARED_PREFS,
                Context.MODE_PRIVATE
            )

            this.encryptKeys = encryptKeys
        } catch (e: GeneralSecurityException) {
            throw SecurePreferencesException(e)
        } catch (e: UnsupportedEncodingException) {
            throw SecurePreferencesException(e)
        }
    }

    companion object {

        private var instance: SharedPrefsAESManager? = null
        const val SHARED_PREFS = "sharedpref"
        const val CHARSET: String = "UTF-8"
        const val SECRET_KEY_HASH_TRANSFORMATION = "SHA-256"
        const val TRANSFORMATION: String = "AES/CBC/PKCS5Padding" //requires ivSpec
        const val KEY_TRANSFORMATION: String = "AES/CBC/PKCS5Padding"

        fun getInstance(context: Context, secureKey: String): SharedPrefsAESManager {
            if (instance == null) {
                instance = SharedPrefsAESManager(
                    context,
                    secureKey, true
                )
            }
            return instance as SharedPrefsAESManager
        }
    }

    private val iv: IvParameterSpec
        get() {
            val iv = ByteArray(cipherWriter.blockSize)
            System.arraycopy(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".toByteArray(), 0,
                iv, 0, cipherWriter.blockSize
            )
            return IvParameterSpec(iv)
        }

    private fun getSecretKey(key: String): SecretKeySpec {
        val keyBytes = createKeyBytes(key)
        return SecretKeySpec(keyBytes, TRANSFORMATION)
    }

    private fun createKeyBytes(key: String): ByteArray {
        val md = MessageDigest
            .getInstance(SECRET_KEY_HASH_TRANSFORMATION)
        md.reset()
        return md.digest(key.toByteArray(charset(CHARSET)))
    }

    fun putString(key: String, value: String?) {
        if (value == null) {
            preferences.edit().remove(encryptKey(key)).apply()
        } else {
            val encryptedKey: String = encryptKey(key)
            Log.d(TAG, "encrypted secure key is : $encryptedKey")
            //Copy encrypted key into a file
            StringUtils.writeToFile(encryptedKey, mContext)
            putValue(key, value)
        }
    }

    fun putBoolean(key: String, value: Boolean?) {
        if (value == null) {
            preferences.edit().remove(encryptKey(key)).apply()
        } else {
            val encryptedKey: String = encryptKey(key)
            Log.d(TAG, "encrypted secure key is : $encryptedKey")
            //Copy encrypted key into a file
            StringUtils.writeToFile(encryptedKey, mContext)
            putValue(key, java.lang.Boolean.toString(value))
        }
    }

    fun putLong(key: String, value: Long) {
        val encryptedKey: String = encryptKey(key)
        Log.d(TAG, "encrypted secure key is : $encryptedKey")
        //Copy encrypted key into a file
        StringUtils.writeToFile(encryptedKey, mContext)
        putValue(key, java.lang.Long.toString(value))
    }

    fun putInt(key: String, value: Int) {
        val encryptedKey: String = encryptKey(key)
        Log.d(TAG, "encrypted secure key is : $encryptedKey")
        //Copy encrypted key into a file
        StringUtils.writeToFile(encryptedKey, mContext)
        putValue(key, Integer.toString(value))
    }

    private fun putValue(key: String, value: String) {
        val secureValueEncoded = encryptData(value, cipherWriter)
        Log.d(TAG, "encrypted secure value is : $secureValueEncoded")
        preferences.edit().putString(key, secureValueEncoded).apply()
    }

    fun getString(): String {
        //Fetching encrypted key from file and decrypting the key
        val decryptKey = decryptData(StringUtils.readFromFile(mContext))

        //get stored  encrypted value using key
        val securedEncodedValue = preferences.getString(decryptKey, "")
        Log.d(TAG, "secure encoded value retrieved from key is : $securedEncodedValue")

        //decrypt the value and return
        return decryptData(securedEncodedValue)
    }

    fun getLong(): Long {
        //Fetching encrypted key from file and decrypting the key
        val decryptKey = decryptData(StringUtils.readFromFile(mContext))

        //get stored  encrypted value using key
        val securedEncodedValue = preferences.getString(decryptKey, "")
        Log.d(TAG, "secure encoded value retrieved from key is : $securedEncodedValue")

        //decrypt the value and return
        return java.lang.Long.parseLong(decryptData(securedEncodedValue))
    }

    fun getBoolean(): Boolean {
        //Fetching encrypted key from file and decrypting the key
        val decryptKey = decryptData(StringUtils.readFromFile(mContext))

        //get stored  encrypted value using key
        val securedEncodedValue = preferences.getString(decryptKey, "")
        val securedEncodedValues = preferences.getBoolean(decryptKey, false)
        Log.d(TAG, "secure encoded value retrieved from key is : $securedEncodedValue")

        //decrypt the value and return
        return java.lang.Boolean.parseBoolean(decryptData(securedEncodedValue))
    }

    fun getInt(): Int {
        //Fetching encrypted key from file and decrypting the key
        val decryptKey = decryptData(StringUtils.readFromFile(mContext))

        //get stored  encrypted value using key
        val securedEncodedValue = preferences.getString(decryptKey, "")
        Log.d(TAG, "secure encoded value retrieved from key is : $securedEncodedValue")

        //decrypt the value and return
        return Integer.parseInt(decryptData(securedEncodedValue))
    }

    private fun encryptData(value: String, writer: Cipher): String {
        val secureValue: ByteArray
        try {
            secureValue = convert(writer, value.toByteArray(charset(CHARSET)))
        } catch (e: UnsupportedEncodingException) {
            throw SecurePreferencesException(e)
        }
        return Base64.encodeToString(
            secureValue,
            Base64.NO_WRAP
        )
    }

    private fun decryptData(securedEncodedValue: String?): String {
        val securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP)
        val value = convert(cipherReader, securedValue)
        try {
            return String(value, charset(CHARSET))
        } catch (e: UnsupportedEncodingException) {
            throw SecurePreferencesException(e)
        }
    }

    private fun convert(cipher: Cipher, byteArray: ByteArray): ByteArray {
        try {
            return cipher.doFinal(byteArray)
        } catch (e: Exception) {
            throw SecurePreferencesException(e)
        }
    }

    private fun encryptKey(key: String): String {
        return if (encryptKeys)
            encryptData(key, cipherKeyWriter)
        else
            key
    }

    fun containsKey(key: String): Boolean {
        return preferences.contains(encryptKey(key))
    }
}