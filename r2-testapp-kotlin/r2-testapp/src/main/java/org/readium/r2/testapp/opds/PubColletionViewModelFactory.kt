package org.readium.r2.testapp.opds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.readium.r2.testapp.data.PubCollectionDataSource
import org.readium.r2.testapp.data.PubCollectionRepository


class PubColletionViewModelFactory : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PubColletctionViewModel::class.java)) {
            return PubColletctionViewModel(
                    pubcollectionRepository = PubCollectionRepository(
                            pubcolletiondataSource = PubCollectionDataSource()
                    )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}