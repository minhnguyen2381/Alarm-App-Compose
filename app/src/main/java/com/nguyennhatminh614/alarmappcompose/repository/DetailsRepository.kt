package com.nguyennhatminh614.alarmappcompose.repository

import com.nguyennhatminh614.alarmappcompose.database.AppDatabase
import com.nguyennhatminh614.alarmappcompose.database.asDomainModel
import com.nguyennhatminh614.alarmappcompose.domain.Details
import com.nguyennhatminh614.alarmappcompose.network.DetailsApi
import com.nguyennhatminh614.alarmappcompose.network.model.asDatabaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DetailsRepository @Inject constructor(
    private val detailsApi: DetailsApi,
    private val appDatabase: AppDatabase
) {

    fun getUserDetails(user: String): Flow<Details?> =
        appDatabase.usersDao.getDetails(user).map { it?.asDomainModel() }

    suspend fun refreshDetails(user: String) {
        val userDetails = detailsApi.getDetails(user)
        appDatabase.usersDao.insertDetails(userDetails.asDatabaseModel())
    }

}