package com.reezkyillma.projectandroid.Model



class Chat {

    var sender: String? = null
    var receiver: String? = null
    var message: String? = null
    var isIsseen: Boolean = false

    constructor(sender: String, receiver: String, message: String, isseen: Boolean) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.isIsseen = isseen
    }

    constructor() {}
}
