package com.reezkyillma.projectandroid.Fragments


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reezkyillma.projectandroid.Adapter.RecipeListAdapter
import com.reezkyillma.projectandroid.R
import com.reezkyillma.projectandroid.Model.Recipes
import com.reezkyillma.projectandroid.RecipeDetailActivity
import com.reezkyillma.projectandroid.RecipeViewModel
import kotlinx.android.synthetic.main.fragment_recipe.*

class RecipeFragment : Fragment(), RecipeListAdapter.OnItemClickListener {


    private lateinit var modelizer: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modelizer = ViewModelProviders.of(this).get(RecipeViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recipe, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modelizer.dataArticle?.observeForever{ articles ->
            articles?.let {
                populateArticleList(articles)
                Log.i("TAG", "data:" + articles)
            }

        }
//        modelizer.dataSubject?.observeForever { subjects ->
//            subjects?.let {
//                populateSubjectList(subjects)
//                Log.i("TAG", "data:" + subjects)
//            }
//
//
//        }

//        val HorizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//        rcViewSubject.setLayoutManager(HorizontalLayout)
//        refreshLayout.setOnRefreshListener(object : LiquidRefreshLayout.OnRefreshListener {
//            override fun completeRefresh() {
//
//            }
//
//            override fun refreshing() {
//                modelizer.getAllArtices()
//                Handler().postDelayed({
//                    refreshLayout.finishRefreshing()
//                }, 5000)
//
//            }
//        })

    }

    private fun populateArticleList(articleList: List<Recipes>) {
        rcViewArticle.adapter = RecipeListAdapter(articleList, this)
    }

//    private fun populateSubjectList(subjectList: List<Subjects>) {
//        rcViewSubject.adapter = SubjectListAdapter(subjectList, this)
//    }

    override fun onItemClickArticle(recipes: Recipes, itemView: View) {
//        val detailBundle = Bundle().apply{
//            putString(getString(R.string.article_id), recipes.id)
//        }
        val i: Intent = Intent(context, RecipeDetailActivity::class.java);
        i.putExtra("id", recipes.getArticleId())
        startActivity(i)
    }

//    override fun onItemClickSubject(subjects: Subjects, itemView: View) {
////        val detailBundle = Bundle().apply{
////            putInt(getString(R.string.book_id), book.id)
////        }
////        view?.findNavController()?.navigate(R.id.action2, detailBundle)
//    }


}




