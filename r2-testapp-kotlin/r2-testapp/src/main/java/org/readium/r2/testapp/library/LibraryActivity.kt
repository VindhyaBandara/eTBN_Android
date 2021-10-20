/*
 * Module: r2-testapp-kotlin
 * Developers: Aferdita Muriqi, Clément Baumann, Mostapha Idoubihi, Paul Stoica
 *
 * Copyright (c) 2018. European Digital Reading Lab. All rights reserved.
 * Licensed to the Readium Foundation under one or more contributor license agreements.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.testapp.library

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.webkit.URLUtil
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import com.mcxiaoke.koi.ext.onClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.design.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.readium.r2.shared.Injectable
import org.readium.r2.shared.extensions.extension
import org.readium.r2.shared.extensions.toPng
import org.readium.r2.shared.extensions.tryOrNull
import org.readium.r2.shared.publication.ContentProtection
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.publication.services.isRestricted
import org.readium.r2.shared.publication.services.protectionError
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.mediatype.MediaType
import org.readium.r2.streamer.Streamer
import org.readium.r2.streamer.server.Server
import org.readium.r2.testapp.BuildConfig.DEBUG
import org.readium.r2.testapp.CatalogActivity
import org.readium.r2.testapp.R
import org.readium.r2.testapp.R2AboutActivity
import org.readium.r2.testapp.RegisteredDevicesActivity
import org.readium.r2.testapp.db.*
import org.readium.r2.testapp.drm.DRMFulfilledPublication
import org.readium.r2.testapp.drm.DRMLibraryService
import org.readium.r2.testapp.opds.GridAutoFitLayoutManager
import org.readium.r2.testapp.opds.OPDSListActivity
import org.readium.r2.testapp.permissions.PermissionHelper
import org.readium.r2.testapp.permissions.Permissions
import org.readium.r2.testapp.ui.login.AppSession
import org.readium.r2.testapp.ui.login.LoginActivity
import org.readium.r2.testapp.ui.login.LogoutActivity
import org.readium.r2.testapp.utils.ContentResolverUtil
import org.readium.r2.testapp.utils.NavigatorContract
import org.readium.r2.testapp.utils.extensions.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.ServerSocket
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.readium.r2.shared.util.File as R2File

var activitiesLaunched: AtomicInteger = AtomicInteger(0)

@SuppressLint("Registered")
abstract class LibraryActivity : AppCompatActivity(), BooksAdapter.RecyclerViewClickListener, DRMLibraryService, CoroutineScope {

    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var server: Server
    private var localPort: Int = 0

    private lateinit var booksAdapter: BooksAdapter
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var permissions: Permissions
    private lateinit var preferences: SharedPreferences
    private lateinit var R2DIRECTORY: String

    private lateinit var database: BooksDatabase
    private lateinit var positionsDB: PositionsDatabase

    protected var contentProtections: List<ContentProtection> = emptyList()
    private lateinit var streamer: Streamer

    private lateinit var catalogView: androidx.recyclerview.widget.RecyclerView
    private lateinit var alertDialog: AlertDialog
    private lateinit var documentPickerLauncher: ActivityResultLauncher<String>
    private lateinit var navigatorLauncher: ActivityResultLauncher<NavigatorContract.Input>
    lateinit var appsession: AppSession

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            preferences = getSharedPreferences("org.readium.r2.settings", Context.MODE_PRIVATE)

            streamer = Streamer(this, contentProtections = contentProtections)

            val s = ServerSocket(if (DEBUG) 8080 else 0)
            s.localPort
            s.close()

            localPort = s.localPort
            server = Server(localPort, applicationContext)

            val properties = Properties()
            val inputStream = this.assets.open("configs/config.properties")
            properties.load(inputStream)
            val useExternalFileDir = properties.getProperty("useExternalFileDir", "false")!!.toBoolean()

            R2DIRECTORY = if (useExternalFileDir) {
                this.getExternalFilesDir(null)?.path + "/"
            } else {
                this.filesDir.path + "/"
            }

            permissions = Permissions(this)
            permissionHelper = PermissionHelper(this, permissions)

            database = BooksDatabase(this)
            books = database.books.list()

            booksAdapter = BooksAdapter(this, books, this)

            documentPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { importPublicationFromUri(it) }
            }

            navigatorLauncher = registerForActivityResult(NavigatorContract()) { pubData: NavigatorContract.Output? ->
                if (pubData == null)
                    return@registerForActivityResult

                tryOrNull { pubData.publication.close() }
                Timber.d("Publication closed")
                if (pubData.deleteOnResult)
                    tryOrNull { pubData.file.file.delete() }
            }

            intent.data?.let { importPublicationFromUri(it) }

            val opdslistactivity = OPDSListActivity()
            opdslistactivity.appsession = AppSession(appContext)
            val orgname = opdslistactivity.appsession.orgname

            coordinatorLayout {
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    //backgroundColor=R.color.colorAccent
                    weightSum = 0f
                    padding = dip(4)
                    lparams(width = matchParent, height = wrapContent)
                    //weightSum = 2f
                    textView {
                        text = orgname
                        textSize = 20f
                    }.setTypeface(null, Typeface.BOLD)
                }
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    //backgroundColor=R.color.colorAccent
                    weightSum = 0f
                    padding = dip(27)
                    lparams(width = matchParent, height = wrapContent)
                    //weightSum = 2f

                }

                lparams {
                    topMargin = dip(8)
                    bottomMargin = dip(8)
                    padding = dip(0)
                    width = matchParent
                    height = matchParent
                }

                catalogView = recyclerView {
                    layoutManager = GridAutoFitLayoutManager(this@LibraryActivity, 120)
                    adapter = booksAdapter
                    topPadding = 75

                    lparams {
                        elevation = 2F
                        width = matchParent
                    }

                    addItemDecoration(VerticalSpaceItemDecoration(10))
                }

                floatingActionButton {
                    imageResource = R.drawable.icon_plus_white
                    contentDescription = context.getString(R.string.floating_button_add_book)

                    onClick {
                        //val loggeduid:String = intent.getStringExtra("loggeduid").toString()
                        val opdslistactivity = OPDSListActivity()
                        opdslistactivity.appsession = AppSession(appContext)
                        val loggeduid = opdslistactivity.appsession.loggeduserid


                        val i = Intent(this@LibraryActivity, OPDSListActivity::class.java)
                        i.putExtra("loggeduid", loggeduid)
                        startActivity(i)
                        this@LibraryActivity.finish()
                    }
                }.lparams {
                    gravity = Gravity.END or Gravity.BOTTOM
                    margin = dip(16)
                }
            }
            Log.e("e.toString()", "FROM Rnd Try")
        } catch (e: Exception) {
            Log.e(e.toString(), "FROM LibraryActivity")
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            Log.e("onstart", "FROM onStart")
            startServer()

            permissionHelper.storagePermission {
                if (books.isEmpty()) {
                    /*****************************************/

                    alert(Appcompat, "Please go to Collections to download publications") {
                        positiveButton("Proceed") {
                            it.dismiss()

                            //val loggeduid:String
                            //loggeduid= intent.getStringExtra("loggeduid").toString()


                            val opdslistactivity = OPDSListActivity()
                            opdslistactivity.appsession = AppSession(appContext)
                            val loggeduid = opdslistactivity.appsession.loggeduserid

                            //this.finish()

                            val i = Intent(this@LibraryActivity, OPDSListActivity::class.java)
                            i.putExtra("loggeduid", loggeduid)
                            startActivity(i)

                            this@LibraryActivity.finish()
                        }
                        negativeButton("Later") {
                            it.dismiss()
                            //cont.resume(false)
                        }
                    }.build().apply {
                        setCancelable(false)
                        setCanceledOnTouchOutside(false)
                        show()
                    }
                    /************************************/
                    if (!preferences.contains("samples")) {
                        val dir = File(R2DIRECTORY)
                        if (!dir.exists()) {
                            dir.mkdirs()
                        }
                        launch {
                            copySamplesFromAssetsToStorage()
                        }
                        preferences.edit().putBoolean("samples", true).apply()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(e.toString(), "FROM onStart error")
        }
    }

    override fun onResume() {
        super.onResume()
        booksAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        //TODO not sure if this is needed
        stopServer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {

            R.id.opds -> {
                //val loggeduid:String = intent.getStringExtra("loggeduid").toString()
                val opdslistactivity = OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@LibraryActivity, OPDSListActivity::class.java)
                i.putExtra("loggeduid", loggeduid)
                startActivity(i)
                this.finish()

                false
            }
            R.id.about -> {
                startActivity(intentFor<R2AboutActivity>())
                this.finish()//yes night code
                false
            }
            R.id.regddevs -> {
                val opdslistactivity =  OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@LibraryActivity, RegisteredDevicesActivity::class.java)
                i.putExtra ("loggeduid" , loggeduid)
                startActivity(i)
                this.finish()

                false
            }
            R.id.logout -> {
                startActivity(intentFor<LogoutActivity>())
                false
            }
