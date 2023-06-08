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
import com.example.androidapplicationproject.databinding.LandlordItemBinding
import com.example.androidapplicationproject.Util.Utils
import com.google.android.gms.maps.model.LatLng
import net.cachapa.expandablelayout.ExpandableLayout


class LandlordListAdapter()
    : ListAdapter<UserTable, LandlordListAdapter.LandlordViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandlordViewHolder {
        return LandlordViewHolder(
            LandlordItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )

        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: LandlordViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class LandlordViewHolder(private var binding: LandlordItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        fun bind(user: UserTable) {
            binding.landlordName.text = "Landlord ${user.userName}"
            binding.getInfoBtn.setOnClickListener{
                Log.d("Landlord", "expand")
                Utils.startLandlordPage(user.userId)
            }

        }

    }
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<UserTable>() {
            override fun areItemsTheSame(oldItem: UserTable, newItem: UserTable): Boolean {
                return oldItem === newItem
            }
            override fun areContentsTheSame(oldItem: UserTable, newItem: UserTable): Boolean {
                return oldItem.userId == newItem.userId
            }
        }
    }
}