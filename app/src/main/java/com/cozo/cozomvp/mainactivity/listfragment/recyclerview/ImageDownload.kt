package com.cozo.cozomvp.mainactivity.listfragment.recyclerview

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import java.io.File
import java.io.FileNotFoundException

class ImageDownload(private val context: Context, private val bmImage: ImageView,
                    picRefId: String) : AsyncTask<String, Void, Bitmap>() {

    init {
        this.execute(picRefId)
    }

    override fun doInBackground(vararg urls: String): Bitmap? {
        val urlDisplay = urls[0]
        val cachedImage: Bitmap? = getCachedImage(urlDisplay)
        if (cachedImage != null){
            return cachedImage
        }
        var downloadedImage: Bitmap? = null
        try {
            val inputStream = java.net.URL(urlDisplay).openStream()
            downloadedImage = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        storeImageToCache(urlDisplay)
        return downloadedImage
    }

    override fun onPostExecute(result: Bitmap) {
        bmImage.setImageBitmap(result)
    }

    private fun getCachedImage(url: String): Bitmap? =
            Uri.parse(url)?.lastPathSegment?.let{ filename ->
                try{
                    return BitmapFactory.decodeFile(filename)
                } catch (e: FileNotFoundException){
                    return null
                }
            }

    private fun storeImageToCache(url: String): File? =
            Uri.parse(url)?.lastPathSegment?.let { filename ->
                File.createTempFile(filename, null, context.cacheDir)
            }

}