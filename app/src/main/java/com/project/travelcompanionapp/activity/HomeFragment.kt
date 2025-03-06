package com.project.travelcompanionapp.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.adapter.PopularAdapter
import com.project.travelcompanionapp.adapter.SearchAdapter
import com.project.travelcompanionapp.adapter.SliderAdapter
import com.project.travelcompanionapp.model.SliderModel
import com.project.travelcompanionapp.viewmodel.MainViewModel
import com.project.travelcompanionapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    interface HomeFragmentListener {
        fun onSeeMoreClicked() // Function to notify MainActivity
    }

    private var listener: HomeFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentListener) {
            listener = context
        }
    }

    private val viewModel = MainViewModel()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBanner()
        initPopular()
        initSearch()

        view.findViewById<TextView>(R.id.txtSeeMore).setOnClickListener {
            listener?.onSeeMoreClicked() // Notify MainActivity
        }
    }

    private fun initSearch() {
        viewModel.filteredDestinations.observe(viewLifecycleOwner) { results ->
            binding.recyclerViewSearchList.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewSearchList.adapter = SearchAdapter(results) { selectedItem ->
                binding.SearchViewField.setQuery(selectedItem, false)
                binding.recyclerViewSearchList.visibility = View.GONE
                Toast.makeText(requireContext(), "$selectedItem Selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.SearchViewField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    binding.recyclerViewSearchList.visibility = View.GONE
                } else {
                    binding.recyclerViewSearchList.visibility = View.VISIBLE
                    viewModel.searchQuery.value = newText
                }
                return true
            }
        })

        binding.SearchViewField.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) binding.recyclerViewSearchList.visibility = View.GONE
        }

        viewModel.loadDestination()
    }

    private fun initPopular() {
        binding.progressBarRecommended.visibility = View.VISIBLE
        viewModel.popular.observe(viewLifecycleOwner) {
            binding.recyclerRecommended.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerRecommended.adapter = PopularAdapter(it)
            binding.progressBarRecommended.visibility = View.GONE
        }
        viewModel.loadPopular()
    }

    private fun banners(items: List<SliderModel>) {
        binding.viewPagerSlider.adapter = SliderAdapter(items)
        binding.viewPagerSlider.clipToPadding = false
        binding.viewPagerSlider.clipChildren = false
        binding.viewPagerSlider.offscreenPageLimit = 3
        binding.viewPagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer)
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.banners.observe(viewLifecycleOwner) { items ->
            banners(items)
            binding.progressBarBanner.visibility = View.GONE
        }
        viewModel.loadBanners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
