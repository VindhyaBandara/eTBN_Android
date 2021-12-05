package org.readium.r2.testapp.ui.login

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.readium.r2.testapp.CatalogActivity
import org.readium.r2.testapp.R
import android.provider.Settings
import android.R.attr.capitalize
import android.R.attr.launchMode
import android.os.Build
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import android.widget.*
import androidx.versionedparcelable.CustomVersionedParcelable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import nl.komponents.kovenant.Kovenant.context
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.opds.OPDSListActivity
import org.readium.r2.testapp.opds.appContext
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var appsession: AppSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        appsession =AppSession(this@LoginActivity)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        CheckLatestAppVesion()

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
        })

        username.setOnFocusChangeListener { view, b ->
            if(username.text.toString() != "") {
                loginViewModel.loginDataChanged(
               username.text.toString(),
                password.text.toString()
                )
            }
        }
        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }
            /**********************************Get Device Information******************************************/
            val Deviceid: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            var Devicename: String

            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter != null) {
                Devicename = adapter.name
                //Devicename = adapter.address
            }
            else{
                var manufacturer: String? = Build.MANUFACTURER
                val model: String = Build.MODEL
                Devicename = manufacturer + model
            }

            val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = manager.connectionInfo
            val Macadd = info.macAddress
            /**********************************Get Device Information******************************************/
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                                username.text.toString(),
                                password.text.toString(),
                                Deviceid,
                                Devicename,
                                Macadd,
                                context
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE

                loginViewModel.login(username.text.toString(), password.text.toString(),Deviceid,Devicename,Macadd,context)
            }
        }
    }

    private fun CheckLatestAppVesion() : Int {
        loginViewModel.getLatestVersion(applicationContext)
        var retVal : Int = -1

        loginViewModel.VersioninfoResult.observe(this@LoginActivity, Observer {
            val versioninforesult = it ?: return@Observer

            if (versioninforesult.error != null) {
                val test= versioninforesult.error
            }
            if (versioninforesult.success != null) {
                val Lversion= versioninforesult.success.test.getversion()
                val CVersion = 1.7
                if(Lversion.toFloat() > CVersion.toFloat())
                {
                    val isMandotory : Boolean= versioninforesult.success.test.getisMandatory().toBoolean()
                    val setupfileURL : String = versioninforesult.success.test.url.toString();//https://etbn.global/apps/eTBN_1.6.apk
                    if(isMandotory)
                    {
                        alert(Appcompat, "eTBN needs an update. Please download and install the new version to proceed.") {
                            positiveButton("Download") {
                                it.dismiss()
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(setupfileURL))
                                startActivity(browserIntent)
                            }
                        }.build().apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                            show()
                        }
                    }
                    else
                    {
                        alert(Appcompat, "Update eTBN? eTBN recommends that you download and install the latest version.") {
                            positiveButton("Download") {
                                it.dismiss()
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(setupfileURL))
                                startActivity(browserIntent)
                            }
                            negativeButton("No Thanks") {
                                it.dismiss()
                                //cont.resume(false)
                            }
                        }.build().apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                            show()
                        }
                    }
                }
                else
                {
                    retVal = 0
                }
            }
        })
        return retVal
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience

        val toast = Toast.makeText(applicationContext,
                "$welcome $displayName", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL,0, 200)

        toast.show()
        /***************************Shared Pref**************************/
        //and now we set sharedpreference then use this like
        appsession.loggeduserid = model.loggeduid
        appsession.usertoken = model.usertoken
        appsession.orgname = model.orgname
        appsession.orgid = model.orgid
        /***************************Shared Pref**************************/

        val i = Intent(this@LoginActivity, CatalogActivity::class.java)
        i.putExtra ("loggeduid" , model.loggeduid)
        startActivity(i)
    }

    //private fun showLoginFailed(@StringRes errorString: Int) {
    private fun showLoginFailed(errorString: String) {

//        val toast = Toast.makeText(applicationContext,
//                errorString, Toast.LENGTH_LONG)
//        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL,0, 180)
//        toast.show()

        alert(Appcompat, errorString) {
            positiveButton("OK") {
                it.dismiss()
            }
        }.build().apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}