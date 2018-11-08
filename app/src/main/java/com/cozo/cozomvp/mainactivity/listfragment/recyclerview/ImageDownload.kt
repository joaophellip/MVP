package com.cozo.cozomvp.mainactivity.listfragment.recyclerview

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import java.io.*
import java.security.MessageDigest

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
        storeImageToCache(urlDisplay, downloadedImage)
        return downloadedImage
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            bmImage.setImageBitmap(result)
        } else {
            // get default image when unable to download it from backend (for whatever reason)
        }
    }

    private fun getCachedImage(url: String): Bitmap? =
            Uri.parse(url)?.lastPathSegment?.let{ filename ->
                try{
                    val file = File(context.cacheDir, hashFilename(filename))
                    return BitmapFactory.decodeStream(FileInputStream(file))
                } catch (e: FileNotFoundException){
                    return null
                }
            }

    private fun storeImageToCache(url: String, downloadedImage: Bitmap?) {
        val f =Uri.parse(url)?.lastPathSegment?.let { filename ->
            File(context.cacheDir, hashFilename((filename)))
        }
        val byteOut = ByteArrayOutputStream()
        downloadedImage!!.compress(Bitmap.CompressFormat.JPEG, 100, byteOut)
        val out = FileOutputStream(f)
        out.write(byteOut.toByteArray())
        out.flush()
        out.close()
        byteOut.close()
    }

    private fun hashFilename(name: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        val textBytes = name.toByteArray()
        md.update(textBytes, 0, textBytes.size)
        val sha1hash = md.digest()
        val result = sha1hash.map {
            String.format("%02X", (it.toInt() and 0xff))
        }.joinToString("")
        return result
    }


}