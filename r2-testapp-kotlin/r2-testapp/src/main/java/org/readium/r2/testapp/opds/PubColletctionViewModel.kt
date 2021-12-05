package org.readium.r2.testapp.opds

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.readium.r2.streamer.container.ContainerError
import org.readium.r2.testapp.data.LoginRepository
import org.readium.r2.testapp.data.PubCollectionRepository
import org.readium.r2.testapp.data.model.LatestVersionInfo
import org.readium.r2.testapp.data.model.PublicationCollections
import org.readium.r2.testapp.data.model.PublicationInfo
import org.readium.r2.testapp.ui.login.LatestvertioninforUserView
import org.readium.r2.testapp.ui.login.VersioninfoResult


class PubColletctionViewModel(private val pubcollectionRepository: PubCollectionRepository): ViewModel() {

    private val _pubcollectionResult = MutableLiveData<PubCollectionResult>()
    val CollectionResult: LiveData<PubCollectionResult> = _pubcollectionResult

    private val _publicationinfoResult = MutableLiveData<PublicationInfoResult>()
    val PublicationInfoResult: LiveData<PublicationInfoResult> = _publicationinfoResult

    fun getpubcollectionlist(loggeduserid: String,orgID:String, pcontext: Context,usertoken:String) {
        // can be launched in a separate asynchronous job
        val result = pubcollectionRepository.getcollectionlist(loggeduserid,orgID, pcontext,usertoken, object: PubCollectionRepository.VolleyResponseListenerRep {

            override fun onError(message: String) {
                _pubcollectionResult.value = PubCollectionResult(error = 1)
            }

            override fun onResponse(response: Any) {
                val responceinfo : Array<PublicationCollections> = response as Array<PublicationCollections>

                //_pubcollectionResult.value = PubCollectionResult(success = PubCollectionUserView(id = responceinfo.getcName(),title = responceinfo.getcName(),description = responceinfo.getcName()))
                _pubcollectionResult.value = PubCollectionResult(success = PubCollectionUserView(responceinfo))
            }
        })
    }

    fun getPublicationInfo(uuid:String,pcontext: Context) {
        val result = pubcollectionRepository.getPublicationinfo(uuid,pcontext,object: PubCollectionRepository.VolleyResponseListenerRep {

            override fun onError(message: String) {
                _publicationinfoResult.value = PublicationInfoResult()
            }

            override fun onResponse(response: Any) {
                val responceinfo : PublicationInfo = response as PublicationInfo

                //_pubcollectionResult.value = PubCollectionResult(success = PubCollectionUserView(id = responceinfo.getcName(),title = responceinfo.getcName(),description = responceinfo.getcName()))
                _publicationinfoResult.value = PublicationInfoResult(success = PubInfoUserView(responceinfo))
            }
        })
    }
}