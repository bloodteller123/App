package com.example.androidapplicationproject.database

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class PropertyViewModel(private val repository: Repository) : ViewModel(){

    fun loadPropertiesFrom(userId: Int): LiveData<List<PropertyTable>> {
        return repository.loadPropertiesFrom(userId).asLiveData()
    }
    fun insertUser(user: UserTable){
        viewModelScope.launch{
            repository.insertUser(user)
        }
    }

    fun insertProperty(pro: PropertyTable){
        viewModelScope.launch {
            repository.insertProperty(pro)
        }
    }

    fun checkuserpass(userName:String, password:String): List<UserTable> {
//        viewModelScope.launch {
            return repository.checkuserpass(userName, password)
//        }
    }

}

class PropertyViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}