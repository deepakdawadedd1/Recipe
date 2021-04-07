package com.devdd.recipe.data.remote.datasource

import com.devdd.recipe.data.db.entities.Category
import com.devdd.recipe.data.db.entities.Recipe
import com.devdd.recipe.data.remote.retrofit.RetrofitNetworkServiceApi
import com.devdd.recipe.domain.mappers.CategoryResponseToCategoryEntity
import com.devdd.recipe.domain.mappers.RecipeResponseToRecipeEntity
import com.devdd.recipe.utils.extensions.dataOrThrowException
import javax.inject.Inject


interface RecipeDataSource {
    suspend fun fetchRecipes(): List<Recipe>

    suspend fun fetchCategories(): List<Category>
}

class RecipeDataSourceImpl @Inject constructor(
    private val networkServiceApi: RetrofitNetworkServiceApi,
    private val recipeResponseToRecipeEntity: RecipeResponseToRecipeEntity,
    private val categoryResponseToCategoryEntity: CategoryResponseToCategoryEntity
) : RecipeDataSource {
    override suspend fun fetchRecipes(): List<Recipe> {
        val response = networkServiceApi.recipes()
        val fetchedRecipes = response.dataOrThrowException()
        return fetchedRecipes.recipes?.map {
            recipeResponseToRecipeEntity.map(it)
        } ?: emptyList()
    }

    override suspend fun fetchCategories(): List<Category> {
        val response = networkServiceApi.categories()
        val categories = response.dataOrThrowException().categories
        return categories?.map { categoryResponseToCategoryEntity.map(it) } ?: emptyList()

    }
}