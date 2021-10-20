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
import java.util.*


class PubCollectionDataSource {
    interface VolleyResponseListener {
        fun onError(message:String)
        fun onResponse(response:Any)
    }

    fun  getcollectionlist(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String, volleyreslisner: PubCollectionDataSource.VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/user-collection/:"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/user-collection/:"

        val jarrequest =  object:JsonArrayRequest(Request.Method.GET, url,null,
                Response.Listener<JSONArray> { response ->

                    val gson = GsonBuilder().create()
                    val CollectionArray: Array<PublicationCollections> = gson.fromJson(response.toString(), Array<PublicationCollections>::class.java)

                    volleyreslisner.onResponse(CollectionArray)
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

    fun  getregeddevices(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String, volleyreslisner: PubCollectionDataSource.VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-etbn-hub-v1-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/user-collection/:"
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices/devices"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/user-devices/devices"

        val jarrequest =  object:JsonArrayRequest(Request.Method.GET, url,null,
            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder().create()
                val CollectionArray: Array<PublicationCollections> = gson.fromJson(response.toString(), Array<PublicationCollections>::class.java)

                volleyreslisner.onResponse(CollectionArray)
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

    fun getcollectionlisttest(loggeduserid: String, pcontext: Context, volleyreslisner: PubCollectionDataSource.VolleyResponseListener) {
        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-hub-v2-dot-model-signifier-297723.uc.r.appspot.com/api/v1/collection"
        //val url = "https://prothumia-hub-v2-dot-model-signifier-297723.uc.r.appspot.com/api/v1/collection/1"
        val url = "https://www.metaweather.com/api/location/44418/"


        val jarrequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> { response ->
                    val collectionlist: JSONArray = response.getJSONArray("consolidated_weather")
//                    var strResp = response.toString()
//                    val gson = GsonBuilder().create()
//
//                    val pcollections: PublicationCollections = gson.fromJson(strResp, PublicationCollections::class.java)

                    volleyreslisner.onResponse(collectionlist)
                },
                Response.ErrorListener {
                    volleyreslisner.onError("Error Occured")
                })
        queue.add(jarrequest)
    }

    fun getreslist () {
        val reslist:List<PublicationCollections>

        //get the json object

        //get the array

        //get each item in array
    }
}


//val queue = Volley.newRequestQueue(pcontext)
//val url = "https://prothumia-hub-v2-dot-model-signifier-297723.uc.r.appspot.com/api/v1/collection"
//
//val jarrequest = JsonObjectRequest(Request.Method.GET, url,null,
//        Response.Listener<JSONObject> { response ->
//            val collectionlist:JSONObject=response.getJSONObject("")
////                    var strResp = response.toString()
////                    val gson = GsonBuilder().create()
////
////                    val pcollections: PublicationCollections = gson.fromJson(strResp, PublicationCollections::class.java)
//
//            volleyreslisner.onResponse(collectionlist)
//        },
//        Response.ErrorListener {
//            volleyreslisner.onError("Error Occured")
//        })
//queue.add(jarrequest)