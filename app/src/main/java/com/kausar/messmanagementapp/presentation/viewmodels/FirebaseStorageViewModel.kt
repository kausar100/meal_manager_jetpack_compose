package com.kausar.messmanagementapp.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausar.messmanagementapp.data.firebase_storage.FirebaseStorageRepo
import com.kausar.messmanagementapp.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseStorageViewModel @Inject constructor(
    private val repo: FirebaseStorageRepo
) : ViewModel() {

    private val _profile: MutableState<StorageState> = mutableStateOf(
        StorageState()
    )
    val profile: State<StorageState> = _profile

    fun uploadProfilePic(image: Uri) = viewModelScope.launch {
        repo.uploadProfilePicture(image).collectLatest {
            when (it) {
                is ResultState.Success -> {
                    it.data?.let { uri ->
                        _profile.value = StorageState(url = uri)
                    }

                }

                is ResultState.Failure -> {
                    _profile.value = StorageState(
                        error = it.message.localizedMessage!!.toString()
                    )
                }

                is ResultState.Loading -> {
                    _profile.value = StorageState(
                        isLoading = true
                    )
                }
            }
        }
    }

    data class StorageState(
        val url: String = "",
        val error: String = "",
        val isLoading: Boolean = false
    )

}