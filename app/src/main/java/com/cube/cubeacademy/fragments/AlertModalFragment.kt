package com.cube.cubeacademy.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cube.cubeacademy.activities.MainActivity
import com.cube.cubeacademy.databinding.FragmentAlertModalBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlertModalFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAlertModalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentAlertModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.leavePageButton.setOnClickListener {
            dismiss()
            navigateToHome()
        }
    }

    /**
     * Navigates to the Home screen.
     */
    private fun navigateToHome() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        requireActivity().navigateUpTo(intent)
    }
}