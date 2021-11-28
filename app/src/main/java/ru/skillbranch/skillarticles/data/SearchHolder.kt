package ru.skillbranch.skillarticles.data

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData

/**
 * Created by Oleksiy Pasmarnov on 28.11.21
 */
object SearchHolder {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val search = MutableLiveData(SearchData())

    fun getSearchData() = search
    fun updateSearchData(searchData: SearchData) {
        search.value = searchData
    }
}

data class SearchData(
    val querySearch: String? = null,
    val isSearch: Boolean = false,
)