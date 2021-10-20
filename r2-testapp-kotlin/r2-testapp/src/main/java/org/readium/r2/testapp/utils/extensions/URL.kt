/* Module: r2-testapp-kotlin
* Developers: Quentin Gliosca
*
* Copyright (c) 2020. European Digital Reading Lab. All rights reserved.
* Licensed to the Readium Foundation under one or more contributor license agreements.
* Use of this source code is governed by a BSD-style license which is detailed in the
* LICENSE file present in the project repository where this source code is maintained.
*/

package org.readium.r2.testapp.utils.extensions

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.extensions.tryOr
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.File
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.opds.OPDSCatalogActivity
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import org.readium.r2.testapp.ui.login.AppSession

lateinit var appsession: AppSession
suspend fun URL.download(path: String): Boolean = tryOr(false) {
    withContext(Dispatchers.IO) {
        openStream().use { input ->
            FileOutputStream(File(path).file).use { output ->
                input.copyTo(output)
            }
        }
    }
    true
}

suspend fun URL.downloadwithauth(path: String,fileurl:String,usertoken:String): Boolean = tryOr(false) {
    withContext(Dispatchers.IO) {

        val url = URL(fileurl)
        //val url = URL("https://prothumia-hub-v1-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/download/5820fd61-5f98-4fc0-879e-ae598faaf4db")


        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        urlConnection.addRequestProperty("Authorization", "Bearer $usertoken")
         BufferedInputStream(urlConnection.getInputStream()).use { input ->
            FileOutputStream(File(path).file).use { output ->
                input.copyTo(output)
            }
        }
    }
    true
}