package org.readium.r2.testapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.readium.r2.testapp.data.RegedDevicesDataSource
import org.readium.r2.testapp.data.RegedDivicesRepository

class RegdDevicesViewModelFactory :ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegdDevicesViewModel::class.java)) {
            return RegdDevicesViewModel(
                regeddevicesRepository = RegedDivicesRepository(
                    regeddevicesdataSource = RegedDevicesDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}