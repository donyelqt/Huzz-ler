package com.example.huzzler.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.example.huzzler.R
import com.example.huzzler.databinding.DialogEditProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Modern Edit Profile Dialog - 2025 Best Practices
 * 
 * Features:
 * - Material Design 3 styling
 * - Real-time validation
 * - Smooth animations
 * - Keyboard handling
 */
class EditProfileDialog : DialogFragment() {

    private var _binding: DialogEditProfileBinding? = null
    private val binding get() = _binding!!
    
    private var currentName: String = ""
    private var onNameSaved: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupClickListeners()
        
        // Auto-focus and show keyboard
        binding.etName.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun setupViews() {
        binding.etName.setText(currentName)
        binding.etName.setSelection(currentName.length) // Cursor at end
        
        // Real-time validation
        binding.etName.doAfterTextChanged { text ->
            val name = text?.toString()?.trim() ?: ""
            binding.btnSave.isEnabled = name.isNotEmpty() && name.length >= 2
            
            // Show error if needed
            binding.tilName.error = when {
                name.isEmpty() -> "Name cannot be empty"
                name.length < 2 -> "Name must be at least 2 characters"
                else -> null
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            if (newName.isNotEmpty() && newName.length >= 2) {
                onNameSaved?.invoke(newName)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            currentName: String,
            onNameSaved: (String) -> Unit
        ): EditProfileDialog {
            return EditProfileDialog().apply {
                this.currentName = currentName
                this.onNameSaved = onNameSaved
            }
        }
    }
}
