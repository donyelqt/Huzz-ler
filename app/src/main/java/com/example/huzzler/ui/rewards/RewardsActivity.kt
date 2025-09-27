package com.example.huzzler.ui.rewards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.huzzler.R
import com.example.huzzler.databinding.ActivityRewardsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, RewardsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RewardsFragment())
                .commit()
        }
    }
}
