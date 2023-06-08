package com.example.androidapplicationproject.database

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapplicationproject.databinding.AddressItemBinding
import com.example.androidapplicationproject.Util.Utils
import com.google.android.gms.maps.model.LatLng
import net.cachapa.expandablelayout.ExpandableLayout


class PropertyListAdapter(val propertyViewModel: PropertyViewModel, val isTenant:Boolean, val recyclerView:RecyclerView)
    : ListAdapter<PropertyTable, PropertyListAdapter.PropertyViewHolder>(DiffCallback){

    private val UNSELECTED = -1
    private var selectedItem = UNSELECTED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        return PropertyViewHolder(
            AddressItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )

        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val current = getItem(position)
//        holder.itemView.setOnClickListener {
//            onItemClicked(current)
//        }
        holder.bind(current)
    }

    inner class PropertyViewHolder(private var binding: AddressItemBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {

        //        lateinit var button: ImageButton
        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        fun bind(property: PropertyTable) {
//            button = binding.expandBtn
            binding.expandableLayout.setInterpolator(OvershootInterpolator())
            binding.expandableLayout.setOnExpansionUpdateListener(this)
            binding.expandBtn.setOnClickListener(this)
            binding.addressUserDesc.text = "${property.address}, ${property.postCode}"
            binding.addressUserCityCountry.text = property.city+" "+property.country
            binding.addressUserPrice.text = "$ ${property.price.toString()}"
            binding.viewBtn.setOnClickListener {
                Log.d("Bind", "Click")
                Utils.startMaps(arrayListOf(LatLng(property.latitude, property.longitude)),isTenant)
            }
            binding.shareBtn.setOnClickListener {
                Log.d("Bind", "share")
                Utils.shareLocation(property.city, property.latitude, property.longitude)
            }
            if(isTenant){
                binding.deleteBtn.visibility = View.GONE
            }else{
                binding.deleteBtn.setOnClickListener {
                    Utils.deleteProperty(property.latitude, property.longitude)
                }
            }
        }
        override fun onClick(view: View?) {
//            val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem)
            binding.expandBtn.isSelected = false
            binding.expandableLayout.collapse()

            val position = adapterPosition
            if (position == selectedItem) {
                selectedItem = UNSELECTED
            } else {
                binding.expandBtn.isSelected = true
                binding.expandableLayout.expand()
                selectedItem = position
            }
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            Log.d("ExpandableLayout", "State: $state" );
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(adapterPosition);
            }
        }
    }
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PropertyTable>() {
            override fun areItemsTheSame(oldItem: PropertyTable, newItem: PropertyTable): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: PropertyTable, newItem: PropertyTable): Boolean {
                return oldItem.propertyId == newItem.propertyId
            }
        }
    }
}