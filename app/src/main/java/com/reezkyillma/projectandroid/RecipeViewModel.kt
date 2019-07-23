package com.reezkyillma.projectandroid


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.reezkyillma.projectandroid.Model.Recipes
//import com.readme.data.model.Subjects
import com.reezkyillma.projectandroid.API.BaseUrl
import com.reezkyillma.projectandroid.API.OnlyApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RecipeViewModel(application : Application) : AndroidViewModel(application) {
    var articleList : MutableLiveData<List<Recipes>>? = null
    //    var subjectList : MutableLiveData<List<Subjects>>? = null
    val dataArticle : LiveData<List<Recipes>>
        get() {
            if(articleList == null){
                articleList = MutableLiveData()
                getAllArtices()
            }
            articleList = MutableLiveData()
            getAllArtices()

            return articleList!!
        }
    //    val dataSubject : LiveData<List<Subjects>>
////        get() {
////            if(subjectList == null){
////                subjectList = MutableLiveData()
////                getAllSubjects()
////            }
////            subjectList = MutableLiveData()
////            getAllSubjects()
////            return subjectList!!
////        }
    init {
        getAllArtices()
    }


    fun getAllArtices(){
        val retrofit = Retrofit.Builder()
                .baseUrl(BaseUrl.baseUrl+ BaseUrl.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val apidata = retrofit.create(OnlyApi::class.java)
        val calldata = apidata.getArticle()
        calldata.enqueue(object : Callback<List<Recipes>> {
            override fun onResponse(call: Call<List<Recipes>>, response: Response<List<Recipes>>) {
                articleList?.value = response.body()
            }

            override fun onFailure(call: Call<List<Recipes>>, t: Throwable) {

            }

        })
    }

//    fun getAllSubjects(){
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BaseUrl.baseUrl+BaseUrl.apiPath)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apidata = retrofit.create(OnlyApi::class.java)
//        val calldata = apidata.getSubject()
//        calldata.enqueue(object : Callback<List<Subjects>> {
//            override fun onResponse(call: Call<List<Subjects>>, response: Response<List<Subjects>>) {
//                subjectList?.value = response.body()
//            }
//
//            override fun onFailure(call: Call<List<Subjects>>, t: Throwable) {
//
//            }
//
//        })
//    }

}



