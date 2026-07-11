package com.hotaro.strictclock

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.os.Handler
import android.os.Looper

object UpdateChecker {
    suspend fun check(context: Context, currentVersionName: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/Hotaro26/Mascot/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val tagName = json.getString("tag_name")
                    val htmlUrl = json.getString("html_url")
                    
                    val latestVersion = tagName.removePrefix("v")
                    val current = currentVersionName.removePrefix("v")
                    
                    val latestNum = latestVersion.toDoubleOrNull() ?: 0.0
                    val currentNum = current.toDoubleOrNull() ?: 0.0
                    
                    if (latestNum > currentNum) {
                        Handler(Looper.getMainLooper()).post {
                            AlertDialog.Builder(context)
                                .setTitle("Update Available")
                                .setMessage("A new version of mascot (v$latestVersion) is available! Would you like to download it?")
                                .setPositiveButton("Update") { _, _ ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(htmlUrl))
                                    context.startActivity(intent)
                                }
                                .setNegativeButton("Later", null)
                                .show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
