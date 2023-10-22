package com.cube.cubeacademy.lib.di

import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee

class Repository(val api: ApiService) {
	// TODO: Add additional code if you need it

	suspend fun getAllNominations(): List<Nomination> {
		// TODO: Write the code to fetch the list nominations from the api
		return api.getAllNominations().data
	}

	suspend fun getAllNominees(): List<Nominee> {
		// TODO: Write the code to fetch list of all nominees from the api
		return api.getAllNominees().data
	}

	suspend fun createNomination(nomineeId: String, reason: String, process: String): Nomination? {
		// TODO: Write the code to create a new nomination using the api
		return api.createNomination(nomineeId, reason, process).data
	}
}