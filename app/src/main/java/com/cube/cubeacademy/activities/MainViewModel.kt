package com.cube.cubeacademy.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
) : AndroidViewModel(application) {

    init {
        getNominees()
        getNominations()
    }

    private val _nominees = MutableStateFlow<List<Nominee>>(emptyList())
    val nominees: StateFlow<List<Nominee>>
        get() = _nominees

    private val _nominations = MutableStateFlow<List<Nomination>>(emptyList())
    val nominations: StateFlow<List<Nomination>>
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
    private val _nominationModelFlow = MutableStateFlow(nominationModel)
    val nominationModelFlow : StateFlow<NominationModel>
        get() = _nominationModelFlow

    fun updateNominationModelLive() {
        _nominationModelFlow.value = nominationModel
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
        viewModelScope.launch {
            try {
                _nominees.value = repository.getAllNominees()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getNominations() {
        viewModelScope.launch {
            try {
                _nominations.value = repository.getAllNominations()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createNomination(
        navigate: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.createNomination(
                    nominationModel.nomineeId, nominationModel.reason, nominationModel.process
                )
                navigate()
            } catch (e: Exception) {
//                Toast.makeText(
//                    application.applicationContext,
//                    "Make sure your device is connected",
//                    Toast.LENGTH_SHORT
//                ).show()
                e.printStackTrace()
            }
        }
    }
}