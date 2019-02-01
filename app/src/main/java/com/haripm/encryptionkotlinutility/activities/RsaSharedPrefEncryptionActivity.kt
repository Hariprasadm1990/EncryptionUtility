package com.haripm.encryptionkotlinutility.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.haripm.encryptionkotlinutility.R
import com.haripm.encryptionkotlinutility.utils.SharedPrefsRSAManager
import com.haripm.encryptionkotlinutility.utils.StringUtils

class RsaSharedPrefEncryptionActivity : AppCompatActivity() {

    private lateinit var preferencesManager: SharedPrefsRSAManager
    private lateinit var mContent: EditText
    private lateinit var mKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_prefs)

        mContent = findViewById<EditText>(R.id.content_text)
    }

    fun encryptData(view: View) {
        val valueEntered = mContent.text.toString()
        mKey = StringUtils.generateSafeToken()
        preferencesManager = SharedPrefsRSAManager.getInstance(this, mKey)
        preferencesManager.putString(mKey, valueEntered)
        Toast.makeText(this@RsaSharedPrefEncryptionActivity, "Data encrypted and stored in shared preferences", Toast.LENGTH_LONG).show()
        mContent.setText("")
    }

    fun decryptData(view: View) {
        preferencesManager = SharedPrefsRSAManager.getInstance(this, mKey)
        mContent.setText( preferencesManager.getString())
    }
}