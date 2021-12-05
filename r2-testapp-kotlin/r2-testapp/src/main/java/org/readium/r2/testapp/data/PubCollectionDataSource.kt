package org.readium.r2.testapp.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import org.readium.r2.lcp.license.model.components.lcp.User
import org.readium.r2.testapp.data.model.LatestVersionInfo
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.data.model.PublicationInfo
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

    fun  getPublicationInfo(uuid : String,pcontext: Context,volleyreslisner: LoginDataSource.VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
        val url =
            "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/publications/UUID/$uuid"
        //val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/publications/25"

        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener<String> { response ->

                    var strResp = response.toString()
                    val gson = GsonBuilder().create()

                    val PPublicationInfo: PublicationInfo = gson.fromJson(strResp, PublicationInfo::class.java)

                    volleyreslisner.onResponse(PPublicationInfo)
                },
                Response.ErrorListener { error ->
                    if(error.networkResponse.statusCode== 401) {
                        volleyreslisner.onError("exceeded")
                    }
                    else
                    {
                        volleyreslisner.onError(error.networkResponse.statusCode.toString())
                    }
                }
            ) {
//                override fun getBody(): ByteArray {
//                    return requestBody.toByteArray(Charset.defaultCharset())
//                }
            }
        queue.add(stringReq)
    }

    fun getreslist () {
        val reslist:List<PublicationCollections>

        //get the json object

        //get the array

        //get each item in array
    }
}