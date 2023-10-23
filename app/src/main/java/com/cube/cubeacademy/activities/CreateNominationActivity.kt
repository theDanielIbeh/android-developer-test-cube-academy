package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.cube.cubeacademy.databinding.ActivityCreateNominationBinding
import com.cube.cubeacademy.fragments.AlertModalFragment
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nominee
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateNominationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNominationBinding

    @Inject
    lateinit var repository: Repository

    private val viewModel: MainViewModel by viewModels()
    private var alertModalFragment = AlertModalFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNominationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateUI()
        handleBackButton()
    }

    private fun populateUI() {
        /**
         * TODO: Populate the form after having added the views to the xml file (Look for TODO comments in the xml file)
         * 		 Add the logic for the views and at the end, add the logic to create the new nomination using the api
         * 		 The nominees drop down list items should come from the api (By fetching the nominee list)
         */
        populateNominees()
        setupEditableFields()
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.submitButton.setOnClickListener {
            viewModel.createNomination {
                navigateToNominationSubmitted()
                viewModel.resetNominationModel()
            }
        }
    }

    private fun handleBackButton() {
        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                if (hasStartedFilling()) {
                    alertModalFragment.show(supportFragmentManager, "AlertModalFragment")
                } else {
                    val intent = Intent(this@CreateNominationActivity, MainActivity::class.java)
                    navigateUpTo(intent)
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback
        )
    }

    private fun hasStartedFilling(): Boolean {
        val isNomineeSelected = viewModel.nominationModel.nomineeId.isNotBlank()
        val isReasonEntered = viewModel.nominationModel.reason.isNotBlank()
        val isProcessSelected = viewModel.nominationModel.process.isNotBlank()

        return isNomineeSelected || isReasonEntered || isProcessSelected
    }

    /**
     *
     */
    private fun populateNominees() {
//        Create a list of the first and last names of the nominees
        val nomineeList = mutableListOf<String>()
        lifecycleScope.launch {
            viewModel.nominees.collectLatest {
                it.forEach { nominee -> nomineeList.add(nominee.firstName + " " + nominee.lastName) }
//            Populate recyclerview with the nominees' names
                binding.autoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        this@CreateNominationActivity,
                        com.cube.cubeacademy.R.layout.dropdown_item,
                        nomineeList
                    )
                )
            }
        }
    }

    /**
     *
     */
    private fun navigateToNominationSubmitted() {
        val intent = Intent(this, NominationSubmittedActivity::class.java)
        startActivity(intent)
    }

    private fun checkInputFields(): Boolean {
        val isNomineeSelected = viewModel.nominationModel.nomineeId.isNotBlank()
        val isReasonEntered = viewModel.nominationModel.reason.isNotBlank()
        val isProcessSelected = viewModel.nominationModel.process.isNotBlank()

        return isNomineeSelected && isReasonEntered && isProcessSelected
    }

    private fun setupEditableFields() {
        val nominee = binding.autoCompleteTextView
        val reason = binding.editTextTextMultiLine
        val process = binding.radioGroup

        nominee.setOnItemClickListener { _, _, position, _ ->
            val selectedNominee: Nominee = viewModel.nominees.value[position]
            viewModel.nominationModel.nomineeId = selectedNominee.nomineeId
            binding.submitButton.isEnabled = checkInputFields()
        }
        reason.doOnTextChanged { text, _, _, _ ->
            viewModel.nominationModel.reason = text.toString()
            binding.submitButton.isEnabled = checkInputFields()
        }
        process.setOnCheckedChangeListener { group, checkedId ->
            val checkedProcess = group.findViewById<RadioButton>(checkedId)
            viewModel.nominationModel.process = convertToSnakeCase(checkedProcess.text)
            binding.submitButton.isEnabled = checkInputFields()
        }
    }

    private fun convertToSnakeCase(text: CharSequence): String {
        var convertedText = ""
        text.forEach { char ->
            convertedText += if (char.toString() == " ") {
                "_"
            } else {
                char.lowercase()
            }
        }
        return convertedText
    }
}