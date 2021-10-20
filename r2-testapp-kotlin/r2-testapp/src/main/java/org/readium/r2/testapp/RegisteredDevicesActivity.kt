package org.readium.r2.testapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.activity_outline.*
import kotlinx.android.synthetic.main.activity_registered_devices.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.nestedScrollView
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.data.model.RegisteredDevices
import org.readium.r2.testapp.db.appContext
import org.readium.r2.testapp.opds.*
import org.readium.r2.testapp.ui.login.AppSession
import org.readium.r2.testapp.ui.login.LogoutActivity
import org.readium.r2.testapp.utils.RegdDevicesViewModel
import org.readium.r2.testapp.utils.RegdDevicesViewModelFactory


class RegisteredDevicesActivity : AppCompatActivity() {
    private lateinit var regeddevicesViewModel: RegdDevicesViewModel
    var deviceslist:Array<RegisteredDevices>? = null
    lateinit var appsession: AppSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        regeddevicesViewModel = ViewModelProvider(this@RegisteredDevicesActivity, RegdDevicesViewModelFactory())
            .get(RegdDevicesViewModel::class.java)

        var loggeduser:String= intent.getStringExtra("loggeduid").toString()

        val registeredrevicesActivity =  RegisteredDevicesActivity()
        registeredrevicesActivity.appsession = AppSession(this.applicationContext)
        var usertoken:String = registeredrevicesActivity.appsession.usertoken
        var orgname:String = registeredrevicesActivity.appsession.orgname
        var OrgID:String = registeredrevicesActivity.appsession.orgid
        registeredrevicesActivity.appsession.pagenumber = "1"

//        regeddevicesViewModel.getregeddevices(loggeduser,OrgID,this@RegisteredDevicesActivity,usertoken)
        regeddevicesViewModel.getregeddevices(loggeduser,OrgID,this@RegisteredDevicesActivity,usertoken)

        regeddevicesViewModel.DevicesResult.observe(this@RegisteredDevicesActivity, Observer {
            val deviceResult = it ?: return@Observer
            if (deviceResult.error != null) {
                val test:String=deviceResult.error.toString()
            }
            if (deviceResult.success != null) {
                val devicelist:Array<RegisteredDevices> = deviceResult.success.test
                deviceslist=devicelist
                val deviceAdapter = deviceslist?.toMutableList()?.let {DeviceListViewAdapter(this,it,this@RegisteredDevicesActivity,regeddevicesViewModel)}

                coordinatorLayout {
                    fitsSystemWindows = true
                    this.lparams(width = matchParent, height = matchParent)
                    padding = dip(10)

                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        //backgroundColor=R.color.colorAccent
                        weightSum = 0f
                        padding = dip(1)
                        this.lparams(width = matchParent, height = wrapContent)
                        //weightSum = 2f
                        textView {
                            text = orgname
                            textSize = 20f
                            paintFlags = Paint.UNDERLINE_TEXT_FLAG
                        }.setTypeface(null, Typeface.BOLD)
//                        view{
//                            layoutParams.height=1
//                            setBackgroundColor(Color.parseColor("#000000"))
//                        }textview.setPaintFlags(textview.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    }

                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        //backgroundColor=R.color.colorAccent
                        weightSum = 0f
                        padding = dip(30)
                        leftPadding = dip(2)
                        this.lparams(width = matchParent, height = wrapContent)
                        //weightSum = 2f
                        textView {
                            text = "Registered Devices"
                            textSize = 18f
                            textColor = (Color.parseColor("#8a8787"))
                            paintFlags = Paint.UNDERLINE_TEXT_FLAG

                        }.setTypeface(null, Typeface.BOLD);
                    }
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        //backgroundColor=R.color.colorAccent
                        weightSum = 0f
                        padding = dip(12)
                        this.lparams(width = matchParent, height = wrapContent)
                        //weightSum = 2f

                    }
                    nestedScrollView {
                        this.lparams(width = matchParent, height = matchParent)
                        linearLayout {
                            orientation = LinearLayout.VERTICAL
                            topPadding = 95
                            recyclerView {
                                layoutManager = LinearLayoutManager(this@RegisteredDevicesActivity)
                                (layoutManager as LinearLayoutManager).orientation = RecyclerView.VERTICAL
                                adapter = deviceAdapter
                            }
                        }
                    }
                }
            }
        })
        //val opdsAdapter = pubcollectionlist?.toMutableList()?.let { OPDSViewAdapter(this, it,this@RegisteredDevicesActivity) }


        setContentView(R.layout.activity_registered_devices)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.opds -> {
                val opdslistactivity =  OPDSListActivity()
                opdslistactivity.appsession = AppSession(appContext)
                val loggeduid = opdslistactivity.appsession.loggeduserid

                val i = Intent(this@RegisteredDevicesActivity, OPDSListActivity::class.java)
                i.putExtra ("loggeduid" , loggeduid)
                startActivity(i)
                this.finish()

                false
            }
            R.id.library -> {
                //startActivity(intentFor<LibraryActivity>())
                startActivity(intentFor<CatalogActivity>())
                this.finish()
                false
            }
            R.id.about -> {
                startActivity(intentFor<R2AboutActivity>())
                false
            }
            R.id.logout -> {
                startActivity(intentFor<LogoutActivity>())
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        startActivity(intentFor<CatalogActivity>())
        this.finish()
    }
}

private class DeviceListViewAdapter(private val activity: Activity, private val list: MutableList<RegisteredDevices>,private val contxt: Context,private val regeddevicesViewModel:RegdDevicesViewModel) : RecyclerView.Adapter<DeviceListViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.activity_registered_devices, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val regddevicesactivity =  RegisteredDevicesActivity()
        regddevicesactivity.appsession = AppSession(contxt)

        val usertoken = regddevicesactivity.appsession.usertoken

        //val database = OPDSDatabase(activity)
        val devicecollection = list[position]

        viewHolder.textView.text = "Device Name : " + devicecollection.deviceName
        viewHolder.did.text = "Device ID : " + devicecollection.deviceID
        viewHolder.dmodel.text = "Device Model : " + devicecollection.deviceModel
        val test= devicecollection.requestedForRemove
        viewHolder.button.text = "Forget Device"

        viewHolder.button.onClick {
            try {
                regeddevicesViewModel.requestfordelete(devicecollection.getdevid(),contxt,usertoken)

                (contxt as Activity).startActivity((contxt as Activity).intentFor<RegisteredDevicesActivity>())
                (contxt as Activity).finish()

                val toast = Toast.makeText(contxt as Activity,"The Process is completed.", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,0, 180)

                toast.show()
            } catch (e: Exception) {
                val test = e.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById<View>(R.id.device_name) as TextView
        val did: TextView = view.findViewById<View>(R.id.device_id) as TextView
        val dmodel: TextView = view.findViewById<View>(R.id.device_model) as TextView
        val button: Button = view.findViewById<View>(R.id.button) as Button
    }
}