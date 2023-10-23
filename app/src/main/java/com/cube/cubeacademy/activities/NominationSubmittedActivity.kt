package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cube.cubeacademy.databinding.ActivityNominationSubmittedBinding

class NominationSubmittedActivity : AppCompatActivity() {
	private lateinit var binding: ActivityNominationSubmittedBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityNominationSubmittedBinding.inflate(layoutInflater)
		setContentView(binding.root)

		populateUI()
	}

	/**
	 * Adds the logic to the buttons.
	 */
	private fun populateUI() {
		binding.submitButton.setOnClickListener {
			navigateToCreateNomination()
		}

		binding.backButton.setOnClickListener {
			navigateToHome()
		}
	}

	/**
	 * Navigates to the Home screen.
	 */
	private fun navigateToHome() {
		val intent = Intent(this, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
		startActivity(intent)
		this.finish()
	}

	/**
	 * Navigates to the Create Nomination screen.
	 */
	private fun navigateToCreateNomination() {
		val intent = Intent(this, CreateNominationActivity::class.java)
		navigateUpTo(intent)
	}
}