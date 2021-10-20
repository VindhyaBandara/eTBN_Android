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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.mcxiaoke.koi.ext.onClick
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.then
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.nestedScrollView
import org.json.JSONObject
import org.readium.r2.opds.OPDS1Parser
import org.readium.r2.opds.OPDS2Parser
import org.readium.r2.shared.opds.ParseData
import org.readium.r2.shared.promise
import org.readium.r2.testapp.CatalogActivity
import org.readium.r2.testapp.R
import org.readium.r2.testapp.R2AboutActivity
import org.readium.r2.testapp.RegisteredDevicesActivity
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.ui.login.AppSession
import org.readium.r2.testapp.ui.login.LogoutActivity
import java.net.URL

class OPDSListActivity : AppCompatActivity() {//word public added by vindhya

    private lateinit var pubcollectionViewModel: PubColletctionViewModel
    var pubcollectionlist:Array<PublicationCollections>? = null
    //public static lateinit var : AppSession
    lateinit var appsession: AppSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pubcollectionViewModel = ViewModelProvider(this@OPDSListActivity, PubColletionViewModelFactory())
                .get(PubColletctionViewModel::class.java)

//        appsession = AppSession(this@OPDSListActivity);
//        var etts= appsession.loggeduserid

        val database = OPDSDatabase(this)

        val preferences = getSharedPreferences("org.readium.r2.testapp", Context.MODE_PRIVATE)

        val version = 2
        val VERSION_KEY = "OPDS_CATALOG_VERSION"

        if (preferences.getInt(VERSION_KEY, 0) < version) {
            preferences.edit().putInt(VERSION_KEY, version).apply()
        }

        /*****************************************************************/
        var loggeduser:String= intent.getStringExtra("loggeduid").toString()

        val opdslistactivity =  OPDSListActivity()
        opdslistactivity.appsession = AppSession(this.applicationContext)
        var usertoken:String = opdslistactivity.appsession.usertoken
        var orgname:String = opdslistactivity.appsession.orgname
        var OrgID:String = opdslistactivity.appsession.orgid
        opdslistactivity.appsession.pagenumber = "1"

        pubcollectionViewModel.getpubcollectionlist(loggeduser,OrgID,this@OPDSListActivity,usertoken)


        pubcollectionViewModel.CollectionResult.observe(this@OPDSListActivity, Observer {
            val collectionResult = it ?: return@Observer
            if (collectionResult.error != null) {
                val test:String=collectionResult.error.toString()
            }
            if (collectionResult.success != null) {
                val collectionlist:Array<PublicationCollections> = collectionResult.success.test
                pubcollectionlist=collectionlist

                /*******************from outside**********************************/
                //val list = pubcollectionlist?.toMutableList()
                val opdsAdapter = pubcollectionlist?.toMutableList()?.let { OPDSViewAdapter(this, it,this@OPDSListActivity) }
//                val list = pubcollectionlist?.toMutableList() as MutableList<OPDSModel>
//                val opdsAdapter = OPDSViewAdapter(this, list)



                coordinatorLayout {
                    fitsSystemWindows = true
                    this.lparams(width = matchParent, height = matchParent)
                    padding = dip(10)

                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        //backgroundColor=R.color.colorAccent
                        weightSum = 0f
                        padding = dip(2)
                        this.lparams(width = matchParent, height = wrapContent)
                        //weightSum = 2f
                        textView {
                            text = orgname
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
                        this.lparams(width = matchParent, height = matchParent)
                        linearLayout {
                            orientation = LinearLayout.VERTICAL
                            topPadding = 75
                            recyclerView {
                                layoutManager = LinearLayoutManager(this@OPDSListActivity)
                                (layoutManager as LinearLayoutManager).orientation = RecyclerView.VERTICAL
                                adapter = opdsAdapter
                            }
                        }
                    }

                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about -> {
                startActivity(intentFor<R2AboutActivity>())
                this.finish()
                false
            }
            R.id.regddevs -> {
                val opdslistactivity =  OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@OPDSListActivity, RegisteredDevicesActivity::class.java)
                i.putExtra ("loggeduid" , loggeduid)
                startActivity(i)
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
        startActivity(intentFor<CatalogActivity>())
        this.finish()
    }
//    private fun parseURL(url: URL): Promise<ParseData, Exception> {
//        return Fuel.get(url.toString(), null).promise() then {
//            val (_, _, result) = it
//            if (isJson(result)) {
//                OPDS2Parser.parse(result, url)
//            } else {
//                OPDS1Parser.parse(result, url)
//            }
//        }
//    }

    private fun isJson(byteArray: ByteArray): Boolean {
        return try {
            JSONObject(String(byteArray))
            true
        } catch (e: Exception) {
            false
        }
    }
}

private class OPDSViewAdapter(private val activity: Activity, private val list: MutableList<PublicationCollections>,private val contxt: Context) : RecyclerView.Adapter<OPDSViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.item_recycle_opds_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val opdslistactivity =  OPDSListActivity()
        opdslistactivity.appsession = AppSession(contxt)

        val loggeduserid = opdslistactivity.appsession.loggeduserid

        //val database = OPDSDatabase(activity)
        val publicationcollections = list[position]

        //var collectionurl:String = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/"
        var collectionurl:String = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/opds/xml/paginate/"


        //var tempurl:String = "https://storage.googleapis.com/prothumia/OPDS/Prothumia_LCPL_Books_v8.xml"


        viewHolder.button.text = publicationcollections.gettitle()//"test"//publicationcollections.gettitle()
        viewHolder.button.onClick {
            //snackbar(viewHolder.itemView, "test")
            try
            {
                //activity.startActivity(activity.intentFor<OPDSCatalogActivity>("publicationcollections" to publicationcollections::class.java))//opdsModel
                val test=publicationcollections
                val intent = Intent(viewHolder.button.context ,OPDSCatalogActivity::class.java)
                //var value = this@OPDSListActivity. start from here

                intent.putExtra ("publicationurl" ,collectionurl + publicationcollections.gettype())//publicationcollections.gethref()
                //intent.putExtra ("publicationurl" ,"https://storage.googleapis.com/prothumia/OPDS/Prothumia_EPUB_Books_v7.xml" )//publicationcollections.gethref()

                //intent.putExtra ("publicationurl" ,tempurl )
                intent.putExtra ("publicationtitle" , "Collection: " + publicationcollections.gettitle())

                val paramvalus:String
                paramvalus="1,10,'',true,null," + ""

                intent.putExtra ("paramvalus" , paramvalus)

                viewHolder.button.context.startActivity(intent)
            }
            catch (e : Exception)
            {
                val test=e.toString()
            }
        }
//        viewHolder.button.onLongClick {
//            database.opds.delete(opdsModel)
//            list.remove(opdsModel)
//            this.notifyDataSetChanged()
//            return@onLongClick true
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById<View>(R.id.button) as Button

    }
}
