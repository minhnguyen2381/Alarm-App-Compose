package com.nguyennhatminh614.alarmappcompose.repository

import com.nguyennhatminh614.alarmappcompose.database.AppDatabase
import com.nguyennhatminh614.alarmappcompose.database.asDomainModel
import com.nguyennhatminh614.alarmappcompose.domain.User
import com.nguyennhatminh614.alarmappcompose.network.UsersApi
import com.nguyennhatminh614.alarmappcompose.network.model.asDatabaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class UsersRepository @Inject constructor(
    private val usersApi: UsersApi,
    private val appDatabase: AppDatabase
) {

    val users: Flow<ImmutableList<User>?> =
        appDatabase.usersDao.getUsers().map { it?.asDomainModel()?.toImmutableList() }

    suspend fun refreshUsers() {
        val users = usersApi.getUsers()
        appDatabase.usersDao.insertUsers(users.asDatabaseModel())
    }
}