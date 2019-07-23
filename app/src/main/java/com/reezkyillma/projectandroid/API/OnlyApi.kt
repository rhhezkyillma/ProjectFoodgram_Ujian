package com.reezkyillma.projectandroid.API

import com.reezkyillma.projectandroid.Model.Recipes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OnlyApi {
//    @GET("/subjects")
//    fun getSubject() : Observable<List<Subjects>>

    @GET("article/")
    fun getArticle() : Call<List<Recipes>>

    @GET("article/{id_article}")
    fun getArticlebyid(@Path("id_article") id : String) : Call<Recipes>

}