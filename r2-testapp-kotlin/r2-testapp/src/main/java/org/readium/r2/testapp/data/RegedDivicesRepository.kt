package org.readium.r2.testapp.data

import android.content.Context
import android.net.MacAddress
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject
import org.readium.r2.testapp.data.model.AuthenticatedUser
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.data.model.RegisteredDevices

class RegedDivicesRepository (val regeddevicesdataSource: RegedDevicesDataSource){

    interface VolleyResponseListenerRep {
        fun onError(message:String)
        fun onResponse(response:Any)
    }

    var regeddevices: Array<RegisteredDevices>? = null
        private set

    fun getregeddevices(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String,volleyresponseListenerrep: RegedDivicesRepository.VolleyResponseListenerRep) {
        regeddevicesdataSource.getdevicelist(loggeduserid,orgID, pcontext,usertoken, object: RegedDevicesDataSource.VolleyResponseListener {

            override fun onError(message: String) {
                volleyresponseListenerrep.onError(message)
            }
            override fun onResponse(response: Any) {
                //var collectionlist:Array<PublicationCollections> = response as Array<PublicationCollections>
                setdevices(response as Array<RegisteredDevices>)
                volleyresponseListenerrep.onResponse(response)
            }
        })


    }

    fun requestfordelete(id:String?,pcontext: Context,usertoken:String,volleyresponseListenerrep: RegedDivicesRepository.VolleyResponseListenerRep) {
        regeddevicesdataSource.requestfordelete(id,pcontext,usertoken, object: RegedDevicesDataSource.VolleyResponseListener {

            override fun onError(message: String) {
                volleyresponseListenerrep.onError(message)
            }
            override fun onResponse(response: Any) {
                //var collectionlist:Array<PublicationCollections> = response as Array<PublicationCollections>
                if(response is String)
                {
                    volleyresponseListenerrep.onResponse(response)
                }
                else {
                    setdevices(response as Array<RegisteredDevices>)
                    volleyresponseListenerrep.onResponse(response)
                }
            }
        })


    }

    private fun setdevices(devicelist: Array<RegisteredDevices>) {
        this.regeddevices = devicelist
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}