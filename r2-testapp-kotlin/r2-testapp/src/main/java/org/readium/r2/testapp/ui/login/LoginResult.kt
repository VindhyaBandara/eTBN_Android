package org.readium.r2.testapp.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: LoggedInUserView? = null,
        //val error: Int? = null
        val error: String? = null
)