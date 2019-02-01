package com.haripm.encryptionkotlinutility.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import java.security.spec.AlgorithmParameterSpec
import java.util.Calendar
import java.util.GregorianCalendar

import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

class SharedPrefsRSAManager constructor(context: Context, secureKey: String) {

    private var mContext: Context = context
    private val KEYSTORE = "AndroidKeyStore"
    private val ALIAS = "MY_APP"
    private val TYPE_RSA = "RSA"
    private val CYPHER = "RSA/ECB/PKCS1Padding"
    private val CHARSET = "UTF-8"
    private val TAG = "SharedPrefsRSAManager"
    private var prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(
            SharedPrefsRSAManager.SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    companion object {
        const val SHARED_PREFS = "sharedpref"
        private var instance: SharedPrefsRSAManager? = null

        fun getInstance(context: Context, secureKey: String): SharedPrefsRSAManager {
            if (instance == null) {
                instance = SharedPrefsRSAManager(context, secureKey)
            }
            return instance as SharedPrefsRSAManager
        }
    }

    fun putString(key: String, value: String?) {
        if (value.equals(null) || value!!.isEmpty()) {
            prefs.edit().putString(key, null).apply()
        } else {
            try {
                val encryptedKey = encryptKey(mContext, key)
                if (encryptedKey != null) {
                    StringUtils.writeToFile(encryptedKey, mContext)
                    Log.d(TAG, "key stored in file is : $encryptedKey")
                }
                prefs.edit().putString(key, value).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun putBoolean(key: String, value: Boolean) {
            try {
                val encryptedKey = encryptKey(mContext, key)
                if (encryptedKey != null) {
                    StringUtils.writeToFile(encryptedKey, mContext)
                    Log.d(TAG, "key stored in file is : $encryptedKey")
                }
                prefs.edit().putBoolean(key, value).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    fun putLong(key: String, value: Long) {
            try {
                val encryptedKey = encryptKey(mContext, key)
                if (encryptedKey != null) {
                    StringUtils.writeToFile(encryptedKey, mContext)
                    Log.d(TAG, "key stored in file is : $encryptedKey")
                }
                prefs.edit().putLong(key, value).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    fun putInt(key: String, value: Int) {
            try {
                val encryptedKey = encryptKey(mContext, key)
                if (encryptedKey != null) {
                    StringUtils.writeToFile(encryptedKey, mContext)
                    Log.d(TAG, "key stored in file is : $encryptedKey")
                }
                prefs.edit().putInt(key, value).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    fun getString(): String? {
        val keyFromFile = StringUtils.readFromFile(mContext)
        Log.d(TAG, "Key fetched from file is : $keyFromFile")
        val decryptedKey = decryptKey(mContext, keyFromFile)
        return prefs.getString(decryptedKey, "")
    }

    fun getBoolean(): Boolean? {
        val keyFromFile = StringUtils.readFromFile(mContext)
        Log.d(TAG, "Key fetched from file is : $keyFromFile")
        val decryptedKey = decryptKey(mContext, keyFromFile)
        return prefs.getBoolean(decryptedKey, false)
    }

    fun getLong(): Long? {
        val keyFromFile = StringUtils.readFromFile(mContext)
        Log.d(TAG, "Key fetched from file is : $keyFromFile")
        val decryptedKey = decryptKey(mContext, keyFromFile)
        return prefs.getLong(decryptedKey, 0)
    }

    fun getInt(): Int? {
        val keyFromFile = StringUtils.readFromFile(mContext)
        Log.d(TAG, "Key fetched from file is : $keyFromFile")
        val decryptedKey = decryptKey(mContext, keyFromFile)
        return prefs.getInt(decryptedKey, 0)
    }

    private fun encryptKey(context: Context, stringToEncrypt: String): String? {
        try {
            val privateKeyEntry = getPrivateKey(context)
            if (privateKeyEntry != null) {
                val publicKey = privateKeyEntry.certificate.publicKey
                // Encrypt the text
                val cipherInput = Cipher.getInstance(CYPHER)
                cipherInput.init(Cipher.ENCRYPT_MODE, publicKey)

                val outputStream = ByteArrayOutputStream()
                val cipherOutputStream = CipherOutputStream(outputStream, cipherInput)
                cipherOutputStream.write(stringToEncrypt.toByteArray(charset(CHARSET)))
                cipherOutputStream.close()

                val values = outputStream.toByteArray()
                val encryptedString: String = Base64.encodeToString(values, Base64.DEFAULT)
                return encryptedString
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            return null
        }

        return null
    }

    private fun decryptKey(context: Context, encrypted: String?): String? {
        try {
            val privateKeyEntry = getPrivateKey(context)
            if (privateKeyEntry != null) {
                val privateKey = privateKeyEntry.privateKey

                val cipherOutput = Cipher.getInstance(CYPHER)
                cipherOutput.init(Cipher.DECRYPT_MODE, privateKey)
                // decryptData the bytes using a CipherInputStream
                val cipherInputStream =
                    CipherInputStream(ByteArrayInputStream(Base64.decode(encrypted, Base64.DEFAULT)), cipherOutput)
                val output = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                while (true) {
                    val nextByte = cipherInputStream.read(buffer, 0, buffer.size - 1)
                    if (nextByte <= 0) {
                        break
                    }
                    output.write(buffer, 0, nextByte)
                }
                val decryptedFinalString: String? = String(output.toByteArray(), charset(CHARSET))
                Log.d("CHECK_ME ", "final decrypted key $decryptedFinalString")
                return decryptedFinalString
            }

        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            return null
        }

        return null
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class,
        UnrecoverableEntryException::class
    )
    private fun getPrivateKey(context: Context): KeyStore.PrivateKeyEntry? {

        var ks = KeyStore.getInstance(KEYSTORE)
        ks.load(null)

        // Load the key pair from the Android Key Store
        var entry: KeyStore.Entry? = ks.getEntry(ALIAS, null)

        /* If the entry is null, keys were never stored under this alias.
         */
        if (entry == null) {
            Log.w(TAG, "No key found under alias: $ALIAS")
            Log.w(TAG, "Generating new key...")
            try {
                createKeys(context)

                // reload keystore
                ks = KeyStore.getInstance(KEYSTORE)
                ks.load(null)

                // reload key pair
                entry = ks.getEntry(ALIAS, null)

                if (entry == null) {
                    Log.w(TAG, "Generating new key failed...")
                    return null
                }
            } catch (e: NoSuchProviderException) {
                Log.w(TAG, "Generating new key failed...")
                e.printStackTrace()
                return null
            } catch (e: InvalidAlgorithmParameterException) {
                Log.w(TAG, "Generating new key failed...")
                e.printStackTrace()
                return null
            }
        }

        /* If entry is not a KeyStore.PrivateKeyEntry, it might have gotten stored in a previous
         * iteration of your application that was using some other mechanism, or been overwritten
         * by something else using the same keystore with the same alias.
         * You can determine the type using entry.getClass() and debug from there.
         */
        if (entry !is KeyStore.PrivateKeyEntry) {
            Log.w(TAG, "Not an instance of a PrivateKeyEntry")
            return null
        }

        return entry
    }

    /**
     * Creates a public and private key and stores it using the Android Key Store, so that only
     * this application will be able to access the keys.
     */
    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun createKeys(context: Context) {
        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        val start = GregorianCalendar()
        val end = GregorianCalendar()
        end.add(Calendar.YEAR, 25)

        // The KeyGenParameterSpec object is how parameters for your key pair are passed
        // to the KeyPairGenerator.
        val spec: KeyPairGeneratorSpec =
            KeyPairGeneratorSpec.Builder(context)
                // You'll use the alias later to retrieve the key.  It's a key for the key!
                .setAlias(ALIAS)
                .setKeySize(1024)
                // The subject used for the self-signed certificate of the generated pair
                .setSubject(X500Principal("CN=" + ALIAS))
                // The serial number used for the self-signed certificate of the generated pair.
                .setSerialNumber(BigInteger.valueOf(1337))
                // Date range of validity for the generated pair.
                .setStartDate(start.time)
                .setEndDate(end.time)
                .setEncryptionRequired()
                .build()

        // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
        // and the KeyStore.  This example uses the AndroidKeyStore.
        val kpGenerator = KeyPairGenerator.getInstance(TYPE_RSA, KEYSTORE)
        kpGenerator.initialize(spec)

        val kp = kpGenerator.generateKeyPair()
        Log.d(TAG, "Public Key is: " + kp.public.toString())
    }

}