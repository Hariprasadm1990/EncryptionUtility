package com.haripm.encryptionkotlinutility.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
 open class PersonDetailsModel() : RealmModel {
   @PrimaryKey
    var id: Int = 0

    var name: String? = null
    var email: String? = null
    var address: String? = null
    var age: String? = null
}