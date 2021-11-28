package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.SearchData
import ru.skillbranch.skillarticles.viewmodels.ArticleState

/**
 * Created by Oleksiy Pasmarnov on 28.11.21
 */
fun ArticleState.toSearchData() : SearchData {
    return SearchData(searchQuery,isSearch)
}