package com.haripm.encryptionkotlinutility.activities

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.haripm.encryptionkotlinutility.R
import com.haripm.encryptionkotlinutility.model.PersonDetailsModel
import kotlinx.android.synthetic.main.activity_person_details.*

class PersonDetailsActivity : Activity() {

    private var personDetailModel: PersonDetailsModel = PersonDetailsModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_details)

        getAllWidgets()
        getDataFromIntent()
        setDataToView()
    }

    private fun setDataToView() {
        tvPersonDetailID.setText(personDetailModel.id.toString())
        tvPersonDetailName.setText(personDetailModel.name)
        tvPersonDetailEmail.setText(personDetailModel.email)
        tvPersonDetailAddress.setText(personDetailModel.address)
        tvPersonDetailAge.setText(personDetailModel.age)
    }

    private fun getDataFromIntent() {
        var personID: Int = intent.getIntExtra("PersonId", -1)
        personDetailModel = RealmEncryptionActivity.getInstance().searchPerson(personID)!!
    }

    private fun getAllWidgets() {
        var tvPersonDetailID: TextView = findViewById(R.id.tvPersonDetailID)
        var tvPersonDetailName: TextView = findViewById(R.id.tvPersonDetailName)
        var tvPersonDetailEmail: TextView = findViewById(R.id.tvPersonDetailEmail)
        var tvPersonDetailAddress: TextView = findViewById(R.id.tvPersonDetailAddress)
        var tvPersonDetailAge: TextView = findViewById(R.id.tvPersonDetailAge)
    }
}