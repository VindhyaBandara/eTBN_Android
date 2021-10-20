/*
 * Module: r2-testapp-kotlin
 * Developers: Aferdita Muriqi, Clément Baumann, Paul Stoica
 *
 * Copyright (c) 2018. European Digital Reading Lab. All rights reserved.
 * Licensed to the Readium Foundation under one or more contributor license agreements.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.testapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.mcxiaoke.koi.ext.onClick
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.library.LibraryActivity
import org.readium.r2.testapp.opds.OPDSCatalogActivity
import org.readium.r2.testapp.opds.OPDSListActivity
import org.readium.r2.testapp.ui.login.AppSession
import org.readium.r2.testapp.ui.login.LogoutActivity


class R2AboutActivity : AppCompatActivity() {
    lateinit var appsession: AppSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val opdscatalogactivity = R2AboutActivity()
        opdscatalogactivity.appsession = AppSession(appContext)
        val orgName = opdscatalogactivity.appsession.orgname



        coordinatorLayout {
            fitsSystemWindows = true
            this.lparams(width = wrapContent, height = matchParent)
            padding = dip(10)


            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                //backgroundColor=R.color.colorAccent
                weightSum = 0f
                padding= dip(2)
                this.lparams(width = matchParent, height = wrapContent)
                //weightSum = 2f
                textView {
                    text = orgName
                    textSize = 20f

                }.setTypeface(null, Typeface.BOLD);
            }
            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                //backgroundColor=R.color.colorAccent
                weightSum = 0f
                padding= dip(12)
                this.lparams(width = matchParent, height = wrapContent)
                //weightSum = 2f

            }

            linearLayout {
                orientation = LinearLayout.VERTICAL
                this.lparams(width = matchParent, height = matchParent)

                textView {
                    padding = dip(10)
                    topPadding = dip(30)
                    text = context.getString(R.string.app_version_header)
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                }
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    this.lparams(width = matchParent, height = wrapContent)
                    weightSum = 2f
                    textView {
                        padding = dip(10)
                        text = context.getString(R.string.app_version_label)
                        textSize = 18f
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                    textView {
                        padding = dip(10)
                        text =context.getString(R.string.app_version)
                        textSize = 18f
                        gravity = Gravity.END
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)

                }
                /*linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    lparams(width = matchParent, height = wrapContent)
                    weightSum = 2f
                    textView {
                        padding = dip(10)
                        text = context.getString(R.string.github_tab_label)
                        textSize = 18f
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                    textView {
                        padding = dip(10)
                        text = context.getString(R.string.github_tag)
                        textSize = 18f
                        gravity = Gravity.END
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                }*/

                textView {
                    padding = dip(10)
                    topPadding = dip(15)
                    text = context.getString(R.string.copyright_label)
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                }
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    this.lparams(width = matchParent, height = wrapContent)
                    weightSum = 2f
                    textView {
                        padding = dip(10)
                        text = context.getString(R.string.copyright)
                        textSize = 16f
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                    textView {
                        padding = dip(10)
                        text = ""
                        textSize = 18f
                        gravity = Gravity.END
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                }
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    this.lparams(width = matchParent, height = wrapContent)
                    weightSum = 2f
                    textView {
                        padding = dip(10)
                        text = context.getString(R.string.copyright_link)
                        textSize = 16f
                        onClick {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theologicalbooknetwork.org/"))
                            startActivity(browserIntent)
                        }
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f).setTextColor(Color.parseColor("#0000FF"))
                    textView {
                        padding = dip(10)
                        text = ""
                        textSize = 18f
                        gravity = Gravity.END
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                }
//                linearLayout {
//                    orientation = LinearLayout.HORIZONTAL
//                    this.lparams(width = matchParent, height = wrapContent)
//                    weightSum = 2f
//                    textView {
//                        padding = dip(10)
//                        text = context.getString(R.string.bsd_license_label)
//                        contentDescription = context.getString(R.string.bsd_license_label_accessible)
//                        textSize = 18f
//                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
//                    textView {
//                        padding = dip(10)
//                        text = ""
//                        textSize = 18f
//                        gravity = Gravity.END
//                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
//                }

                textView {
                    padding = dip(10)
                    topPadding = dip(15)
                    text = context.getString(R.string.acknowledgements_label)
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                }
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    this.lparams(width = matchParent, height = wrapContent)
                    weightSum = 2f
                    textView {
                        padding = dip(10)
                        text = context.getString(R.string.developed_by)
                        textSize = 18f
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                    textView {
                        padding = dip(10)
                        text = ""
                        textSize = 18f
                        gravity = Gravity.END
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                }
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    this.lparams(width = matchParent, height = wrapContent)
                    weightSum = 2f
                    textView {
                        padding = dip(10)
                        text = context.getString(R.string.developed_by_link)
                        textSize = 18f
                        textColor = 0
                        onClick {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.chesalon.com"))
                            startActivity(browserIntent)
                        }
                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f).setTextColor(Color.parseColor("#0000FF"))
//                    textView {
//                        padding = dip(10)
//                        text = ""
//                        textSize = 18f
//                        gravity = Gravity.END
//                    }.lparams(width = wrapContent, height = wrapContent, weight = 1f)
                }
                textView {
                    padding = dip(10)
                    topPadding = dip(30)
                    text = context.getString(R.string.about_thorium)
                    textSize = 18f
//                    typeface = Typeface.DEFAULT_BOLD
                }
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    this.lparams(width = matchParent, height = wrapContent)

                    imageView {
                        image = resources.getDrawable(R.drawable.repfr, theme)
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }.lparams(width = wrapContent, height = 200, weight = 1f)
                }


            }
        }
    }
    /**************************************yesterday night code****************************************/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {

            R.id.opds -> {
                //val loggeduid:String = intent.getStringExtra("loggeduid").toString()
                val opdslistactivity =  OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@R2AboutActivity, OPDSListActivity::class.java)
                i.putExtra ("loggeduid" , loggeduid)
                startActivity(i)
                this.finish()

                false
            }
            R.id.library -> {
                //startActivity(intentFor<LibraryActivity>())
                startActivity(intentFor<CatalogActivity>())
                this.finish()
                false
            }
            R.id.regddevs -> {
                val opdslistactivity =  OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@R2AboutActivity, RegisteredDevicesActivity::class.java)
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
    /**************************************yesterday night code****************************************/
    override fun onBackPressed() {
        startActivity(intentFor<CatalogActivity>())
        this.finish()
    }

}
