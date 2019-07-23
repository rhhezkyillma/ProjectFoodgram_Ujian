package com.reezkyillma.projectandroid
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.reezkyillma.projectandroid.Model.Recipes
import com.reezkyillma.projectandroid.API.ApiClient
import com.reezkyillma.projectandroid.API.BaseUrl
import com.reezkyillma.projectandroid.API.OnlyApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_recipe.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var article : Recipes
    private lateinit var imagePoster : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_recipe)
        imagePoster =  findViewById(R.id.image)
        val onlyApi : OnlyApi = ApiClient.getClient().create(OnlyApi::class.java)
        val i = intent
        val id = i.getSerializableExtra("id")
        getDetailArticle(onlyApi, id.toString())
        back.setOnClickListener {
            onBackPressed()

        }

    }

    private fun getDetailArticle(onlyApi: OnlyApi, id: String) {
        val call : Call<Recipes> = onlyApi.getArticlebyid(id)
        call.enqueue(object : Callback<Recipes>{
            override fun onFailure(call: Call<Recipes>?, t: Throwable?) {
                Log.d("TAG", "Gagal Fetch Detail Article")
            }

            override fun onResponse(call: Call<Recipes>?, response: Response<Recipes>?) {
                article = response?.body()!!
                Log.d("TAG", "Movie size ${article}")
//                collapseToolbar.title = article.getTitle()
                textView_title.text =  article.getTitle()
                Description.text =  article.getDescription()

//                textView_published.text =  article.getPublishedAt()
                Picasso.get().load(BaseUrl.baseUrl+article.getImage()).into(imagePoster)
//                text_content.text = article.getTextContent()


            }

        })
    }
}

