package com.haripm.encryptionkotlinutility.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.haripm.encryptionkotlinutility.R

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sharedPrefAesEncryption(view: View) {
        val aesSharePref = Intent(applicationContext, AesSharedPrefEncryptionActivity::class.java)
        startActivity(aesSharePref)
    }

    fun realmAesEncryption(viewRealm: View) {
        val realmEncryption= Intent(applicationContext, RealmEncryptionActivity::class.java)
        startActivity(realmEncryption)
    }

    fun rsaEncryption(viewRsa: View) {
        val rsaSharedPref = Intent(applicationContext, RsaSharedPrefEncryptionActivity::class.java)
        startActivity(rsaSharedPref)
    }

    fun retrofitCertificatePinning(viewCPinning: View) {
        val retrofitCertificatePinning = Intent(applicationContext, RetrofitCertificatePinningActivity::class.java)
        startActivity(retrofitCertificatePinning)
    }

    fun certificatePinning(viewCPin: View) {
        val certificatePinning = Intent(applicationContext, CertificatePinningActivity::class.java)
        startActivity(certificatePinning)
    }
}