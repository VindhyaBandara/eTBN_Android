package org.readium.r2.testapp.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import org.readium.r2.lcp.license.model.components.lcp.User
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.data.model.RegisteredDevices
import java.nio.charset.Charset
import java.util.*
import com.android.volley.VolleyError

import com.android.volley.toolbox.StringRequest

class RegedDevicesDataSource {
    interface VolleyResponseListener {
        fun onError(message:String)
        fun onResponse(response:Any)
    }

    fun  getdevicelist(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String, volleyreslisner: RegedDevicesDataSource.VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices/devices"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices/devices"

        val jarrequest =  object:JsonArrayRequest(Request.Method.GET, url,null,
            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder().create()
                val DevicesArray: Array<RegisteredDevices> = gson.fromJson(response.toString(), Array<RegisteredDevices>::class.java)

                volleyreslisner.onResponse(DevicesArray)
            },
            Response.ErrorListener {
                volleyreslisner.onError("Error Occured")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $usertoken"
                headers["organizationId"] = orgID
                return headers
            }
        }
        queue.add(jarrequest)
    }

    fun  requestfordelete_(deviceID: String?, devicemodel: String?,devicename:String?,deviceOS:String?,id:String?,MACAdd:String?,user:String?, pcontext: Context,usertoken:String, volleyreslisner: RegedDevicesDataSource.VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices/devices"
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices"

        val requestBody = "DeviceID=$deviceID&DeviceModel=$devicemodel&DeviceName=$devicename&DeviceOS=$deviceOS&id=$id&MACAddress=$MACAdd&RequestedForRemove=true&user=$user"
        val jarrequest =  object:JsonArrayRequest(Request.Method.PUT, url,null,
            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder().create()
                val DevicesArray: Array<RegisteredDevices> = gson.fromJson(response.toString(), Array<RegisteredDevices>::class.java)

                volleyreslisner.onResponse(DevicesArray)
            },
            Response.ErrorListener {
                volleyreslisner.onError("Error Occured")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $usertoken"
                headers["content-type"] = "application/json"
                return headers
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        queue.add(jarrequest)
    }

    fun requestfordelete(id:String?, pcontext: Context,usertoken:String, volleyreslisner: RegedDevicesDataSource.VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices/$id"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices/$id"

        val putRequest: StringRequest = object : StringRequest(
            Method.DELETE, url,
            Response.Listener { response -> // response
                val gson = GsonBuilder().create()
                //val test: Array<String> = gson.fromJson(response.toString(), Array<String>::class.java)
                val retval = response.toString()
                volleyreslisner.onResponse(retval)
            },
            Response.ErrorListener { error -> // error
                volleyreslisner.onError("Error Occured")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $usertoken"
                //headers["content-type"] = "application/json"
                return headers
            }

//            override fun getParams(): Map<String, String>? {
//                val params: MutableMap<String, String> = HashMap()
//                //"DeviceID=$deviceID&DeviceModel=$devicemodel&DeviceName=$devicename&DeviceOS=$deviceOS&id=$id&MACAddress=$MACAdd&RequestedForRemove=true&user=$user"
//
//                params["id"] = "29"
//                return params
//            }
        }

        queue.add(putRequest)
    }

//    fun requestfordelete(deviceID: String?, devicemodel: String?,devicename:String?,deviceOS:String?,id:String?,MACAdd:String?,user:String?, pcontext: Context,usertoken:String, volleyreslisner: RegedDevicesDataSource.VolleyResponseListener)
//    {
//        val queue = Volley.newRequestQueue(pcontext)
//        val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices"
//
//        val putRequest: StringRequest = object : StringRequest(
//            Method.PUT, url,
//            Response.Listener { response -> // response
//                val gson = GsonBuilder().create()
//                //val test: Array<String> = gson.fromJson(response.toString(), Array<String>::class.java)
//                val retval = response.toString();
//                volleyreslisner.onResponse(retval)
//            },
//            Response.ErrorListener { error -> // error
//                volleyreslisner.onError("Error Occured")
//            }
//        ) {
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["Authorization"] = "Bearer $usertoken"
//                //headers["content-type"] = "application/json"
//                return headers
//            }
//
//            override fun getParams(): Map<String, String>? {
//                val params: MutableMap<String, String> = HashMap()
//                "DeviceID=$deviceID&DeviceModel=$devicemodel&DeviceName=$devicename&DeviceOS=$deviceOS&id=$id&MACAddress=$MACAdd&RequestedForRemove=true&user=$user"
//                params["DeviceID"] = "dd"
//                params["DeviceModel"] = "null"
//                params["DeviceName"] = "test"
//                params["DeviceOS"] = "test"
//                params["id"] = "29"
//                params["MACAddress"] = "dd"
//                params["RequestedForRemove"] = "true"
//                params["user"] = "s"
//                return params
//            }
//        }
//
//        queue.add(putRequest)
//    }
}