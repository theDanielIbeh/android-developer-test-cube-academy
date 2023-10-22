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

	private fun populateUI() {
		/**
		 * TODO: Add the logic for the two buttons (Don't forget that if you start to add a new nomination, the back button shouldn't come back here)
		 */

		binding.submitButton.setOnClickListener {
			navigateToCreateNomination()
		}

		binding.backButton.setOnClickListener {
			navigateToHome()
		}
	}

	/**
	 *
	 */
	private fun navigateToHome() {
		val intent = Intent(this, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
		startActivity(intent)
		this.finish()
	}

	/**
	 *
	 */
	private fun navigateToCreateNomination() {
		val intent = Intent(this, CreateNominationActivity::class.java)
		navigateUpTo(intent)
	}
}