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
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.mcxiaoke.koi.ext.onClick
import com.squareup.picasso.Picasso
import org.readium.r2.shared.extensions.putPublication
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.opds.images
import org.readium.r2.testapp.R
import org.readium.r2.testapp.RegisteredDevicesActivity
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.ui.login.AppSession

class RecyclerViewAdapter(private val activity: Activity, private val strings: MutableList<Publication>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.item_recycle_opds, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val publication = strings[position]
        viewHolder.textView.text = publication.metadata.title

        publication.linkWithRel("http://opds-spec.org/image/thumbnail")?.let { link ->
            Picasso.with(activity).load(link.href).into(viewHolder.imageView)
        } ?: run {
            if (publication.images.isNotEmpty()) {
                Picasso.with(activity).load(publication.images.first().href).into(viewHolder.imageView)
            }
        }

        viewHolder.itemView.onClick {
            /****************************************************/
//            val opdslistactivity =  OPDSListActivity()
//            opdslistactivity.appsession = AppSession(appContext)
//            val loggeduid = opdslistactivity.appsession.loggeduserid
//
//            val i = Intent(activity, RegisteredDevicesActivity::class.java)
//            i.putExtra ("uuid" , "a7d60750-ae40-46e0-a950-a18eafae77c4")
//            startActivity(i)
//            this.finish()
            /****************************************************/
            activity.startActivity(Intent(activity, OPDSDetailActivity::class.java).apply {
                putPublication(publication)
            })
        }
    }

    override fun getItemCount(): Int {
        return strings.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById<View>(R.id.titleTextView) as TextView
        val imageView: ImageView = view.findViewById(R.id.coverImageView) as ImageView

    }
}
