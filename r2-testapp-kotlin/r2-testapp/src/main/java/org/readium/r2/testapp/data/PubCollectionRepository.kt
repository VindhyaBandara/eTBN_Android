package org.readium.r2.testapp.data

import android.content.Context
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject
import org.readium.r2.testapp.data.model.AuthenticatedUser
import org.readium.r2.testapp.data.model.LatestVersionInfo
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.data.model.PublicationInfo

class PubCollectionRepository(val pubcolletiondataSource: PubCollectionDataSource) {

    interface VolleyResponseListenerRep {
        fun onError(message:String)
        fun onResponse(response:Any)
    }

    var pubcollectionlist: Array<PublicationCollections>? = null
        private set
    var pubinfo: PublicationInfo? = null
        private set

    fun getcollectionlist(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String,volleyresponseListenerrep: PubCollectionRepository.VolleyResponseListenerRep) {
        // handle login
        pubcolletiondataSource.getcollectionlist(loggeduserid,orgID, pcontext,usertoken, object: PubCollectionDataSource.VolleyResponseListener {

            override fun onError(message: String) {
                volleyresponseListenerrep.onError(message)
            }
            override fun onResponse(response: Any) {
                //var collectionlist:Array<PublicationCollections> = response as Array<PublicationCollections>
                setCollections(response as Array<PublicationCollections>)
                volleyresponseListenerrep.onResponse(response)
            }
        })


    }

    fun getregeddevices(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String,volleyresponseListenerrep: PubCollectionRepository.VolleyResponseListenerRep) {
        // handle login
        pubcolletiondataSource.getregeddevices(loggeduserid,orgID, pcontext,usertoken, object: PubCollectionDataSource.VolleyResponseListener {

            override fun onError(message: String) {
                volleyresponseListenerrep.onError(message)
            }
            override fun onResponse(response: Any) {
                //var collectionlist:Array<PublicationCollections> = response as Array<PublicationCollections>
                setCollections(response as Array<PublicationCollections>)
                volleyresponseListenerrep.onResponse(response)
            }
        })


    }

    fun getPublicationinfo(uuid: String,pcontext: Context,volleyresponseListenerrep: PubCollectionRepository.VolleyResponseListenerRep) {
        pubcolletiondataSource.getPublicationInfo(uuid,pcontext,object: LoginDataSource.VolleyResponseListener {

            override fun onError(message: String) {
                volleyresponseListenerrep.onError(message)
            }
            override fun onResponse(response: Any) {
                setVersionInfo(response as PublicationInfo)
                volleyresponseListenerrep.onResponse(response)
            }
        })
    }

    private fun setCollections(collectionlist: Array<PublicationCollections>) {
        this.pubcollectionlist = collectionlist
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private fun setVersionInfo(pubinfomation: PublicationInfo) {
        this.pubinfo = pubinfomation
    }
}