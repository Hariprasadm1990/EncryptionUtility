package com.haripm.encryptionkotlinutility.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import com.haripm.encryptionkotlinutility.R
import com.haripm.encryptionkotlinutility.adapter.PersonDetailsAdapter
import com.haripm.encryptionkotlinutility.model.PersonDetailsModel
import com.haripm.encryptionkotlinutility.utils.StringUtils
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.security.SecureRandom

class RealmEncryptionActivity : AppCompatActivity() {

    private var fabAddPerson: FloatingActionButton? = null
    private var entryList: ListView? = null
    private lateinit var subDialog: AlertDialog.Builder
    private lateinit var myRealm: Realm

    companion object {
        fun getInstance(): RealmEncryptionActivity {
            return instance!!
        }

        private var id: Int = 1
        private var dataList: ArrayList<PersonDetailsModel> = ArrayList<PersonDetailsModel>()
        private lateinit var instance: RealmEncryptionActivity
        private lateinit var mPersonDetailsAdapter: PersonDetailsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realm_encryption)
        val realmConfiguration = RealmConfiguration.Builder()
            .name("hello.realm")
            .schemaVersion(0)
            .encryptionKey(StringUtils.generateEncryptionKey())
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        myRealm = Realm.getInstance(realmConfiguration)
        instance = this
        getAllWidgets();
        bindWidgetsWithEvents()


        mPersonDetailsAdapter =
                PersonDetailsAdapter(this@RealmEncryptionActivity, dataList as ArrayList<PersonDetailsModel>)
        entryList?.adapter = mPersonDetailsAdapter

    }

    private fun bindWidgetsWithEvents() {
        fabAddPerson?.setOnClickListener { addOrUpdatePersonDetailsDialog(null, -1) }

        entryList?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent: Intent = Intent(this@RealmEncryptionActivity, PersonDetailsActivity::class.java)
            intent.putExtra("PersonId", dataList[position].id)
            startActivity(intent)
        }
    }

    fun addOrUpdatePersonDetailsDialog(model: PersonDetailsModel?, position: Int) {

        //dialog for incomplete details
        subDialog = AlertDialog.Builder(this@RealmEncryptionActivity)
            .setMessage("Please enter all the details!!!")
            .setCancelable(false)
            .setPositiveButton("Ok") { dlg2, which -> dlg2.cancel() }

        //set Main information entry dialog
        val layoutInflater = LayoutInflater.from(this@RealmEncryptionActivity)
        var promptView = layoutInflater.inflate(R.layout.prompt_dialog, null)
        val mainDialog = AlertDialog.Builder(this@RealmEncryptionActivity)
        mainDialog.setView(promptView)

        val etAddPersonName = promptView.findViewById(R.id.etAddPersonName) as EditText
        val etAddPersonEmail = promptView.findViewById(R.id.etAddPersonEmail) as EditText
        val etAddPersonAddress = promptView.findViewById(R.id.etAddPersonAddress) as EditText
        val etAddPersonAge = promptView.findViewById(R.id.etAddPersonAge) as EditText

        if (model != null) {
            etAddPersonName.setText(model.name)
            etAddPersonEmail.setText(model.email)
            etAddPersonAddress.setText(model.address)
            etAddPersonAge.setText(model.age)
        }

        mainDialog.setCancelable(false)
            .setNegativeButton("Cancel") { dlg, which -> dlg.cancel() }

        mainDialog.setPositiveButton("ok") { dialog, which ->
            if (!etAddPersonName.text.trim().isBlank() && !etAddPersonEmail.text.trim().isBlank()
                && !etAddPersonAddress.text.trim().isBlank() && !etAddPersonAge.text.trim().isBlank()
            ) {

                var personName: String = etAddPersonName.text.toString()
                var personEmail: String = etAddPersonEmail.text.toString()
                var personAddress: String = etAddPersonAddress.text.toString()
                var personAge: String = etAddPersonAge.text.toString()

                var dataModel = PersonDetailsModel()
                dataModel.name = personName
                dataModel.email = personEmail
                dataModel.address = personAddress
                dataModel.age = personAge

                if (model == null) {
                    addDataToRealm(dataModel)
                } else {
                    updatePersonDetails(dataModel, position, model.id)
                }
            } else {
                subDialog.show()
            }
        }
        val dialog: AlertDialog = mainDialog.create()
        dialog.show()

    }

    private fun addDataToRealm(dataModel: PersonDetailsModel) {
        myRealm.beginTransaction()
        val personDetailsModel = myRealm.createObject(PersonDetailsModel::class.java, id)
        personDetailsModel.name = dataModel.name
        personDetailsModel.email = dataModel.email
        personDetailsModel.address = dataModel.address
        personDetailsModel.age = dataModel.age
        dataList.add(personDetailsModel)
        myRealm.commitTransaction()
        mPersonDetailsAdapter!!.notifyDataSetChanged()
        id++
    }

    fun updatePersonDetails(dataModel: PersonDetailsModel, position: Int, personId: Int) {


        val editPersonDetails = myRealm.where(PersonDetailsModel::class.java).equalTo("id", personId).findFirst()
        myRealm.beginTransaction()
        editPersonDetails!!.name = dataModel.name
        editPersonDetails.email = dataModel.email
        editPersonDetails.address = dataModel.address
        editPersonDetails.age = dataModel.age
        myRealm.commitTransaction()

        dataList.set(position, editPersonDetails)
        mPersonDetailsAdapter!!.notifyDataSetChanged()

    }

    fun searchPerson(personId: Int): PersonDetailsModel? {
        val results: RealmResults<PersonDetailsModel> =
            myRealm.where(PersonDetailsModel::class.java).equalTo("id", personId).findAll()
        myRealm.beginTransaction()
        myRealm.commitTransaction()

        return results[0]
    }

    fun deletePerson(personId: Int, position: Int) {
        val results: RealmResults<PersonDetailsModel> =
            myRealm.where(PersonDetailsModel::class.java).equalTo("id", personId).findAll()
        myRealm.beginTransaction()
        results.deleteFromRealm(0)
        myRealm.commitTransaction()
        dataList.removeAt(position)
        mPersonDetailsAdapter!!.notifyDataSetChanged()
    }


    private fun getAllWidgets() {
        fabAddPerson = findViewById<FloatingActionButton>(R.id.fabAddPerson)
        entryList = findViewById<ListView>(R.id.listView)
    }
}