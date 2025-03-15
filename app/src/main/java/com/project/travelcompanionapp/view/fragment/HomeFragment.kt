package com.project.travelcompanionapp.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.databinding.FragmentHomeBinding
import com.project.travelcompanionapp.view.activity.DestinationListActivity
import com.project.travelcompanionapp.view.activity.SignInActivity
import com.project.travelcompanionapp.view.adapter.PopularAdapter
import com.project.travelcompanionapp.view.adapter.BannerSliderAdapter
import com.google.firebase.auth.FirebaseAuth
import com.project.travelcompanionapp.model.BannerSliderModel
import com.project.travelcompanionapp.view.adapter.SearchAdapter
import com.project.travelcompanionapp.viewmodel.BannerViewModel
import com.project.travelcompanionapp.viewmodel.PopularViewModel
import com.project.travelcompanionapp.viewmodel.SearchViewModel

class HomeFragment : Fragment() {
    interface HomeFragmentListener {

        fun onSeeMoreClicked()
    }

    private var listener: HomeFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentListener) {
            listener = context
        }
    }

    private lateinit var popularViewModel: PopularViewModel
    private lateinit var bannerViewModel: BannerViewModel
    private lateinit var searchViewModel: SearchViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popularViewModel = ViewModelProvider(this)[PopularViewModel::class.java]
        bannerViewModel = ViewModelProvider(this)[BannerViewModel::class.java]
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        firebaseAuth = FirebaseAuth.getInstance()

        initSearch()
        initBanner()
        initPopular()

        binding.btnLogOut.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        view.findViewById<TextView>(R.id.txtSeeMore).setOnClickListener {
            listener?.onSeeMoreClicked()
        }
        view.findViewById<TextView>(R.id.txtSeeAll).setOnClickListener {
            val intent = Intent(requireContext(), DestinationListActivity::class.java)
            startActivity(intent)
        }

        bannerViewModel.banners.observe(viewLifecycleOwner) { items ->
            banners(items)
        }

        bannerViewModel.loadBanners()
    }

    private fun initSearch() {

        searchViewModel.filteredDestinations.observe(viewLifecycleOwner) { results ->
            binding.recyclerViewSearchList.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewSearchList.adapter = SearchAdapter(results) { selectedItem ->
                binding.SearchViewField.setQuery(selectedItem, false)
                binding.recyclerViewSearchList.visibility = View.GONE

                hideKeyboard()
                binding.SearchViewField.clearFocus()

            }
        }
        binding.SearchViewField.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    binding.recyclerViewSearchList.visibility = View.GONE
                } else {
                    binding.recyclerViewSearchList.visibility = View.VISIBLE
                    searchViewModel.setSearchQuery(newText)
                }
                return true
            }
        })


        binding.SearchViewField.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) binding.recyclerViewSearchList.visibility = View.GONE
        }
        searchViewModel.setDestinations()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.SearchViewField.windowToken, 0)
    }


    private fun initPopular() {
        binding.progressBarRecommended.visibility = View.VISIBLE
        popularViewModel.popular.observe(viewLifecycleOwner) { popularList ->
            binding.recyclerRecommended.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerRecommended.adapter = PopularAdapter(popularList)
            binding.progressBarRecommended.visibility = View.GONE
        }
        popularViewModel.loadPopular()
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        bannerViewModel.banners.observe(viewLifecycleOwner) { bannerList ->
            binding.viewPagerSlider.adapter = BannerSliderAdapter(bannerList)
            binding.progressBarBanner.visibility = View.GONE

        }
        bannerViewModel.loadBanners()
    }

    private fun banners(items: List<BannerSliderModel>) {

        binding.viewPagerSlider.adapter = BannerSliderAdapter(items)
        binding.viewPagerSlider.clipToPadding = false
        binding.viewPagerSlider.clipChildren = false
        binding.viewPagerSlider.offscreenPageLimit = 3
        binding.viewPagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
