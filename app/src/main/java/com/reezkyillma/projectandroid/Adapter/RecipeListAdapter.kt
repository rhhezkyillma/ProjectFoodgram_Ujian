package com.reezkyillma.projectandroid.Adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.reezkyillma.projectandroid.R
import com.reezkyillma.projectandroid.Model.Recipes
import com.reezkyillma.projectandroid.API.BaseUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recipe_layout_list_item.view.*

class RecipeListAdapter (
        private val articles : List<Recipes>,
        private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    interface OnItemClickListener{
        fun onItemClickArticle(recipes: Recipes, itemView : View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recipe_layout_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(articles[position], clickListener)
        Picasso.get().load(BaseUrl.baseUrl+articles.get(position).getImage()).into(holder.poster)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var poster: ImageView = itemView.findViewById(R.id.ImageArticle)

        fun bind(recipes: Recipes, listener: OnItemClickListener) = with(itemView) {
            txtATitle.text = recipes.getTitle().toString()
//                txtADescription.text = recipes.getDescription().toString()
            txtADate.text = recipes.getCreatedAt().toString()
//                visitCount.text = recipes.getViewCount().toString()

            setOnClickListener {
                listener.onItemClickArticle(recipes, it)
            }
        }
    }

}