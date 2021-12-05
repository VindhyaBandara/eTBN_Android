/*
 * Module: r2-testapp-kotlin
 * Developers: Aferdita Muriqi, Clément Baumann
 *
 * Copyright (c) 2018. European Digital Reading Lab. All rights reserved.
 * Licensed to the Readium Foundation under one or more contributor license agreements.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.testapp.opds

//import org.readium.r2.testapp.db.Book
//import org.readium.r2.testapp.db.BooksDatabase

//import org.readium.r2.testapp.db.Book
//import org.readium.r2.testapp.db.BooksDatabase
//import org.readium.r2.testapp.db.books
//import org.readium.r2.testapp.db.Book
//import org.readium.r2.testapp.db.BooksDatabase
//import org.readium.r2.testapp.db.books
//import org.readium.r2.testapp.db.Book
//import org.readium.r2.testapp.db.BooksDatabase
//import org.readium.r2.testapp.db.books
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcxiaoke.koi.ext.onClick
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.nestedScrollView
import org.readium.r2.lcp.LcpService
import org.readium.r2.shared.extensions.*
import org.readium.r2.shared.publication.ContentProtection
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.opds.images
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.mediatype.MediaType
import org.readium.r2.streamer.Streamer
import org.readium.r2.testapp.CatalogActivity
import org.readium.r2.testapp.R
import org.readium.r2.testapp.R2AboutActivity
import org.readium.r2.testapp.RegisteredDevicesActivity
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.db.Book
import org.readium.r2.testapp.db.BooksDatabase
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.db.books
import org.readium.r2.testapp.drm.DRMFulfilledPublication
import org.readium.r2.testapp.drm.DRMLibraryService
import org.readium.r2.testapp.library.BooksAdapter
import org.readium.r2.testapp.library.LibraryActivity
import org.readium.r2.testapp.permissions.PermissionHelper
import org.readium.r2.testapp.permissions.Permissions
import org.readium.r2.testapp.ui.login.AppSession
import org.readium.r2.testapp.ui.login.LoginViewModel
import org.readium.r2.testapp.ui.login.LoginViewModelFactory
import org.readium.r2.testapp.ui.login.LogoutActivity
import org.readium.r2.testapp.utils.extensions.*
import timber.log.Timber
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.readium.r2.shared.util.File as R2File

class OPDSDetailActivity : AppCompatActivity(), DRMLibraryService, CoroutineScope {
    /**
     * Context of this scope.
     */
    private lateinit var pubcollectionViewModel: PubColletctionViewModel
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var R2DIRECTORY: String
    private lateinit var catalogView: androidx.recyclerview.widget.RecyclerView
    private lateinit var streamer: Streamer
    private lateinit var database: BooksDatabase
    private lateinit var booksAdapter: BooksAdapter
    protected var contentProtections: List<ContentProtection> = emptyList()
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var permissions: Permissions
    private lateinit var lcpService: LcpService
    lateinit var appsession: AppSession

    override fun onCreate(savedInstanceState: Bundle?) {

        /*********************Added By Vindyha for Implementing LCP Module***************************/
        super.onCreate(savedInstanceState)
        pubcollectionViewModel = ViewModelProvider(this@OPDSDetailActivity, PubColletionViewModelFactory())
            .get(PubColletctionViewModel::class.java)

        lcpService = LcpService(this) ?: throw Exception("liblcp is missing on the classpath")
        contentProtections = listOf(lcpService.contentProtection())


        /*********************end Added By Vindhya for Implementing LCP Module***************************/

        //val database = BooksDatabase(this)

        val opdsDownloader = OPDSDownloader(this)

        /*********************Added By Vindyha for Implementing LCP Module***************************/
        streamer = Streamer(this, contentProtections = contentProtections)
        /*********************Added By Vindyha for Implementing LCP Module***************************/

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

        lcpService = LcpService(this) ?: throw Exception("liblcp is missing on the classpath")
        contentProtections = listOf(lcpService.contentProtection())


        /*********************Added By Vindyha for Implementing LCP Module***************************/



        catalogView = recyclerView {
            layoutManager = GridAutoFitLayoutManager(this@OPDSDetailActivity, 120)
            //adapter = booksAdapter

            this.lparams {
                elevation = 2F
                width = matchParent
                topMargin=50
            }

            addItemDecoration(LibraryActivity.VerticalSpaceItemDecoration(10))

        }

        val publication: Publication = intent.getPublication(this)



        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            //backgroundColor=R.color.colorAccent
            weightSum = 0f
            padding = dip(2)
            this.lparams(width = matchParent, height = wrapContent)
            //weightSum = 2f
            textView {
                text = "orgname"
                textSize = 20f

            }.setTypeface(null, Typeface.BOLD);
        }
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            //backgroundColor=R.color.colorAccent
            weightSum = 0f
            padding = dip(12)
            this.lparams(width = matchParent, height = wrapContent)
            //weightSum = 2f
        }

        nestedScrollView {
            fitsSystemWindows = true
            this.lparams(width = matchParent, height = matchParent)
            padding = dip(10)

            val identifier = publication.metadata.identifier.toString()//"http://etbn.global/dfd35346-4fb9-4237-855a-7a24a5d807b7"
            val uuid = identifier.substring(identifier.lastIndexOf("/") + 1, identifier.length);

            pubcollectionViewModel.getPublicationInfo(uuid,applicationContext)

            pubcollectionViewModel.PublicationInfoResult.observe(this@OPDSDetailActivity, Observer {
                val publicationinforesult = it ?: return@Observer

                if (publicationinforesult.error != null) {
                    val test= publicationinforesult.error
                }
                if (publicationinforesult.success != null) {
                    val test = publicationinforesult.success.test.authorName

                    linearLayout {
                        orientation = LinearLayout.VERTICAL

                        imageView {
                            this@linearLayout.gravity = Gravity.CENTER

                            publication.coverLink?.let { link ->
                                /*Replaced with() > get() -- Need to check 'Picasso.with' vs 'Picasso.get' in run time-- by Vindhya*/
                                Picasso.with(this@OPDSDetailActivity).load(link.href).into(this)
                                //Picasso.get().load(link.href).into(this)

                                /*Replaced with() > get() */
                            } ?: this.run {
                                if (publication.images.isNotEmpty()) {
                                    /*Replaced with() > get() -- Need to check 'Picasso.with' vs 'Picasso.get' in run time-- by Vindhya*/
                                    Picasso.with(this@OPDSDetailActivity).load(publication.images.first().href).into(this)
                                    //Picasso.get().load(publication.images.first().href).into(this)
                                    /*Replaced with() > get() */
                                }
                            }

                        }.lparams {
                            height = 800
                            width = matchParent
                        }

                        textView {
                            padding = dip(10)
                            text = publication.metadata.title //+ "-----" + uuid
                            textSize = 20f
                        }
                        textView {
                            padding = dip(10)

                            text = if(!publicationinforesult.success.test.authorName.isNullOrEmpty()){
                                "Contributor : " + publicationinforesult.success.test.authorName
                            } else {
                                "Contributor : NA"
                            }
                        }
                        textView {
                            padding = dip(10)
                            text = if(!publicationinforesult.success.test.isbn.isNullOrEmpty()) {
                                "ISBN : " + publicationinforesult.success.test.isbn
                            }else {
                                "ISBN : NA"
                            }
                        }
                        textView {
                            padding = dip(10)
                            text = if(!publicationinforesult.success.test.publisherName.isNullOrEmpty()) {
                                "Publisher : " + publicationinforesult.success.test.publisherName
                            }else {
                                "Publisher : NA"
                            }

                        }
                        textView {
                            padding = dip(10)
                            text =if(!publicationinforesult.success.test.cityOfPublication.isNullOrEmpty()) {
                                "Place of publication : " + publicationinforesult.success.test.cityOfPublication + ", " + publicationinforesult.success.test.countryOfPublication
                            }else {
                                "Place of publication : NA"
                            }
                        }
                        textView {
                            padding = dip(10)
                            text = if(!publicationinforesult.success.test.publishedDate.isNullOrEmpty()) {
                                "Date : " + publicationinforesult.success.test.publishedDate
                            }else {
                                "Date : NA"
                            }
                        }
                        textView {
                            padding = dip(10)
                            text = if(!publicationinforesult.success.test.summary.isNullOrEmpty()) {
                                publicationinforesult.success.test.summary
                            }else {
                                ""
                            }
                        }


                        val downloadUrl = getDownloadURL(publication)
                        var test= publication.metadata.otherMetadata
                        //val downloadUrl=URL("http://35.226.47.75:9003/api/v1/purchases/18/license")


                        downloadUrl?.let {
                            button {
                                text = context.getString(R.string.opds_detail_download_button)
                                onClick {
//                            val progress = indeterminateProgressDialog(getString(R.string.progress_wait_while_downloading_book))
//                            progress.show()

                                    var lastelement: String = downloadUrl.toString().substringAfterLast("/")
                                    if(lastelement != "")
                                    {
                                        launch (Dispatchers.Main)
                                        {

                                            val progress =
                                                blockingProgressDialog(getString(R.string.progress_wait_while_downloading_book))
                                                    .apply { show() }

//                                downloadUrl.openConnection().addRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluQGFkbWluLmNvbSIsInBhc3N3b3JkIjoiMTIzMzIxIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJvcmdhbml6YXRpb25OYW1lIjoiIiwib3JnYW5pemF0aW9uSWQiOjAsImlkIjoxLCJpYXQiOjE2MjU0NjM2OTgsImV4cCI6MTYyNTU1MDA5OH0.BuBgLkntvgOJB3d4pdu7cit50VArGWvV1rptFf1zotk")
//                                downloadUrl.openConnection()
                                            val downloadedFile = downloadUrl.copyToTempFile(downloadUrl.toString())?: return@launch
                                            //downloadUrl.openConnection()


                                            /****************************By Vindhya ****************************/
                                            var coverimg = ByteArrayOutputStream()
                                            val publicationIdentifier = publication.metadata.identifier ?: publication.metadata.title
                                            val author = publication.metadata.authorName
                                            /****************************End By Vindhya****************************/
                                            var bitmapurl:String =""

                                            if (publication.images.isNotEmpty()) {
                                                bitmapurl = publication.images.first().href
                                            }

                                            importPublication(downloadedFile, sourceUrl = downloadUrl.toString(), progress = progress, pubtitle = publication.metadata.title,bitmapurl = bitmapurl,pubidentifier = publicationIdentifier,pubauthor = author)
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(
                                            applicationContext,
                                            "Publication is not available.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    /***********************************************************************/

                                    /***********************************************************************/
                                }
                            }
                        }
                    }
                }
                else
                {

                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.opds -> {
                val opdsdetailactivity = OPDSDetailActivity()
                opdsdetailactivity.appsession = AppSession(appContext)
                val loggeduid = opdsdetailactivity.appsession.loggeduserid

                val i = Intent(this@OPDSDetailActivity, OPDSListActivity::class.java)
                i.putExtra ("loggeduid" , loggeduid)
                startActivity(i)
                this.finish()
                false
            }
            R.id.regddevs -> {
                val opdslistactivity =  OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@OPDSDetailActivity, RegisteredDevicesActivity::class.java)
                i.putExtra ("loggeduid" , loggeduid)
                startActivity(i)
                this.finish()

                false
            }
            R.id.about -> {
                startActivity(intentFor<R2AboutActivity>())
                this.finish()
                false
            }
            R.id.logout -> {
                startActivity(intentFor<LogoutActivity>())
                false
            }
            R.id.library -> {
                //startActivity(intentFor<LibraryActivity>())
                startActivity(intentFor<CatalogActivity>())
                this.finish()
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val opdsdetailactivity = OPDSDetailActivity()
        opdsdetailactivity.appsession = AppSession(appContext)
        val loggeduid = opdsdetailactivity.appsession.loggeduserid

        val i = Intent(this@OPDSDetailActivity, OPDSListActivity::class.java)
        i.putExtra ("loggeduid" , loggeduid)
        startActivity(i)
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        intent.destroyPublication(this)
    }

    private fun getDownloadURL(publication: Publication): URL? {
        var url: URL? = null
        val links = publication.links
        for (link in links) {
            val href = link.href
            //"pdf" added by vindhya
            //if (href.contains(Publication.EXTENSION.EPUB.value) || href.contains(Publication.EXTENSION.LCPL.value) || href.contains("pdf")) {
            url = URL(href)
            break
            //}
        }
        return url
    }

    private fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    /*********************Added By Vindyha for Implementing LCP Module***************************/

    private suspend fun URL.copyToTempFile(fileurl: String): R2File? = tryOrNull {
        val filename = UUID.randomUUID().toString()
        val file = File("$R2DIRECTORY$filename.$extension")



        /****************************By Vindhya for tokenized request************************************/
        //val urlnew = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/download/7bea5382-a00b-46f9-9cb4-e9f6fa1dc42d"
        //if (download(file.path)) R2File(file.path)
        //if (downloadwithauth(file.path,"https://prothumia-hub-v1-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/download/b38b11af-cf0d-4a2f-8c7a-d5b6bc58b079")) R2File(file.path)
        val opdsdetailactivity = OPDSDetailActivity()
        opdsdetailactivity.appsession = AppSession(appContext)
        val usertoken = opdsdetailactivity.appsession.usertoken

        if (downloadwithauth(file.path,fileurl,usertoken)) R2File(file.path)
        /****************************By Vindhya for tokenized request************************************/
        else null
    }

    /*********************Added By Vindyha for Implementing LCP Module***************************/

    private suspend fun importPublication(sourceFile: R2File, sourceUrl: String? = null, progress: ProgressDialog? = null,pubtitle:String,bitmapurl:String,pubidentifier:String,pubauthor:String) {

        //progress.show()
        val database = BooksDatabase(this)
        val foreground = progress != null

        val publicationFile =
                if (sourceFile.mediaType() != MediaType.LCP_LICENSE_DOCUMENT)
                    sourceFile

                else {
                    val opdsdetailactivity = OPDSDetailActivity()
                    opdsdetailactivity.appsession = AppSession(appContext)
                    val usertoken = opdsdetailactivity.appsession.usertoken

                    fulfill(sourceFile.file,usertoken).fold(
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

        streamer.open(libraryFile, allowUserInteraction = false, sender = this@OPDSDetailActivity)//@LibraryActivity
                .onSuccess {
                    Log.e("streamer","success")
                    addPublicationToDatabase(bddHref, extension, it,pubtitle,bitmapurl,pubidentifier,pubauthor).let {success ->
                        Log.e("app pub in success","success")
                        progress?.dismiss()
                        val msg =
                                if (success) {
                                    "publication added to your library"
                                }
                                else
                                    "unable to add publication to the database"
                        Log.e(msg.toString(),"msg")
                        if (foreground) {
                            Log.e("snackbar", "download completed vindhya")

                            Toast.makeText(
                                    applicationContext,
                                    "Download Completed",
                                    Toast.LENGTH_LONG
                            ).show()
                            //snackbar(this@OPDSDetailActivity,"download completed")

                            val i = Intent(this, CatalogActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                        //catalogView.snackbar("test test 33")

                        //catalogView.longSnackbar(msg)
                        //catalogView.snackbar(msg)
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


    }

    private suspend fun addPublicationToDatabase(href: String, extension: String, publication: Publication,pubtitle:String,bitmapurl:String,pubidentifier:String,pubauthor:String): Boolean {
        val publicationIdentifier = publication.metadata.identifier ?: ""
        val author = publication.metadata.authorName
        var coverimage = null
        val cover = publication.cover()?.toPng()
        var coverimg = ByteArrayOutputStream()

        if(cover==null)
        {
            val stream = ByteArrayOutputStream()
            val bitmap = getBitmapFromURL(bitmapurl)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            coverimg = stream


            val book = Book(
                    //title = publication.metadata.title, on 06-04-2021 by vindhya
                    title =pubtitle,
                    author = pubauthor,
                    href = href,
                    identifier = pubidentifier,
                    //cover = cover,commented bu vin 06-04-2021 original
                    cover = coverimg.toByteArray(),
                    ext = ".$extension",
                    progression = "{}"
            )

            return addBookToDatabase(book)
        }
        else
        {

            val book = Book(
                    //title = publication.metadata.title, on 06-04-2021 by vindhya
                    title =pubtitle,
                    author = author,
                    href = href,
                    identifier = publicationIdentifier,
                    //cover = cover,commented by vin 06-04-2021 original
                    cover = cover,
                    ext = ".$extension",
                    progression = "{}"
            )

            return addBookToDatabase(book)
        }
    }

    private suspend fun addBookToDatabase(book: Book, alertDuplicates: Boolean = true): Boolean {
        val database = BooksDatabase(this)
        database.books.insert(book, allowDuplicates = !alertDuplicates)?.let { id ->
            book.id = id
            books.add(0, book)
            withContext(Dispatchers.Main) {
                //booksAdapter.notifyDataSetChanged()
            }
            return true
        }

        return if (alertDuplicates && confirmAddDuplicateBook(book))
            addBookToDatabase(book, alertDuplicates = false)
        else
            false
    }
    // override suspend fun fulfill(file: File): Try<DRMFulfilledPublication, Exception> =
    //         Try.failure(Exception("DRM not supported"))

    override suspend fun fulfill(file: File,usertoken:String): Try<DRMFulfilledPublication, Exception> =
            lcpService.acquirePublication(file,usertoken).map { DRMFulfilledPublication(it.localFile, it.suggestedFilename) }

    private fun presentOpeningException(error: Publication.OpeningException) {
        catalogView.longSnackbar(error.getUserMessage(this))
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
}
