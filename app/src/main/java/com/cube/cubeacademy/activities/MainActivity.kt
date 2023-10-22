package com.cube.cubeacademy.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cube.cubeacademy.databinding.ActivityMainBinding
import com.cube.cubeacademy.lib.adapters.NominationsRecyclerViewAdapter
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nomination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NominationsRecyclerViewAdapter.Listener {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var repository: Repository

    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModel.MainViewModelFactory
    private lateinit var adapter: NominationsRecyclerViewAdapter


    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(ConnectivityManager
                .EXTRA_NO_CONNECTIVITY, false)
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

        viewModelFactory = this.let {
            MainViewModel.MainViewModelFactory(
                application = it.application,
                repository = repository
            )
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        populateUI()
    }

    private fun populateUI() {
        /**
         * TODO: Populate the UI with data in this function
         * 		 You need to fetch the list of user's nominations from the api and put the data in the recycler view
         * 		 And also add action to the "Create new nomination" button to go to the CreateNominationActivity
         */
        initialiseRecycler()

        binding.createButton.setOnClickListener {
            navigateToCreateNomination()
        }
    }

    private fun navigateToCreateNomination() {
        val intent = Intent(this, CreateNominationActivity::class.java)
        startActivity(intent)
    }

    private fun initialiseRecycler() {
        adapter = NominationsRecyclerViewAdapter(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun toggleRecyclerVisibility(nominations: List<Nomination>) {
        binding.badNetworkContainer.visibility = View.GONE
        if (nominations.isEmpty()) {
            binding.emptyContainer.visibility = View.VISIBLE
            binding.nominationsContainer.visibility = View.GONE
        } else {
            binding.emptyContainer.visibility = View.GONE
            binding.nominationsContainer.visibility = View.VISIBLE
        }
    }

    private fun populateRecycler(nominations: List<Nomination>) {
        viewModel.nominees.observe(
            this
        ) {
            adapter.submitList(nominations)
        }
    }

    override fun getName(nomineeId: String): String {
        val currentNominee =
            viewModel.nominees.value?.filter { nominee -> nominee.nomineeId == nomineeId }?.get(0)
        val firstName = currentNominee?.firstName ?: ""
        val lastName = currentNominee?.lastName ?: ""
        return "$firstName $lastName".trim()
    }

    private fun onInternetAvailable(
    ) {
        viewModel.nominations.observe(this) { nominations ->
            nominations?.let {
                toggleRecyclerVisibility(it)
                populateRecycler(it)
            }
        }
    }

    private fun onInternetUnavailable(
    ) {
        binding.badNetworkContainer.visibility = View.VISIBLE
        binding.emptyContainer.visibility = View.GONE
        binding.nominationsContainer.visibility = View.GONE
    }
}