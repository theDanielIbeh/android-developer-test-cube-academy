package com.cube.cubeacademy.activities

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainViewModel(
    private val application: Application,
    private val repository: Repository
) : AndroidViewModel(application) {

    init {
        getNominees()
        getNominations()
    }

    private val _nominees = MutableLiveData<List<Nominee>?>()
    val nominees: LiveData<List<Nominee>?>
        get() = _nominees

    private val _nominations = MutableLiveData<List<Nomination>?>()
    val nominations: LiveData<List<Nomination>?>
        get() = _nominations

    data class NominationModel(
        var nomineeId: String,
        var reason: String,
        var process: String,
    )

    var nominationModel: NominationModel = NominationModel(
        nomineeId = "",
        reason = "",
        process = "",
    )
    val nominationModelLive = MutableLiveData(nominationModel)

    fun updateNominationModelLive() {
        nominationModelLive.postValue(nominationModel)
    }

    fun resetNominationModel() {
        nominationModel = NominationModel(
            nomineeId = "",
            reason = "",
            process = "",
        )
        updateNominationModelLive()
    }

    private fun getNominees() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                _nominees.postValue(repository.getAllNominees())
            } catch (e: HttpException) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    private fun getNominations() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                _nominations.postValue(repository.getAllNominations())
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }

    fun createNomination(
        navigate: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                repository.createNomination(
                    nominationModel.nomineeId, nominationModel.reason, nominationModel.process
                )
                navigate()
            } catch (e: HttpException) {
                Toast.makeText(
                    application.applicationContext,
                    "Make sure your device is connected",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    class MainViewModelFactory(
        private val application: Application,
        private val repository: Repository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application = application, repository = repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}