package itmo.alk.womplist.feature.mylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import itmo.alk.womplist.data.repository.AnimeRepository

class MyListViewModelFactory(
    private val repository: AnimeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

