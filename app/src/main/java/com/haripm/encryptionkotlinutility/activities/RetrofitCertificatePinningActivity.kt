package com.haripm.encryptionkotlinutility.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.haripm.encryptionkotlinutility.R
import com.haripm.encryptionkotlinutility.adapter.AvengersAdapter
import com.haripm.encryptionkotlinutility.viewmodel.MyViewModel

class RetrofitCertificatePinningActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: AvengersAdapter
    lateinit var model: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit_certificate_pinning)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        model = ViewModelProviders.of(this@RetrofitCertificatePinningActivity).get(MyViewModel::class.java)
        model.getHeroes().observe(this, Observer { heroList ->
            adapter = AvengersAdapter(this@RetrofitCertificatePinningActivity, heroList!!)
            recyclerView.adapter = adapter
        })
    }
}