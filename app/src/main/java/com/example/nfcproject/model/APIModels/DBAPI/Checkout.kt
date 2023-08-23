package com.example.nfcproject.model.APIModels.DBAPI

import java.util.Date
import java.util.UUID

data class Checkout(
    val checkoutId:UUID,
    val checkoutDateTime: Date?,
    val tagId:String,
    val studentId:String?,
    val employeId: String?
) {}