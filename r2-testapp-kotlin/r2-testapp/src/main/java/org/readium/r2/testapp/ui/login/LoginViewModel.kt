package org.readium.r2.testapp.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import org.readium.r2.testapp.data.LoginRepository
import org.readium.r2.testapp.data.Result

import org.readium.r2.testapp.R
import org.readium.r2.testapp.data.PubCollectionRepository
import org.readium.r2.testapp.data.model.AuthenticatedUser
import org.readium.r2.testapp.data.model.LatestVersionInfo
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.opds.PubCollectionResult
import org.readium.r2.testapp.opds.PubCollectionUserView

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _versioninfoResult = MutableLiveData<VersioninfoResult>()
    val VersioninfoResult: LiveData<VersioninfoResult> = _versioninfoResult

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String,pdeviceid:String,pdevicename:String,pmaddadd:String,pcontext: Context) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password,pdeviceid,pdevicename,pmaddadd, pcontext, object: LoginRepository.VolleyResponseListenerRep {

            override fun onError(message: String) {
                //_loginResult.value = LoginResult(error = R.string.login_failed)
                _loginResult.value = LoginResult(error = message)
            }

            override fun onResponse(response: Any) {
                val responceinfo : AuthenticatedUser = response as AuthenticatedUser
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = responceinfo.getfirstName(),loggeduid = responceinfo.getid(),usertoken=responceinfo.gettoken(),orgname=responceinfo.getorganizationName(),orgid = responceinfo.getorganizationId()))
            }
        })
    }

    fun getLatestVersion(pcontext: Context) {
        val result = loginRepository.getLatestVersion(pcontext,object: LoginRepository.VolleyResponseListenerRep {

            override fun onError(message: String) {
                _versioninfoResult.value = VersioninfoResult()
            }

            override fun onResponse(response: Any) {
                val responceinfo : LatestVersionInfo = response as LatestVersionInfo

                //_pubcollectionResult.value = PubCollectionResult(success = PubCollectionUserView(id = responceinfo.getcName(),title = responceinfo.getcName(),description = responceinfo.getcName()))
                _versioninfoResult.value = VersioninfoResult(success = LatestvertioninforUserView(responceinfo))
            }
        })
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        }
//        else if (!isPasswordValid(password)) {
//            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
//        }
        else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
//        return if(username.contains('@')) {
//            Patterns.EMAIL_ADDRESS.matcher(username).matches()
//        } else {
//            username.isNotBlank()
//        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}