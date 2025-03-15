package com.project.travelcompanionapp.view.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.view.adapter.DestinationListAdapter
import com.project.travelcompanionapp.viewmodel.DestinationViewModel

class DestinationListActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var destinationAdapter: DestinationListAdapter
    private val viewModel: DestinationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_list)


        val backButton = findViewById<ImageView>(R.id.destinationListBackBtn)
        backButton.setOnClickListener {
            finish()
        }


        recyclerView = findViewById(R.id.recyclerViewDestinationList)
        recyclerView.layoutManager = LinearLayoutManager(this)


        viewModel.destinationList.observe(this) { destinationList ->
            destinationAdapter = DestinationListAdapter(destinationList)
            recyclerView.adapter = destinationAdapter
        }


        viewModel.fetchDestinationList()
    }
}
