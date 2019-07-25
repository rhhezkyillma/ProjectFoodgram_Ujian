package com.reezkyillma.projectandroid.Model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Recipes {
    @SerializedName("id_article")
    @Expose
    private var id_article: String? = null
    @SerializedName("id_category")
    @Expose
    private var id_category: String? = null
    @SerializedName("title")
    @Expose
    private var title: String? = null
    @SerializedName("slug")
    @Expose
    private var slug: String? = null
    @SerializedName("description")
    @Expose
    private var description: String? = null
    @SerializedName("created_at")
    @Expose
    private var created_at: String? = null
    @SerializedName("updated_at")
    @Expose
    private var updated_at: String? = null
    @SerializedName("created_by")
    @Expose
    private var created_by: String? = null
    @SerializedName("image")
    @Expose
    private var image: String? = null

    @SerializedName("ingredient")
    @Expose
    private var ingredient: String? = null

    @SerializedName("direction")
    @Expose
    private var direction: String? = null


    fun getArticleId(): String? {

        return id_article
    }
    fun getCategoryId(): String? {

        return id_category
    }

    fun getTitle(): String? {
        return title
    }

    fun getSlug(): String? {
        return slug
    }

    fun getDescription(): String? {
        return description
    }

    fun getCreatedAt(): String? {
        return created_at
    }

    fun getUpdateAt(): String? {
        return updated_at
    }

    fun getCreatedBy(): String? {
        return created_by
    }

    fun getImage(): String? {
        return image
    }

    fun getIngredient(): String?{
        return ingredient
    }

    fun getDirection(): String?{
        return direction
    }


}