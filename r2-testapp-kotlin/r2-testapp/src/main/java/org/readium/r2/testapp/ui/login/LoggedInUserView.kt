package org.readium.r2.testapp.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
        val displayName: String,
        val loggeduid:String,
        val usertoken: String,
        val orgname: String,
        val orgid: String
        //... other data fields that may be accessible to the UI
)