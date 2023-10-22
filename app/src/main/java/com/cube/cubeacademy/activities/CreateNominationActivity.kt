package com.cube.cubeacademy.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.R
import com.cube.cubeacademy.databinding.ActivityCreateNominationBinding
import com.cube.cubeacademy.fragments.AlertModalFragment
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nominee
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CreateNominationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNominationBinding

    @Inject
    lateinit var repository: Repository

    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModel.MainViewModelFactory
    private var alertModalFragment = AlertModalFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNominationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = this.let {
            MainViewModel.MainViewModelFactory(
                application = it.application,
                repository = repository
            )
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]


        populateUI()
        setupEditableFields()
        observeLiveData()
        binding.backButton.setOnClickListener {
            if (hasStartedFilling()) {
                alertModalFragment.show(supportFragmentManager, "AlertModalFragment")
                return@setOnClickListener
            }
            navigateUpTo(Intent(this, MainActivity::class.java))
        }
        binding.submitButton.setOnClickListener {
            viewModel.createNomination {
                navigateToNominationSubmitted();
                viewModel.resetNominationModel()
            }
        }
    }

    private fun populateUI() {
        /**
         * TODO: Populate the form after having added the views to the xml file (Look for TODO comments in the xml file)
         * 		 Add the logic for the views and at the end, add the logic to create the new nomination using the api
         * 		 The nominees drop down list items should come from the api (By fetching the nominee list)
         */
        populateNominees()
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
        viewModel.nominees.observe(
            this
        ) {
            it?.forEach { nominee -> nomineeList.add(nominee.firstName + " " + nominee.lastName) }
//            Populate recyclerview with the nominees' names
            binding.autoCompleteTextView.setAdapter(
                ArrayAdapter(
                    this,
                    com.cube.cubeacademy.R.layout.dropdown_item,
                    nomineeList
                )
            )
        }
    }

    /**
     *
     */
    private fun navigateToNominationSubmitted() {
        val intent = Intent(this, NominationSubmittedActivity::class.java)
        startActivity(intent)
    }

    private fun observeLiveData() {
        viewModel.nominationModelLive.observe(this) {
            val isNomineeSelected = it.nomineeId.isNotBlank()
            val isReasonEntered = it.reason.isNotBlank()
            val isProcessSelected = it.process.isNotBlank()

            binding.submitButton.isEnabled =
                isNomineeSelected && isReasonEntered && isProcessSelected
        }
    }

    private fun setupEditableFields() {
        val nominee = binding.autoCompleteTextView
        val reason = binding.editTextTextMultiLine
        val process = binding.radioGroup

        nominee.setOnItemClickListener { _, _, position, _ ->
            val selectedNominee: Nominee? = viewModel.nominees.value?.get(position)
            viewModel.nominationModel.nomineeId = (selectedNominee?.nomineeId ?: "").toString()
            viewModel.updateNominationModelLive()
        }
        reason.doOnTextChanged { text, _, _, _ ->
            viewModel.nominationModel.reason = text.toString()
            viewModel.updateNominationModelLive()
        }
        process.setOnCheckedChangeListener { group, checkedId ->
            val checkedProcess = group.findViewById<RadioButton>(checkedId)
            viewModel.nominationModel.process = convertToSnakeCase(checkedProcess.text)
            viewModel.updateNominationModelLive()
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