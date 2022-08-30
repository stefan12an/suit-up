package com.example.foodiezapp.main.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiezapp.common.Constants.ATTRIBUTES
import com.example.foodiezapp.databinding.HomeItemRowParentBinding
import com.example.foodiezapp.databinding.HomeSlideRowParentBinding
import com.example.foodiezapp.main.data.model.Restaurant
import com.example.foodiezapp.main.ui.home.HomeFragmentDirections
import com.google.android.material.tabs.TabLayoutMediator

private const val ITEM_PAGER = 0
private const val ITEM_RV = 1

class ParentAdapter(
    private val restaurantList: List<List<Restaurant>>,
    private val clickListener: HomeParentOnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val titleList = ATTRIBUTES.keys.toList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            val binding = HomeSlideRowParentBinding.inflate(inflater, parent, false)
            return ViewHolderOne(binding)
        }
        val binding = HomeItemRowParentBinding.inflate(inflater, parent, false)
        return ViewHolderTwo(binding)
    }

    override fun getItemViewType(position: Int): Int {
        if (titleList[position] == "") {
            return ITEM_PAGER
        }
        return ITEM_RV
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_PAGER -> {
                val viewHolder = holder as ViewHolderOne
                viewHolder.setItems(restaurantList[position], position)
            }
            ITEM_RV -> {
                val viewHolder = holder as ViewHolderTwo
                viewHolder.binding.childHomeSeeAll.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToSeeAllFragment(
                        ATTRIBUTES[titleList[position]]!!
                    )
                    clickListener.onSeeAllClick(action)
                }
                viewHolder.binding.childHomeTitle.text = titleList[position]
                viewHolder.setItems(restaurantList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return titleList.size
    }

    inner class ViewHolderOne(binding: HomeSlideRowParentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val adapter = ChildAdapter(HomeChildOnClickListener { clickListener.onClick(it) })

        init {
            binding.homePager.adapter = adapter
            TabLayoutMediator(binding.tabLayout, binding.homePager) { tab, _ ->
                tab.text = ""
            }.attach()

        }

        fun setItems(restaurantData: List<Restaurant>, position: Int) {
            adapter.setItems(restaurantData, position)
        }
    }

    inner class ViewHolderTwo(val binding: HomeItemRowParentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val adapter = ChildAdapter(HomeChildOnClickListener { clickListener.onClick(it) })

        init {
            binding.childHomeRecyclerView.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.childHomeRecyclerView.adapter = adapter
        }

        fun setItems(restaurantData: List<Restaurant>, position: Int) {
            adapter.setItems(restaurantData, position)
        }
    }
}

class HomeParentOnClickListener(val seeAllClickListener: (directions: NavDirections) -> Unit, val clickListener: (String) -> Unit) {
    fun onSeeAllClick(directions: NavDirections) = seeAllClickListener(directions)
    fun onClick(restaurantId: String) = clickListener(restaurantId)
}