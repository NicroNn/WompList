package itmo.alk.womplist.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import itmo.alk.womplist.data.repository.AnimeRepository

class HomeViewModelFactory(
    private val repository: AnimeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

