package com.example.huzzler.ui.rewards.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.huzzler.R
import com.example.huzzler.data.model.Reward
import com.example.huzzler.databinding.ItemRewardBinding

class RewardAdapter(
    private val onRewardClick: (Reward) -> Unit
) : ListAdapter<Reward, RewardAdapter.RewardViewHolder>(RewardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val binding = ItemRewardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RewardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RewardViewHolder(
        private val binding: ItemRewardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reward: Reward) {
            binding.apply {
                tvRewardTitle.text = reward.title
                tvRewardDescription.text = reward.description
                tvPointsCost.text = "${reward.pointsCost} points"
                
                // Show popular badge
                tvPopular.visibility = if (reward.isPopular) View.VISIBLE else View.GONE
                
                // Show game tag if available
                if (reward.gameTag != null) {
                    tvGameTag.text = reward.gameTag
                    tvGameTag.visibility = View.VISIBLE
                } else {
                    tvGameTag.visibility = View.GONE
                }
                
                // Set reward image based on type (placeholders for now)
                when (reward.title) {
                    "Valorant Points" -> {
                        ivRewardImage.setImageResource(R.drawable.placeholder_valorant)
                    }
                    "MLBB Battle Pass" -> {
                        ivRewardImage.setImageResource(R.drawable.placeholder_mlbb)
                    }
                    else -> {
                        ivRewardImage.setImageResource(R.drawable.placeholder_academic)
                    }
                }
                
                btnRedeem.setOnClickListener {
                    onRewardClick(reward)
                }
                
                root.setOnClickListener {
                    onRewardClick(reward)
                }
            }
        }
    }

    private class RewardDiffCallback : DiffUtil.ItemCallback<Reward>() {
        override fun areItemsTheSame(oldItem: Reward, newItem: Reward): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reward, newItem: Reward): Boolean {
            return oldItem == newItem
        }
    }
}
