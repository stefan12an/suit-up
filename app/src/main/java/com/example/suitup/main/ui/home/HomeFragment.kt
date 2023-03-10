package com.example.suitup.main.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suitup.common.EventObserver
import com.example.suitup.main.ui.home.adapter.HomeParentOnClickListener
import com.example.suitup.main.ui.home.adapter.ParentAdapter
import dagger.hilt.android.AndroidEntryPoint
import suitup.MainNavGraphDirections
import suitup.R
import suitup.databinding.FragmentHomeBinding


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<HomeSideEffects> {
            handleSideEffect(it)
        }
        val homeUiStateObserver = Observer<HomeUiState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.parentHomeRecyclerView.alpha = 0.3F
            } else {
                binding.progressBar.visibility = View.GONE
                binding.parentHomeRecyclerView.alpha = 1F
                bindRv(requireContext())
            }
        }

        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        viewModel.homeUiState.observe(viewLifecycleOwner, homeUiStateObserver)

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.action(HomeIntent.GetSearchResult(binding.searchBar.query))
                return false
            }
        })
        binding.takePhoto.setOnClickListener { viewModel.action(HomeIntent.GoToPhotoSearch) }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.action(HomeIntent.GetData)
    }

    private fun handleSideEffect(sideEffect: HomeSideEffects) {
        when (sideEffect) {
            is HomeSideEffects.NavigateToSeeAll -> findNavController().navigate(sideEffect.directions)
            is HomeSideEffects.NavigateToDetails -> findNavController().navigate(
                MainNavGraphDirections.moveToDetailsFragment(sideEffect.storeId)
            )
            is HomeSideEffects.GoToPhotoSearch ->
                findNavController().navigate(R.id.action_homeFragment_to_photoSearchFragment)
            is HomeSideEffects.NavigateToRequest -> findNavController().navigate(R.id.action_homeFragment_to_requestAccesFragment)
            is HomeSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindRv(context: Context) {
        val listOfStores = listOf(
            viewModel.homeUiState.value?.storesNearBy ?: emptyList(),
            viewModel.homeUiState.value?.storesHotNew ?: emptyList(),
            viewModel.homeUiState.value?.storesDeals ?: emptyList()
        )
        val parentAdapter =
            ParentAdapter(listOfStores, HomeParentOnClickListener({ direction ->
                viewModel.action(HomeIntent.SeeAll(direction))
            }, {
                viewModel.action(HomeIntent.OpenDetails(it))
            }))
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.parentHomeRecyclerView.adapter = parentAdapter
        binding.parentHomeRecyclerView.layoutManager = linearLayoutManager
    }
}