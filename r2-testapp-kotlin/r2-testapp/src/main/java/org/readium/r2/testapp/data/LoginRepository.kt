package org.readium.r2.testapp.data

import android.content.Context
import org.readium.r2.testapp.data.model.AuthenticatedUser
import org.readium.r2.testapp.data.model.LatestVersionInfo
import org.readium.r2.testapp.data.model.LoggedInUser
import org.readium.r2.testapp.data.model.PublicationCollections

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    interface VolleyResponseListenerRep {
        fun onError(message:String)
        fun onResponse(response:Any)
    }

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set
    var authuser: AuthenticatedUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    var lversioninfo: LatestVersionInfo? = null
        private set

    fun login(username: String, password: String,pdeviceid:String,pdevicename:String,pmaddadd:String, pcontext: Context, volleyresponseListenerrep: VolleyResponseListenerRep) {
        // handle login
        dataSource.login(username, password,pdeviceid,pdevicename,pmaddadd, pcontext, object: LoginDataSource.VolleyResponseListener {

            override fun onError(message: String) {
                if(message=="401")
                {
                    volleyresponseListenerrep.onError("Invalid Credentials")
                }
//                else if(message=="409")
//                {
//                    volleyresponseListenerrep.onError("You have exceeded the maximum number of devices for your account. To add another device, log-in using an existing registered device, then go to Device Registration, then choose Forget Device. Then you may log-in with a new device. ")
//                }
                else
                {
                    volleyresponseListenerrep.onError("You have exceeded the maximum number of devices for your account. To add another device, log-in using an existing registered device, then go to Device Registration, then choose Forget Device. Then you may log-in with a new device. ")
                    //volleyresponseListenerrep.onError("Login failed")
                }

            }
            override fun onResponse(response: Any) {
                setLoggedInUser(response as AuthenticatedUser)
                volleyresponseListenerrep.onResponse(response)
            }
        })
    }

    fun getLatestVersion(pcontext: Context,volleyresponseListenerrep: LoginRepository.VolleyResponseListenerRep) {
        // handle login
        dataSource.getLatestVersion(pcontext,object: LoginDataSource.VolleyResponseListener {

            override fun onError(message: String) {
                volleyresponseListenerrep.onError(message)
            }
            override fun onResponse(response: Any) {
                //var collectionlist:Array<PublicationCollections> = response as Array<PublicationCollections>
                setVersionInfo(response as LatestVersionInfo)
                volleyresponseListenerrep.onResponse(response)
            }
        })
    }

    private fun setLoggedInUser(loggedInUser: AuthenticatedUser) {
        this.authuser = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private fun setVersionInfo(versioninfo: LatestVersionInfo) {
        this.lversioninfo = versioninfo
    }
}