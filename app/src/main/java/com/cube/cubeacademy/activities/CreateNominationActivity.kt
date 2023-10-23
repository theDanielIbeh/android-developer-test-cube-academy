package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.cube.cubeacademy.R
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

    /**
     * Populates the form with data.
     * Add the logic to the buttons.
     */
    private fun populateUI() {
        populateNominees()
        setupEventListeners()
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.submitButton.setOnClickListener {
            createNomination()
        }
    }

    /**
     * Creates a new nomination.
     * Calls the function to navigate to the Nomination Submitted screen.
     * Resets the nomination model object.
     */
    private fun createNomination() {
        lifecycleScope.launch {
            try {
                viewModel.createNomination {
                    navigateToNominationSubmitted()
                    viewModel.resetNominationModel()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CreateNominationActivity,
                    getString(R.string.cannot_submit),
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    /**
     * Overrides the back button functionality.
     * Brings up an alert modal if any form field has been filled.
     */
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

    /**
     * Returns true if any of the form's fields has been filled.
     */
    private fun hasStartedFilling(): Boolean {
        val isNomineeSelected = viewModel.nominationModel.nomineeId.isNotBlank()
        val isReasonEntered = viewModel.nominationModel.reason.isNotBlank()
        val isProcessSelected = viewModel.nominationModel.process.isNotBlank()

        return isNomineeSelected || isReasonEntered || isProcessSelected
    }

    /**
     * Populates the nominee's dropdown with their first and last names
     */
    private fun populateNominees() {
//        Create a list of the first and last names of the nominees
        val nomineeList = mutableListOf<String>()
        lifecycleScope.launch {
            viewModel.nominees.collectLatest {
                it.forEach { nominee -> nomineeList.add(nominee.firstName + " " + nominee.lastName) }
//            Populate dropdown with the nominees' names
                binding.autoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        this@CreateNominationActivity,
                        R.layout.dropdown_item,
                        nomineeList
                    )
                )
            }
        }
    }

    /**
     * Navigates to the Navigation Submitted screen.
     */
    private fun navigateToNominationSubmitted() {
        val intent = Intent(this, NominationSubmittedActivity::class.java)
        startActivity(intent)
    }

    /**
     * Returns true if all fields have been filled.
     */
    private fun checkInputFields(): Boolean {
        val isNomineeSelected = viewModel.nominationModel.nomineeId.isNotBlank()
        val isReasonEntered = viewModel.nominationModel.reason.isNotBlank()
        val isProcessSelected = viewModel.nominationModel.process.isNotBlank()

        return isNomineeSelected && isReasonEntered && isProcessSelected
    }

    /**
     * Performs change detection in each field.
     * Updates the create button's enabled state.
     */
    private fun setupEventListeners() {
        val nomineeDropdown = binding.autoCompleteTextView
        val reasonTextField = binding.editTextTextMultiLine
        val processOptions = binding.radioGroup

        nomineeDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedNominee: Nominee = viewModel.nominees.value[position]
            viewModel.nominationModel.nomineeId = selectedNominee.nomineeId
            binding.submitButton.isEnabled = checkInputFields()
        }
        reasonTextField.doOnTextChanged { text, _, _, _ ->
            viewModel.nominationModel.reason = text.toString()
            binding.submitButton.isEnabled = checkInputFields()
        }
        processOptions.setOnCheckedChangeListener { group, checkedId ->
            val checkedOption = group.findViewById<RadioButton>(checkedId)
            viewModel.nominationModel.process = convertToSnakeCase(checkedOption.text.toString())
            binding.submitButton.isEnabled = checkInputFields()
        }
    }

    /**
     * Returns entered [text] in snake case.
     * @param text the text to be converted.
     */
    private fun convertToSnakeCase(text: String): String {
        var convertedText = ""
//        Convert any space to underscore and all letters to lowercase
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