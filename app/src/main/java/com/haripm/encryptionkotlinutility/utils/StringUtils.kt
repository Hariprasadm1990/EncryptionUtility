package com.haripm.encryptionkotlinutility.utils

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import android.content.Context.MODE_PRIVATE
import android.util.Log
import java.io.*


object StringUtils {

        internal fun generateSafeToken(): String {
            val bytes = generateEncryptionKey()
            return Base64.encodeToString(bytes, 0)
        }

        internal fun generateEncryptionKey(): ByteArray {
            val key = ByteArray(64)
            SecureRandom().nextBytes(key)
            return key
        }

        fun writeToFile(data: String, context: Context) {
            try {
                val outputStreamWriter = OutputStreamWriter(context.openFileOutput("encrypted_key.txt", MODE_PRIVATE))
                outputStreamWriter.write(data)
                outputStreamWriter.close()
            } catch (e: IOException) {
                Log.e("Exception", "File write failed: ${e.message}")
            }
        }

        fun readFromFile(context: Context): String {

            var output = ""
            try {
                val inputStream = context.openFileInput("encrypted_key.txt")

                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var receiveString: String? = null
                    val stringBuilder = StringBuilder()
                    while ({ receiveString = bufferedReader.readLine(); receiveString }() != null) {
                        stringBuilder.append(receiveString)
                    }

                    inputStream.close()
                    output = stringBuilder.toString()
                }
            } catch (e: FileNotFoundException) {
                Log.e("Error ", "File not found: " + e.toString())
            } catch (e: IOException) {
                Log.e("Error ", "Can not read file: " + e.toString())
            }
            return output
        }

}