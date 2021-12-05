package org.readium.r2.testapp.opds

import org.readium.r2.testapp.opds.PubInfoUserView

data class PublicationInfoResult (
    val success: PubInfoUserView? = null,
    //val error: Int? = null
    val error: String? = null
        )
