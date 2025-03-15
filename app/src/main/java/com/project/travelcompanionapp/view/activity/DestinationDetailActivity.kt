package com.project.travelcompanionapp.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.project.travelcompanionapp.view.adapter.DestinationSliderAdapter
import com.project.travelcompanionapp.databinding.ActivityDestinationDetailBinding
import com.project.travelcompanionapp.model.ItemModel
import com.project.travelcompanionapp.viewmodel.DestinationViewModel

class DestinationDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDestinationDetailBinding
    private var item: ItemModel? = null
    private val viewModel: DestinationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDestinationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchIntentData()
        observeDestinationDetails()
        initDestinationImage()
    }


    private fun fetchIntentData() {
        val destinationTitle = intent.getStringExtra("destination_title") ?: ""
        if (destinationTitle.isNotEmpty()) {
            viewModel.fetchDestinationDetails(destinationTitle)
        }
    }


    private fun observeDestinationDetails() {
        viewModel.destinationDetails.observe(this) { destination ->
            destination?.let {
                item = it
                initData()
                destinationImage(destination.images)
            }
        }
    }


    private fun initData() {
        item?.let {
            binding.apply {
                titleTxt.text = it.title
                addressTxt.text = it.address
                descriptionTxt.text = it.description
                ratingBar.rating = it.score.toFloat()
                ratingTxt.text = it.score.toString()


                backBtn.setOnClickListener {
                    finish()
                }
            }
        }
    }

    private fun destinationImage(images: List<String>) {
        binding.destinationImageSlider.adapter = DestinationSliderAdapter(images)
        binding.destinationImageSlider.clipToPadding = false
        binding.destinationImageSlider.clipChildren = false
        binding.destinationImageSlider.offscreenPageLimit = 3
        binding.destinationImageSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.destinationImageSlider.setPageTransformer(compositePageTransformer)
    }


    private fun initDestinationImage() {
        viewModel.destinationDetails.observe(this) { destination ->
            destination?.let {
                destinationImage(it.images)
            }
        }
    }



}
