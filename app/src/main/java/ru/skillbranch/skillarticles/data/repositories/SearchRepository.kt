package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.SearchData
import ru.skillbranch.skillarticles.data.SearchHolder

/**
 * Created by Oleksiy Pasmarnov on 28.11.21
 */
object SearchRepository {
    fun getSearchData(): LiveData<SearchData> = SearchHolder.getSearchData()

    fun updateSearchData(searchData: SearchData) {
        SearchHolder.updateSearchData(searchData)
    }
}