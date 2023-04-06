package com.example.androidapplicationproject.database

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapplicationproject.databinding.AddressItemBinding
import com.example.androidapplicationproject.mapUtil.Utils
import com.google.android.gms.maps.model.LatLng

class PropertyListAdapter(val propertyViewModel: PropertyViewModel, val isTenant:Boolean) : ListAdapter<PropertyTable, PropertyListAdapter.PropertyViewHolder>(DiffCallback)  {


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

    inner class PropertyViewHolder(private var binding: AddressItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        fun bind(property: PropertyTable) {
            binding.addressUserDesc.text = property.address
            binding.addressUserCityCountry.text = property.city+" "+property.country
            if(isTenant){
                binding.modifyBtn.visibility = View.GONE
            }else{
                binding.modifyBtn.setOnClickListener {
                    Log.d("Bind","Modify")
                }
            }
            binding.viewBtn.setOnClickListener {
                Log.d("Bind", "Click")
                Utils.startMaps(arrayListOf(LatLng(property.latitude, property.longitude)))
            }
            binding.shareBtn.setOnClickListener {
                Log.d("Bind", "share")
                Utils.shareLocation(property.city, property.latitude, property.longitude)
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