package com.haripm.encryptionkotlinutility.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.haripm.encryptionkotlinutility.R
import com.haripm.encryptionkotlinutility.utils.EncryptionManager
import com.haripm.encryptionkotlinutility.utils.StringUtils

class AesSharedPrefEncryptionActivity : AppCompatActivity() {

    private lateinit var mContent: EditText
    private lateinit var mKeyText: EditText
    private lateinit var mKeyEntered: String
    private lateinit var mKey: String
    private lateinit var mEncryptionMode: EncryptionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_prefs)

        mContent = findViewById(R.id.content_text)
        mKeyText = findViewById(R.id.key_text)
        mKeyEntered = mKeyText.text.toString()
        mEncryptionMode = EncryptionManager(this)
    }

    fun encryptData(view: View) {
        val valueEntered = mContent.text.toString()

        mKey = StringUtils.generateSafeToken()
        mEncryptionMode.putValue(mKeyEntered, valueEntered)
        Toast.makeText(this@AesSharedPrefEncryptionActivity,"Data encrypted and stored in shared preferences", Toast.LENGTH_LONG).show()

        mContent.setText("")
    }

    fun decryptData(view: View) {
        mContent.setText(mEncryptionMode.getValue(mKeyEntered).toString())
        Toast.makeText(
            this@AesSharedPrefEncryptionActivity, "Data is decrypted and retrieved successfully", Toast.LENGTH_LONG).show()
    }
}