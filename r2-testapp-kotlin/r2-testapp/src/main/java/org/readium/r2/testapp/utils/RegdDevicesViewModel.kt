package org.readium.r2.testapp.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.readium.r2.streamer.container.ContainerError
import org.readium.r2.testapp.data.RegedDivicesRepository
import org.readium.r2.testapp.data.model.RegisteredDevices


class RegdDevicesViewModel(private val regeddevicesRepository: RegedDivicesRepository): ViewModel()  {

    private val _devicelistResult = MutableLiveData<DeviceListResult>()
    val DevicesResult: LiveData<DeviceListResult> =   _devicelistResult


    fun getregeddevices(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String) {
        // can be launched in a separate asynchronous job
        val result = regeddevicesRepository.getregeddevices(loggeduserid,orgID, pcontext,usertoken, object: RegedDivicesRepository.VolleyResponseListenerRep {

            override fun onError(message: String) {
                _devicelistResult.value = DeviceListResult(error = 1)
            }

            override fun onResponse(response: Any) {
                if(response is String)
                {
                    _devicelistResult.value = DeviceListResult(error = 1)
                }
                else {
                    val responceinfo: Array<RegisteredDevices> = response as Array<RegisteredDevices>
                    _devicelistResult.value = DeviceListResult(success = DevicesUserView(responceinfo))
                }

            }
        })
    }

    fun requestfordelete(id:String?, pcontext: Context,usertoken:String) {
        // can be launched in a separate asynchronous job
        val result = regeddevicesRepository.requestfordelete(id,pcontext,usertoken, object: RegedDivicesRepository.VolleyResponseListenerRep {

            override fun onError(message: String) {
                _devicelistResult.value = DeviceListResult(error = 1)
            }

            override fun onResponse(response: Any) {
                if(response is String) {
                    _devicelistResult.value = DeviceListResult(error = 1)
                }
                else
                {
                    val responceinfo: Array<RegisteredDevices> =response as Array<RegisteredDevices>
                    _devicelistResult.value = DeviceListResult(success = DevicesUserView(responceinfo))

                }
            }
        })
    }
}