package com.haripm.encryptionkotlinutility.adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.haripm.encryptionkotlinutility.R
import com.haripm.encryptionkotlinutility.activities.RealmEncryptionActivity
import com.haripm.encryptionkotlinutility.model.PersonDetailsModel

public class PersonDetailsAdapter(context: Context, personDetailsArrayList: ArrayList<PersonDetailsModel>) : BaseAdapter() {

    private var mContext: Context
    private var mPersonDetailsArrayList: ArrayList<PersonDetailsModel>
    private var mInflater: LayoutInflater

    init {
        this.mContext = context
        this.mPersonDetailsArrayList = personDetailsArrayList
        mInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var v: View? = convertView
        val holder: Holder

        if (v == null) {
            v = mInflater.inflate(R.layout.inflate_list_item, null)
            holder = Holder()
            holder.tvPersonName = v.findViewById(R.id.tvPersonName) as TextView
            holder.ivEditPersonDetail = v.findViewById(R.id.ivEditPesonDetail) as ImageView
            holder.ivDeletePerson = v.findViewById(R.id.ivDeletePerson) as ImageView
            v.tag = holder
        } else {
            holder = v.tag as Holder
        }
        holder.tvPersonName.text = mPersonDetailsArrayList[position].name
        holder.ivEditPersonDetail.setOnClickListener {
            val dataToEditModel = RealmEncryptionActivity.getInstance().searchPerson(mPersonDetailsArrayList[position].id)
            RealmEncryptionActivity.getInstance().addOrUpdatePersonDetailsDialog(dataToEditModel, position)
        }

        holder.ivDeletePerson.setOnClickListener {
            deleteConfirmationDialog(mPersonDetailsArrayList[position].id, position)
        }

        return v
    }

    override fun getItem(position: Int): Any {
        return mPersonDetailsArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return mPersonDetailsArrayList.size
    }

    fun deleteConfirmationDialog(personId: Int, position: Int) {
        val deleteDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        deleteDialog.setCancelable(true)
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("ok") { dialog, which ->
                    RealmEncryptionActivity.getInstance().deletePerson(personId, position)
                }
        deleteDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = deleteDialog.create()
        alertDialog.show()
    }

    class Holder {
        lateinit var tvPersonName: TextView
        lateinit var ivDeletePerson: ImageView
        lateinit var ivEditPersonDetail: ImageView
    }
}