//            R.id.library -> {
//                startActivity(intentFor<LibraryActivity>())
//                false
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        this.permissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startServer() {
        if (!server.isAlive) {
            try {
                server.start()
            } catch (e: IOException) {
                // do nothing
                if (DEBUG) Timber.e(e)
            }
            if (server.isAlive) {
//                // Add your own resources here
//                server.loadCustomResource(assets.open("scripts/test.js"), "test.js")
//                server.loadCustomResource(assets.open("styles/test.css"), "test.css")
//                server.loadCustomFont(assets.open("fonts/test.otf"), applicationContext, "test.otf")

                server.loadCustomResource(assets.open("Search/mark.js"), "mark.js", Injectable.Script)
                server.loadCustomResource(assets.open("Search/search.js"), "search.js", Injectable.Script)
                server.loadCustomResource(assets.open("Search/mark.css"), "mark.css", Injectable.Style)

                isServerStarted = true
            }
        }
    }

    private fun stopServer() {
        if (server.isAlive) {
            server.stop()
            isServerStarted = false
        }
    }

    private suspend fun copySamplesFromAssetsToStorage() = withContext(Dispatchers.IO) {
        val samples = assets.list("Samples")?.filterNotNull().orEmpty()

        for (element in samples) {
            val file = assets.open("Samples/$element").copyToTempFile()
            if (file != null)
                importPublication(file)
            else if (BuildConfig.DEBUG)
                error("Unable to load sample into the library")
        }
    }

    private fun showDownloadFromUrlAlert() {
        var editTextHref: EditText? = null
        alert(Appcompat, "Add a publication from URL") {

            customView {
                verticalLayout {
                    textInputLayout {
                        padding = dip(10)
                        editTextHref = editText {
                            hint = "URL"
                            contentDescription = "Enter A URL"
                        }
                    }
                }
            }
            positiveButton("Add") { }
            negativeButton("Cancel") { }

        }.build().apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setOnShowListener {
                val b = getButton(AlertDialog.BUTTON_POSITIVE)
                b.setOnClickListener {
                    if (TextUtils.isEmpty(editTextHref!!.text)) {
                        editTextHref!!.error = "Please Enter A URL."
                        editTextHref!!.requestFocus()
                    } else if (!URLUtil.isValidUrl(editTextHref!!.text.toString())) {
                        editTextHref!!.error = "Please Enter A Valid URL."
                        editTextHref!!.requestFocus()
                    } else {
                        val url = tryOrNull { URL(editTextHref?.text.toString()) }
                            ?: return@setOnClickListener

                        launch {
                            val progress =
                                blockingProgressDialog(getString(R.string.progress_wait_while_downloading_book))
                                    .apply { show() }

                            val downloadedFile = url.copyToTempFile() ?: return@launch
                            importPublication(downloadedFile, sourceUrl = url.toString(), progress = progress)
                        }
                    }
                }
            }

        }.show()
    }

    private fun importPublicationFromUri(uri: Uri) {

        launch {
            val progress = blockingProgressDialog(getString(R.string.progress_wait_while_downloading_book))
                .apply { show() }

            uri.copyToTempFile()
                ?.let { importPublication(it, sourceUrl = uri.toString(), progress = progress) }
                ?: progress.dismiss()
        }
    }

    private suspend fun importPublication(sourceFile: R2File, sourceUrl: String? = null, progress: ProgressDialog? = null) {
        val foreground = progress != null
        try {


            val publicationFile =
                if (sourceFile.mediaType() != MediaType.LCP_LICENSE_DOCUMENT)
                    sourceFile
                else {
                    fulfill(sourceFile.file,"usertoken").fold(
                        {
                            val mediaType = MediaType.of(fileExtension = File(it.suggestedFilename).extension)
                            R2File(it.localFile.path, mediaType = mediaType)
                        },
                        {
                            tryOrNull { sourceFile.file.delete() }
                            Timber.d(it)
                            progress?.dismiss()
                            if (foreground) catalogView.longSnackbar("fulfillment error: ${it.message}")
                            return
                        }
                    )
                }

            val mediaType = publicationFile.mediaType()
            val fileName = "${UUID.randomUUID()}.${mediaType.fileExtension}"
            val libraryFile = R2File(
                R2DIRECTORY + fileName,
                mediaType = publicationFile.mediaType()
            )

            try {
                publicationFile.file.moveTo(libraryFile.file)
            } catch (e: Exception) {
                Timber.d(e)
                tryOrNull { publicationFile.file.delete() }
                progress?.dismiss()
                if (foreground) catalogView.longSnackbar("unable to move publication into the library")
                return
            }

            val extension = libraryFile.let {
                it.mediaType().fileExtension ?: it.file.extension
            }

            val isRwpm = libraryFile.mediaType().isRwpm

            val bddHref =
                if (!isRwpm)
                    libraryFile.path
                else
                    sourceUrl ?: run {
                        Timber.e("Trying to add a RWPM to the database from a file without sourceUrl.")
                        progress?.dismiss()
                        return
                    }

            streamer.open(libraryFile, allowUserInteraction = false, sender = this@LibraryActivity)
                .onSuccess {
                    addPublicationToDatabase(bddHref, extension, it).let { success ->
                        progress?.dismiss()
                        val msg =
                            if (success)
                                "publication added to your library"
                            else
                                "unable to add publication to the database"
                        if (foreground)
                            catalogView.longSnackbar(msg)
                        else
                            Timber.d(msg)
                        if (success && isRwpm)
                            tryOrNull { libraryFile.file.delete() }
                    }
                }
                .onFailure {
                    tryOrNull { libraryFile.file.delete() }
                    Timber.d(it)
                    progress?.dismiss()
                    if (foreground) presentOpeningException(it)
                }
        } catch (e: Exception) {
            val tetst = e.message
        }
    }

    private suspend fun addPublicationToDatabase(href: String, extension: String, publication: Publication): Boolean {
        val publicationIdentifier = publication.metadata.identifier ?: ""
        val author = publication.metadata.authorName
        val cover = publication.cover()?.toPng()


        val book = Book(
            title = publication.metadata.title,
            author = author,
            href = href,
            identifier = publicationIdentifier,
            cover = cover,
            ext = ".$extension",
            progression = "{}"
        )

        return addBookToDatabase(book)
    }

    private suspend fun addBookToDatabase(book: Book, alertDuplicates: Boolean = true): Boolean {
        database.books.insert(book, allowDuplicates = !alertDuplicates)?.let { id ->
            book.id = id
            books.add(0, book)
            withContext(Dispatchers.Main) {
                booksAdapter.notifyDataSetChanged()
            }
            return true
        }

        return if (alertDuplicates && confirmAddDuplicateBook(book))
            addBookToDatabase(book, alertDuplicates = false)
        else
            false
    }

    private suspend fun URL.copyToTempFile(): R2File? = tryOrNull {
        val filename = UUID.randomUUID().toString()
        val extn = "lcpl"//added by vin on 03-04-2021
        //val file = File("$R2DIRECTORY$filename.$extension")by vindhya on 03-04-2021
        val file = File("$R2DIRECTORY$filename.$extn")
        //val file = File("$R2DIRECTORY$filename.$extension")

        if (download(file.path)) R2File(file.path)
        else null
    }

    private suspend fun Uri.copyToTempFile(): R2File? = tryOrNull {
        val filename = UUID.randomUUID().toString()
        val mediaType = MediaType.ofUri(this, contentResolver)
        val file = R2File("$R2DIRECTORY$filename.${mediaType?.fileExtension ?: "tmp"}", mediaType = mediaType)
        ContentResolverUtil.getContentInputStream(this@LibraryActivity, this, file.path)
        return file
    }

    private suspend fun InputStream.copyToTempFile(): R2File? = tryOrNull {
        val filename = UUID.randomUUID().toString()
        R2File(R2DIRECTORY + filename)
            .also { toFile(it.path) }
    }

    override fun recyclerViewListLongClicked(v: View, position: Int) {
        //Inflating the layout
        val layout = LayoutInflater.from(this).inflate(R.layout.popup_delete, catalogView, false)
        val popup = PopupWindow(this)
        popup.contentView = layout
        popup.width = ListPopupWindow.WRAP_CONTENT
        popup.height = ListPopupWindow.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        popup.showAsDropDown(v, 24, -350, Gravity.CENTER)
        val delete: Button = layout.findViewById(R.id.delete) as Button
        delete.setOnClickListener {
            val book = books[position]
            books.remove(book)
            booksAdapter.notifyDataSetChanged()
            tryOrNull { File(book.href).delete() }
            val deleted = database.books.delete(book)
            if (deleted > 0) {
                BookmarksDatabase(this).bookmarks.delete(deleted.toLong())
                PositionsDatabase(this).positions.delete(deleted.toLong())
            }
            popup.dismiss()
            catalogView.longSnackbar("publication deleted from your library")
        }
    }

    private suspend fun confirmAddDuplicateBook(book: Book): Boolean = suspendCoroutine { cont ->
        alert(Appcompat, "Publication already exists") {
            positiveButton("Add anyway") {
                it.dismiss()
                cont.resume(true)
            }
            negativeButton("Cancel") {
                it.dismiss()
                cont.resume(false)
            }
        }.build().apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    override fun recyclerViewListClicked(v: View, position: Int) {
        val progress = blockingProgressDialog(getString(R.string.progress_wait_while_preparing_book))
        /*
        FIXME: if the progress dialog were shown, the LCP popup window would not be accessible to the user.
        progress.show()
         */
        progress.dismiss()

        launch {
            val booksDB = BooksDatabase(this@LibraryActivity)
            val book = books[position]

            val remoteUrl = tryOrNull { URL(book.href).copyToTempFile() }
            val mediaType = MediaType.of(fileExtension = book.ext.removePrefix("."))
            val file = remoteUrl // remote file
                ?: R2File(book.href, mediaType = mediaType) // local file

            streamer.open(file, allowUserInteraction = true, sender = this@LibraryActivity)
                .onFailure {
                    Timber.d(it)
                    progress.dismiss()
                    presentOpeningException(it)
                }
                .onSuccess { it ->
                    if (it.isRestricted) {
                        progress.dismiss()
                        it.protectionError?.let { error ->
                            Timber.d(error)
                            /***********************************************Delete expired publication dialog****************************************************/
                            catalogView.longSnackbar(error.getUserMessage(this@LibraryActivity) + ". Please contact your administrator to renew the license and then download the book again.")

                            /***********************************************Delete expired publication dialog****************************************************/

                        }
                    } else {
                        prepareToServe(it, file)
                        progress.dismiss()
                        navigatorLauncher.launch(
                            NavigatorContract.Input(
                                file = file,
                                mediaType = mediaType,
                                publication = it,
                                bookId = book.id,
                                initialLocator = book.id?.let { id -> booksDB.books.currentLocator(id) },
                                deleteOnResult = remoteUrl != null,
                                baseUrl = Publication.localBaseUrlOf(file.name, localPort)
                            )
                        )
                    }
                }
        }
    }

    private fun prepareToServe(publication: Publication, file: R2File) {
        val key = publication.metadata.identifier ?: publication.metadata.title
        preferences.edit().putString("$key-publicationPort", localPort.toString()).apply()
        val userProperties = applicationContext.filesDir.path + "/" + Injectable.Style.rawValue + "/UserProperties.json"
        server.addEpub(publication, null, "/${file.name}", userProperties)
    }

    private fun presentOpeningException(error: Publication.OpeningException) {
        catalogView.longSnackbar(error.getUserMessage(this))
    }

    class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView,
                                    state: androidx.recyclerview.widget.RecyclerView.State) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    override suspend fun fulfill(file: File,usertoken :String): Try<DRMFulfilledPublication, Exception> =
        Try.failure(Exception("DRM not supported"))

    companion object {

        var isServerStarted = false
            private set

    }

}
