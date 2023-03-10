package com.example.suitup.main.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suitup.common.EventObserver
import com.example.suitup.main.data.model.Store
import com.example.suitup.main.data.model.yelp.YelpReview
import com.example.suitup.main.ui.details.adapter.DetailsAdapter
import com.example.suitup.main.ui.details.adapter.DetailsOnClickListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import suitup.R
import suitup.databinding.FragmentDetailsBinding

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private var storeId: String? = null
    private val viewModel: DetailsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val bundle = arguments?.let { DetailsFragmentArgs.fromBundle(it) }
        storeId = bundle?.storeId
        val sideEffectsObserver = EventObserver<DetailsSideEffects> {
            handleSideEffect(it)
        }
        val detailsUiStateObserver = Observer<DetailsUiState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.detailsRv.alpha = 0.3f
            } else {
                binding.progressBar.visibility = View.GONE
                binding.detailsRv.alpha = 1.0f
                bindRv(
                    viewModel.detailsUiState.value?.store,
                    viewModel.detailsUiState.value?.reviews
                )
                Picasso.get().load(viewModel.detailsUiState.value?.store?.image_url).into(binding.toolbarIv)
                binding.myToolbar.title = viewModel.detailsUiState.value?.store?.name
            }
        }
        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        viewModel.detailsUiState.observe(viewLifecycleOwner, detailsUiStateObserver)
        return binding.root
    }

    private fun handleSideEffect(sideEffect: DetailsSideEffects) {
        when (sideEffect) {
            is DetailsSideEffects.NavigateToPhoto -> PhotoFragment(sideEffect.photo).show(
                childFragmentManager, PhotoFragment.TAG
            )
            is DetailsSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
            is DetailsSideEffects.GoToPhotoSearch ->
                findNavController().navigate(R.id.action_detailsFragment_to_photoSearchFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.action(DetailsIntent.GetData(storeId))
        bindToolbar()
    }

    private fun bindRv(store: Store?, reviews: List<YelpReview>?) {
        val adapter = DetailsAdapter(store, reviews, DetailsOnClickListener({
            viewModel.action(DetailsIntent.AddToFavorites)
        },{
          viewModel.action(DetailsIntent.GoToPhotoSearch)
        }, {
            viewModel.action(DetailsIntent.OpenPhoto(it))
        }))
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.detailsRv.layoutManager = linearLayoutManager
        binding.detailsRv.adapter = adapter
    }

    private fun bindToolbar() {
        binding.myToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.myToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }
}