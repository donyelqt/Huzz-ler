package com.example.huzzler.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huzzler.R
import com.example.huzzler.databinding.FragmentChatBinding
import com.example.huzzler.ui.chat.adapter.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private var typingAnimationJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupOverview()
        setupObservers()
        setupClickListeners()
        viewModel.loadInitialMessage()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupOverview() {
        binding.chipSuggestionBst.setOnClickListener {
            viewModel.sendMessage(getString(R.string.suggestion_bst))
        }
        binding.chipSuggestionPrime.setOnClickListener {
            viewModel.sendMessage(getString(R.string.suggestion_prime_window))
        }
        binding.chipSuggestionDeadlines.setOnClickListener {
            viewModel.sendMessage(getString(R.string.suggestion_deadlines))
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                if (messages.isNotEmpty()) {
                    binding.rvChat.scrollToPosition(messages.size - 1)
                }
            }
        }

        viewModel.isTyping.observe(viewLifecycleOwner, ::updateTypingIndicator)

        viewModel.overview.observe(viewLifecycleOwner) { overview ->
            binding.tvPrimeRateValue.text = "${overview.primeRate}%"
            binding.tvDayStreakValue.text = overview.dayStreak.toString()
            binding.tvPointsValue.text = "${overview.points}"
            binding.tvOnlineStatus.apply {
                text = if (overview.isOnline) getString(R.string.status_online) else getString(R.string.status_offline)
                visibility = View.VISIBLE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorMessageConsumed()
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSend.setOnClickListener {
                val message = etMessage.text.toString().trim()
                if (message.isNotEmpty()) {
                    viewModel.sendMessage(message)
                    etMessage.text?.clear()
                }
            }

            btnAttachment.setOnClickListener { anchor ->
                val popup = PopupMenu(requireContext(), anchor)
                popup.menuInflater.inflate(R.menu.menu_chat_actions, popup.menu)

                btnAttachment.setBackgroundResource(R.drawable.circle_red)
                btnAttachment.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_attach -> {
                            viewModel.handleFileAttachment()
                            true
                        }
                        R.id.action_camera -> {
                            viewModel.handleCameraCapture()
                            true
                        }
                        else -> false
                    }
                }

                popup.setOnDismissListener {
                    btnAttachment.setBackgroundResource(R.drawable.circle_gray)
                    btnAttachment.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray_dark)
                }

                popup.show()
            }

            // Navigation icon handled on toolbar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        typingAnimationJob?.cancel()
        typingAnimationJob = null
        _binding = null
    }

    private fun updateTypingIndicator(isTyping: Boolean) {
        if (isTyping) {
            binding.typingIndicatorContainer.visibility = View.VISIBLE
            typingAnimationJob?.cancel()
            val frames = listOf(
                getString(R.string.huzz_is_typing),
                getString(R.string.huzz_is_typing) + ".",
                getString(R.string.huzz_is_typing) + "..",
                getString(R.string.huzz_is_typing) + "..."
            )

            typingAnimationJob = viewLifecycleOwner.lifecycleScope.launch {
                var index = 0
                while (isActive) {
                    binding.tvTypingIndicator.text = frames[index % frames.size]
                    index++
                    delay(350L)
                }
            }
        } else {
            typingAnimationJob?.cancel()
            typingAnimationJob = null
            binding.typingIndicatorContainer.visibility = View.GONE
        }
    }
}
