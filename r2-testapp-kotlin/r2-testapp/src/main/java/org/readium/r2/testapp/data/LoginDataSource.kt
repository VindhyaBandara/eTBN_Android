package org.readium.r2.testapp.data

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.google.gson.GsonBuilder
import org.readium.r2.testapp.data.model.LoggedInUser
import java.io.IOException
import java.nio.charset.Charset
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import org.readium.r2.testapp.data.model.AuthenticatedUser
import org.readium.r2.testapp.data.model.LatestVersionInfo
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.ui.login.LoginActivity

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    interface VolleyResponseListener {
        fun onError(message:String)
        fun onResponse(response:Any)
    }

    fun login(pusername: String, ppassword: String,pdeviceid:String,pdevicename:String,pmacdadd:String, pcontext: Context, volleyreslisner:VolleyResponseListener) {
        // TODO: handle loggedInUser authentication

        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-etbn-hub-v1-dot-model-signifier-297723.uc.r.appspot.com/api/v1/auth/login"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/auth/login"
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/auth/login"


        //val requestBody = "username=orgadmin@admin.com&password=123321"

        /************************************************************/

        val paramlist:List<Pair<String,Any?>>
        val hMap: MutableMap<String, Any?> = HashMap()

        hMap["DeviceID"] = pdeviceid
        hMap["DeviceName"] = pdevicename
        hMap["MACAddress"] = pmacdadd

        paramlist=hMap.toList();

        //val requestBody = "username=$pusername&password=$ppassword&DeviceID=$pdeviceid&DeviceName=$pdevicename&MACAddress=$pmacdadd"
        val requestBody = "username=$pusername&password=$ppassword&DeviceID=$pdeviceid&DeviceName=$pdevicename&MACAddress=$pmacdadd&DeviceInfo=$paramlist"
        //val requestBody = "username=$pusername&password=$ppassword&DeviceID=did&DeviceName=dname&MACAddress=macadd"

        /************************************************************/
        val stringReq: StringRequest =
                object : StringRequest(Method.POST, url,
                        Response.Listener<String> { response ->

                            var strResp = response.toString()
                            val gson = GsonBuilder().create()

                            val ParamAuthenticatedUser: AuthenticatedUser = gson.fromJson(strResp, AuthenticatedUser::class.java)

                            volleyreslisner.onResponse(ParamAuthenticatedUser)
                        },
                        Response.ErrorListener { error ->
                            if(error.networkResponse.statusCode== 409) {
                                volleyreslisner.onError("exceeded")
                            }
                            else
                            {
                                volleyreslisner.onError(error.networkResponse.statusCode.toString())
                            }
                        }
                ) {
                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }
        queue.add(stringReq)
    }

    fun  getLatestVersion(pcontext: Context,volleyreslisner: LoginDataSource.VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/app-versions/mobile"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/app-versions/mobile"

        val stringReq: StringRequest =
            object : StringRequest(Method.GET, url,
                Response.Listener<String> { response ->

                    var strResp = response.toString()
                    val gson = GsonBuilder().create()

                    val PLatestVersionInfo: LatestVersionInfo = gson.fromJson(strResp, LatestVersionInfo::class.java)

                    volleyreslisner.onResponse(PLatestVersionInfo)
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

    fun logout() {
        // TODO: revoke authentication

    }

    fun  login_(pusername: String, ppassword: String,pdeviceid:String,pdevicename:String,pmacdadd:String, pcontext: Context, volleyreslisner:VolleyResponseListener)
    {
        val queue = Volley.newRequestQueue(pcontext)
//        val url = "https://prothumia-etbn-hub-v1-dot-model-signifier-297723.uc.r.appspot.com/api/v1/licenses/user-collection/:"
        //val url = "https://prothumia-hub-dev-dot-model-signifier-297723.uc.r.appspot.com/api/v1/auth/login"
        val url = "https://prothumia-etbn-hub-v1-5-dot-model-signifier-297723.uc.r.appspot.com/api/v1/auth/login"

        val requestBody ="\"username\":\"orgadmin@admin.com\",\"password\":\"123321\""
        val jarrequest =  object: JsonArrayRequest(Request.Method.POST, url,null,
            Response.Listener<JSONArray> { response ->

                val gson = GsonBuilder().create()
                val CollectionArray: Array<PublicationCollections> = gson.fromJson(response.toString(), Array<PublicationCollections>::class.java)

                volleyreslisner.onResponse(CollectionArray)
            },
            Response.ErrorListener {
                volleyreslisner.onError("Error Occured")
            })
        {
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = java.util.HashMap<String, String>()
//
//                return headers
//            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }

        queue.add(jarrequest)
    }
}