package com.haripm.encryptionkotlinutility.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.haripm.encryptionkotlinutility.R
import android.widget.Button
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.net.URL
import android.content.ContextWrapper
import android.support.design.widget.Snackbar
import android.app.ProgressDialog
import android.widget.LinearLayout
import android.support.design.widget.CoordinatorLayout
import android.app.Activity
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


class CertificatePinningActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private var mActivity: Activity? = null

    private var mCLayout: CoordinatorLayout? = null
    private var mButtonDo: Button? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mLLayout: LinearLayout? = null

    private var mMyTask: AsyncTask<*, *, *>? = null

    val url = stringToURL("https://us.battle.net/wow/static/images/2d/avatar/6-0.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate_pinning)

        mContext = applicationContext
        mActivity = this@CertificatePinningActivity

        mCLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        mButtonDo = findViewById(R.id.btn_do)
        mLLayout = findViewById(R.id.ll)

        // Initialize the progress dialog
        mProgressDialog = ProgressDialog(mActivity)
        mProgressDialog!!.isIndeterminate = false
        // Progress dialog horizontal style
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        // Progress dialog title
        mProgressDialog!!.setTitle("AsyncTask")
        // Progress dialog message
        mProgressDialog!!.setMessage("Please wait, we are downloading your image files...")
        mProgressDialog!!.setCancelable(true)

        // Set a progress dialog dismiss listener
        mProgressDialog!!.setOnDismissListener {
            // Cancel the AsyncTask
            mMyTask!!.cancel(false)
        }
    }

    fun downloadImages(view: View) {
        mMyTask = DownloadTask()
            .execute(url)
    }

    private inner class DownloadTask : AsyncTask<URL, Int, List<Bitmap>>() {

        override fun onPreExecute() {
            // Display the progress dialog on async task start
            mProgressDialog?.show()
            mProgressDialog!!.progress = 0
        }

        override fun doInBackground(vararg urls: URL): List<Bitmap> {
// Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
// From https://www.washington.edu/itconnect/security/ca/load-der.crt
            val caInput: InputStream = resources.openRawResource(R.raw.certificate)
            val ca: X509Certificate = caInput.use {
                cf.generateCertificate(it) as X509Certificate
            }
            System.out.println("ca=" + ca.subjectDN)

// Create a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType).apply {
                load(null, null)
                setCertificateEntry("ca", ca)
            }

// Create a TrustManager that trusts the CAs inputStream our KeyStore
            val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
            val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                init(keyStore)
            }

// Create an SSLContext that uses our TrustManager
            val context: SSLContext = SSLContext.getInstance("TLS").apply {
                init(null, tmf.trustManagers, null)
            }

            var connection: HttpsURLConnection? = null
            val bitmaps = ArrayList<Bitmap>()

            val currentURL = urls[0]
            // So download the image from this url
            try {

                connection = currentURL.openConnection() as HttpsURLConnection

                // Connect the http url connection
                connection.connect()
                connection.sslSocketFactory = context.socketFactory
                // Get the input stream from http url connection
                val inputStream = connection.getInputStream()

                // Initialize a new BufferedInputStream from InputStream
                val bufferedInputStream = BufferedInputStream(inputStream)

                // Convert BufferedInputStream to Bitmap object
                val bmp = BitmapFactory.decodeStream(bufferedInputStream)

                // Add the bitmap to list
                bitmaps.add(bmp)


            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                // Disconnect the http url connection
                connection?.disconnect()
            }
            return bitmaps
        }

        // On AsyncTask cancelled
        override fun onCancelled() {
            Snackbar.make(mCLayout!!, "Task Cancelled.", Snackbar.LENGTH_LONG).show()
        }

        // When all async task done
        override fun onPostExecute(result: List<Bitmap>) {
            mProgressDialog!!.dismiss()

            mLLayout!!.removeAllViews()


            val bitmap = result[0]
            // Save the bitmap to internal storage
            val imageInternalUri = saveImageToInternalStorage(bitmap, 0)
            // Display the bitmap from memory
            addNewImageViewToLayout(bitmap)
            // Display bitmap from internal storage
            addNewImageViewToLayout(imageInternalUri)
        }
    }

    // Custom method to convert string to url
    protected fun stringToURL(urlString: String): URL? {
        try {
            return URL(urlString)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return null
    }

    // Custom method to save a bitmap into internal storage
    protected fun saveImageToInternalStorage(bitmap: Bitmap, index: Int): Uri {
        // Initialize ContextWrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "UniqueFileName$index.jpg")

        try {
            // Initialize a new OutputStream
            var stream: OutputStream? = null

            // If the output file exists, it can be replaced or appended to it
            stream = FileOutputStream(file)

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flushes the stream
            stream!!.flush()

            // Closes the stream
            stream!!.close()

        } catch (e: IOException) // Catch the exception
        {
            e.printStackTrace()
        }

        // Parse the gallery image url to uri

        // Return the saved image Uri
        return Uri.parse(file.absolutePath)
    }

    // Custom method to add a new image view using bitmap
    protected fun addNewImageViewToLayout(bitmap: Bitmap) {
        // Initialize a new ImageView widget
        val iv = ImageView(applicationContext)

        // Set an image for ImageView
        iv.setImageBitmap(bitmap)

        // Create layout parameters for ImageView
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500)

        // Add layout parameters to ImageView
        iv.layoutParams = lp

        // Finally, add the ImageView to layout
        mLLayout!!.addView(iv)
    }

    // Custom method to add a new image view using uri
    protected fun addNewImageViewToLayout(uri: Uri) {
        // Initialize a new ImageView widget
        val iv = ImageView(applicationContext)
        iv.setImageURI(uri)
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300)

        // Add layout parameters to ImageView
        iv.layoutParams = lp

        // Finally, add the ImageView to layout
        mLLayout!!.addView(iv)
    }
}