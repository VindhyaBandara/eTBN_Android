/*
 * Module: r2-testapp-kotlin
 * Developers: Aferdita Muriqi, Clément Baumann
 *
 * Copyright (c) 2018. European Digital Reading Lab. All rights reserved.
 * Licensed to the Readium Foundation under one or more contributor license agreements.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

@file:Suppress("DEPRECATION")

package org.readium.r2.testapp.opds

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.commonsware.cwac.merge.MergeAdapter
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.filter_row.view.*
import kotlinx.android.synthetic.main.popup_window_user_settings.view.*
import kotlinx.android.synthetic.main.section_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.ui.successUi
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.nestedScrollView
import org.readium.r2.opds.OPDS1Parser
import org.readium.r2.opds.OPDS2Parser
import org.readium.r2.shared.opds.Facet
import org.readium.r2.shared.opds.ParseData
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.opds.numberOfItems
import org.readium.r2.testapp.BuildConfig.DEBUG
import org.readium.r2.testapp.CatalogActivity
import org.readium.r2.testapp.R
import org.readium.r2.testapp.R2AboutActivity
import org.readium.r2.testapp.RegisteredDevicesActivity
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.ui.login.AppSession
import org.readium.r2.testapp.ui.login.LogoutActivity
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.first
import kotlin.collections.indices
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableListOf
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates


class OPDSCatalogActivity : AppCompatActivity(), CoroutineScope {
    /**
     * Context of this scope.
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var facets: MutableList<Facet>
    private var parsePromise: Promise<ParseData, Exception>? = null
    //private var opdsModel: OPDSModel? = null
    private var publicationcollections: PublicationCollections? = null
    private var showFacetMenu = false
    private var facetPopup: PopupWindow? = null
    private lateinit var progress: ProgressDialog
    lateinit var appsession: AppSession
    public var pnumber: Int = 0
    public var totcount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val puburl:String
        val pubtitle:String
        val pubtype:String
        val reqparams:String

        progress = indeterminateProgressDialog(getString(R.string.progress_wait_while_loading_feed))

        //publicationcollections = intent.getSerializableExtra("publicationcollections") as? PublicationCollections//OPDSModel

        //if (intent != null) {
           // publicationcollections = intent.getSerializableExtra("publicationcollections") as? PublicationCollections

            puburl= intent.getStringExtra("publicationurl").toString()
            pubtitle= intent.getStringExtra("publicationtitle").toString()
            pubtype="1"
            reqparams=intent.getStringExtra("paramvalus").toString()

        //publicationcollections?.gethref().let {
        val opdscatalogactivity =  OPDSCatalogActivity()
        opdscatalogactivity.appsession = AppSession(this.applicationContext)
        val orgname = opdscatalogactivity.appsession.orgname
        val test = opdscatalogactivity.appsession.pagenumber

        pnumber = opdscatalogactivity.appsession.pagenumber.toInt()


        puburl.let {
        //tt.let {

            progress.show()
            try {
                //parsePromise = if (publicationcollections?.gettype() == "1") {
                if(reqparams=="NA") {
                    parsePromise = if (pubtype == "1") {
                        // parsePromise = if (ss == "1") {
                        //OPDS1Parser.parseURL(URL(it))

                        OPDS1Parser.parseURL(getHeaders(), URL(it))
                    } else {
                        OPDS2Parser.parseURL(URL(it))
                    }
                    title = pubtitle
                }
                else
                {
                    parsePromise = if (pubtype == "1") {
                        OPDS1Parser.parseURL(getHeaders(), URL(it),reqparams)
                    } else {
                        OPDS2Parser.parseURL(URL(it))
                    }
                    title = pubtitle
                }
            } catch (e: MalformedURLException) {
                progress.dismiss()
                snackbar(act.coordinatorLayout(), "Failed parsing OPDS")
            }
        }
        parsePromise?.successUi { result ->

            facets = result.feed?.facets ?: mutableListOf()

            if (facets.size > 0) {
                showFacetMenu = true
            }
            invalidateOptionsMenu()

            launch {
                nestedScrollView {
                    padding = dip(8)

                    linearLayout {
                        orientation = LinearLayout.VERTICAL
                        /*****************by vin*********************************/

                        textView {
                            text = orgname
                            textSize = 20f
                        }.setTypeface(null, Typeface.BOLD);

                        var sedtid : Int = 202
                        editText(

                        ).id=sedtid
                        val txtsearch = findViewById<EditText>(sedtid)
                        val searchval= txtsearch.text
                        button {
                            text = "Search"
                            onClick {

                                var collectionid:String = puburl.substringAfterLast("/")

                                //val tempurl = "https://prothumia-etbn-hub-v1-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/" + collectionid
                                val tempurl = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/" + collectionid

                                //val model = PublicationCollections(searchval.toString(), tempurl, "1")

                                val intent = Intent(this@OPDSCatalogActivity ,OPDSCatalogActivity::class.java)

                                intent.putExtra("publicationurl" ,tempurl);

                                val coltitle="Searched for: " + searchval
                                intent.putExtra("publicationtitle" , coltitle);

                                val paramvalus:String
                                paramvalus="1,10,'',true,null," + searchval


                                intent.putExtra("paramvalus", paramvalus)

                                startActivity(intent)
                            }
                        }

                        /***********************by vin*******************/
                        /***********************************************************/
                        coordinatorLayout {
                            linearLayout {
                                orientation = LinearLayout.HORIZONTAL
                                //android.R.attr.layout_gravity = LinearLayout.TEXT_ALIGNMENT_GRAVITY
                                //backgroundColor=R.color.colorAccent
                                weightSum = 0f
                                padding = dip(2)
                                this.lparams(width = matchParent, height = wrapContent)
                                //weightSum = 2f

                                totcount = result.feed?.metadata?.numberOfItems!!

                                var prevbutid : Int = 1000

                                button() {
                                    text = "<"
                                    textSize = 15f
                                    padding= 0

                                    var coltitle = pubtitle


                                    onClick {
                                        if(pnumber!=1)
                                        {
                                            pnumber -= 1
                                        }
                                        opdscatalogactivity.appsession.pagenumber=pnumber.toString()

                                        var collectionid:String = puburl.substringAfterLast("/")
                                        //val tempurl = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/" + collectionid
                                        val tempurl = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/" + collectionid

                                        val intent = Intent(this@OPDSCatalogActivity ,OPDSCatalogActivity::class.java)

                                        intent.putExtra("publicationurl" ,tempurl);


                                        intent.putExtra("publicationtitle" , coltitle);

                                        val paramvalus:String
                                        paramvalus= pnumber.toString() + ",10,'',true,null," + searchval


                                        intent.putExtra("paramvalus", paramvalus)

                                        startActivity(intent)
                                    }
                                }.id=prevbutid//setTypeface(null, Typeface.BOLD)

                                val btnprev = findViewById<Button>(prevbutid)
                                btnprev.isEnabled = pnumber != 1/*if pnumber != 1 then enable the button*/

                                var nextbutid : Int = 2000
                                button() {
                                    text = ">"
                                    textSize = 15f

                                    onClick {
                                        pnumber += 1
                                        opdscatalogactivity.appsession.pagenumber = pnumber.toString()

                                        var coltitle = pubtitle
                                        var collectionid: String = puburl.substringAfterLast("/")

                                        //val tempurl = "https://prothumia-etbn-hub-v1-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/" + collectionid
                                        val tempurl = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/" + collectionid

                                        val intent = Intent(this@OPDSCatalogActivity, OPDSCatalogActivity::class.java)

                                        intent.putExtra("publicationurl", tempurl);


                                        intent.putExtra("publicationtitle", coltitle);

                                        val paramvalus: String
                                        paramvalus = pnumber.toString() + ",10,'',true,null," + searchval


                                        intent.putExtra("paramvalus", paramvalus)

                                        startActivity(intent)
                                    }
                                }.id=nextbutid//setTypeface(null, Typeface.BOLD)

                                val btnnext = findViewById<Button>(nextbutid)
                                if(pnumber>totcount/10)
                                {
                                    btnnext.isEnabled=false
                                }
                                else
                                {
                                    btnnext.isEnabled = pnumber * 10 < totcount
                                }
                                //btnnext.isEnabled = pnumber != totcount/2
                            }
                        }
                        /***********************************************************/

                        for (navigation in result.feed!!.navigation) {
                            button {
                                text = navigation.title
                                onClick {
                                    val model = PublicationCollections(navigation.title!!, navigation.href.toString(), "1")//opdsModel?.type vindhya
                                    progress.show()
                                    startActivity(intentFor<OPDSCatalogActivity>("publicationcollections" to model))
                                }
                            }
                        }

                        if (result.feed!!.publications.isNotEmpty()) {
                            recyclerView {
                                layoutManager = GridAutoFitLayoutManager(act, 120)
                                adapter = RecyclerViewAdapter(act, result.feed!!.publications)
                            }
                        }

                        for (group in result.feed!!.groups) {
                            if (group.publications.isNotEmpty()) {

                                linearLayout {
                                    orientation = LinearLayout.HORIZONTAL
                                    padding = dip(10)
                                    bottomPadding = dip(5)
                                    this.lparams(width = matchParent, height = wrapContent)
                                    weightSum = 2f
                                    textView {
                                        text = group.title
                                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)

                                    if (group.links.size > 0) {
                                        textView {
                                            text = context.getString(R.string.opds_list_more)
                                            gravity = Gravity.END
                                            onClick {
                                                val model = PublicationCollections(group.title, group.links.first().href.toString(), "1")//opdsModel?.type vindhya
                                                startActivity(intentFor<OPDSCatalogActivity>("publicationcollections" to model))
                                            }
                                        }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                                    }
                                }

                                recyclerView {
                                    layoutManager = LinearLayoutManager(act)
                                    (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
                                    adapter = RecyclerViewAdapter(act, group.publications)
                                }
                            }
                            if (group.navigation.isNotEmpty()) {
                                for (navigation in group.navigation) {
                                    button {
                                        text = navigation.title
                                        onClick {
                                            val model = PublicationCollections(navigation.title!!, navigation.href.toString(), "1")//opdsModel?.type vindhya
                                            startActivity(intentFor<OPDSCatalogActivity>("publicationcollections" to model))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                progress.dismiss()
            }
        }

        parsePromise?.fail {
            launch {
                progress.dismiss()
//                snackbar(act.coordinatorLayout(), it.message!!)
            }
            if (DEBUG) Timber.e(it)
        }

    }

    fun getHeaders(): MutableMap<String, String> {
        val opdscatalogactivity = OPDSCatalogActivity()
        opdscatalogactivity.appsession = AppSession(appContext)
        val usertoken = opdscatalogactivity.appsession.usertoken
        val orgid = opdscatalogactivity.appsession.orgid

        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $usertoken"
        headers["organizationId"] = orgid
        return headers
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.opds -> {
                val opdscatalogactivity = OPDSCatalogActivity()
                opdscatalogactivity.appsession = AppSession(appContext)
                val loggeduid = opdscatalogactivity.appsession.loggeduserid

                val i = Intent(this@OPDSCatalogActivity, OPDSListActivity::class.java)
                i.putExtra ("loggeduid" , loggeduid)
                startActivity(i)

                this.finish()
                false
            }
            R.id.regddevs -> {
                val opdslistactivity =  OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@OPDSCatalogActivity, RegisteredDevicesActivity::class.java)
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
        val opdscatalogactivity = OPDSCatalogActivity()
        opdscatalogactivity.appsession = AppSession(appContext)
        val loggeduid = opdscatalogactivity.appsession.loggeduserid

        val i = Intent(this@OPDSCatalogActivity, OPDSListActivity::class.java)
        i.putExtra ("loggeduid" , loggeduid)
        startActivity(i)
        //startActivity(intentFor<OPDSListActivity>())
        this.finish()
    }

    override fun onPause() {
        super.onPause()
        progress.dismiss()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_filter, menu)
//
//        return showFacetMenu
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//
//            R.id.filter -> {
//                facetPopup = facetPopUp()
//                facetPopup?.showAsDropDown(this.findViewById(R.id.filter), 0, 0, Gravity.END)
//                false
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun facetPopUp(): PopupWindow {

        val layoutInflater = LayoutInflater.from(this)
        val layout = layoutInflater.inflate(R.layout.filter_window, null)
        val userSettingsPopup = PopupWindow(this)
        userSettingsPopup.contentView = layout
        userSettingsPopup.width = ListPopupWindow.WRAP_CONTENT
        userSettingsPopup.height = ListPopupWindow.WRAP_CONTENT
        userSettingsPopup.isOutsideTouchable = true
        userSettingsPopup.isFocusable = true

        val adapter = MergeAdapter()
        for (i in facets.indices) {
            adapter.addView(headerLabel(facets[i].title))
            for (link in facets[i].links) {
                adapter.addView(linkCell(link))
            }
        }

        val facetList = layout.findViewById<ListView>(R.id.facetList)
        facetList.adapter = adapter

        return userSettingsPopup
    }

    private fun headerLabel(value: String): View {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.section_header, null) as LinearLayout
        layout.header.text = value
        return layout
    }

    private fun linkCell(link: Link?): View {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.filter_row, null) as LinearLayout
        layout.text.text = link!!.title
        layout.count.text = link.properties.numberOfItems?.toString()
        layout.setOnClickListener {
            val model = PublicationCollections(link.title!!, link.href.toString(), "1")//opdsModel?.type vindhya
            facetPopup?.dismiss()
            startActivity(intentFor<OPDSCatalogActivity>("publicationcollections" to model))
        }
        return layout
    }
}
