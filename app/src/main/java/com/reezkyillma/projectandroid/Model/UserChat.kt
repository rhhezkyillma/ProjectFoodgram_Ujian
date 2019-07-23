package com.reezkyillma.projectandroid.Model

class UserChat{
    var id: String? = null
    var username: String? = null
    var imageurl: String? = null
    var status: String? = null
    var search: String? = null

    constructor(id: String, username: String, imageURL: String, status: String, search: String) {
        this.id = id
        this.username = username
        this.imageurl = imageURL
        this.status = status
        this.search = search
    }

    constructor() {

    }
}

