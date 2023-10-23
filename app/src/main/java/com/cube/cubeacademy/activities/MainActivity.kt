package com.cube.cubeacademy.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityMainBinding
import com.cube.cubeacademy.lib.adapters.NominationsRecyclerViewAdapter
import com.cube.cubeacademy.lib.models.Nomination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NominationsRecyclerViewAdapter.Listener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val nominationsAdapter: NominationsRecyclerViewAdapter by lazy {
        NominationsRecyclerViewAdapter(this)
    }

    //    Detects the device's connectivity status
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false
            )
            if (notConnected) {
                onInternetUnavailable()
            } else {
                onInternetAvailable()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateUI()
    }

    /**
     * Populates the recyclerview with data.
     * Add the navigation logic to the create button.
     */
    private fun populateUI() {
        setupRecycler()

        binding.createButton.setOnClickListener {
            navigateToCreateNomination()
        }
    }

    /**
     * Navigates to the Create Nomination screen.
     */
    private fun navigateToCreateNomination() {
        val intent = Intent(this, CreateNominationActivity::class.java)
        startActivity(intent)
    }

    /**
     * Sets up the recycler
     */
    private fun setupRecycler() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = nominationsAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
        }
    }

    /**
     * Toggles the visibility of the recyclerview based on the number of [nominations].
     * @param nominations the list of previously nominations submitted.
     */
    private fun toggleRecyclerVisibility(nominations: List<Nomination>) {
        if (nominations.isEmpty()) {
            binding.emptyContainer.visibility = View.VISIBLE
            binding.nominationsContainer.visibility = View.GONE
        } else {
            binding.emptyContainer.visibility = View.GONE
            binding.nominationsContainer.visibility = View.VISIBLE
        }
    }

    /**
     * Populates the recyclerview with the list of recently submitted nominations.
     */
    private fun populateRecycler(nominations: List<Nomination>) {
        val recentNominations = nominations.filter { nomination -> isCurrentNomination(nomination) }
        nominationsAdapter.submitList(recentNominations)
    }

    /**
     * Returns true if the nomination was submitted this month.
     */
    private fun isCurrentNomination(nomination: Nomination): Boolean {
//        Get current month and year
        val calendar: Calendar = Calendar.getInstance()
        val currentMonth: Int = calendar.get(Calendar.MONTH) + 1
        val currentYear: Int = calendar.get(Calendar.YEAR)

//        Get month and year of submission
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        val date = LocalDate.parse(nomination.dateSubmitted, formatter)
        val nominationMonth = date.monthValue
        val nominationYear = date.year

        return (currentYear == nominationYear) && (currentMonth == nominationMonth)
    }

    /**
     * Returns the nominee's first and last names.
     * @param nomineeId the id of the nominee.
     */
    override fun getName(nomineeId: String): String {
        val currentNominee =
            viewModel.nominees.value.filter { nominee -> nominee.nomineeId == nomineeId }[0]
        val firstName = currentNominee.firstName
        val lastName = currentNominee.lastName
        return "$firstName $lastName".trim()
    }

    /**
     * Performs the contained action if the device is connected.
     */
    private fun onInternetAvailable() {
        lifecycleScope.launch {
            viewModel.nominations.collectLatest { nominations ->
                nominations.let {
                    toggleRecyclerVisibility(it)
                    populateRecycler(it)
                }
            }
        }
    }

    /**
     * Performs the contained action if the device is not connected.
     */
    private fun onInternetUnavailable(
    ) {
        Toast.makeText(this, getString(R.string.bad_network), Toast.LENGTH_LONG).show()
    }
}