package com.reezkyillma.projectandroid

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.reezkyillma.projectandroid.Adapter.MessageAdapter
import com.reezkyillma.projectandroid.Fragments.APIService
import com.reezkyillma.projectandroid.Model.Chat
import com.reezkyillma.projectandroid.Model.User
import com.reezkyillma.projectandroid.Notifications.Client
import com.reezkyillma.projectandroid.Notifications.Data
import com.reezkyillma.projectandroid.Notifications.MyResponse
import com.reezkyillma.projectandroid.Notifications.Sender
import com.reezkyillma.projectandroid.Notifications.Token

import java.util.ArrayList
import java.util.HashMap

import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_message.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageActivity : AppCompatActivity() {

    lateinit var profile_image: CircleImageView
    lateinit var username: TextView

    internal var fuser: FirebaseUser? = null
    lateinit var reference: DatabaseReference
    private val GALLERY_PICK = 1

    lateinit var btn_send: ImageButton
    lateinit var text_send: EditText

    lateinit var messageAdapter: MessageAdapter
    lateinit var mchat: MutableList<Chat>

    lateinit var recyclerView: RecyclerView

    internal lateinit var intent: Intent

    lateinit var seenListener: ValueEventListener

    lateinit var userid: String

    lateinit var receiver: String

    lateinit var apiService: APIService

    internal var notify = false

    // Storage Firebase
    private var mImageStorage: StorageReference? = null
    private var imageUri: Uri? = null
    private var uploadTask: StorageTask<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            // and this
//            startActivity(Intent(this@MessageActivity, MainMessage::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            onBackPressed()
        }

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java!!)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        profile_image = findViewById(R.id.profile_image)
        username = findViewById(R.id.username)
        btn_send = findViewById(R.id.btn_send)
        text_send = findViewById(R.id.text_send)

        intent = getIntent()
        userid = intent.getStringExtra("userid")
        fuser = FirebaseAuth.getInstance().currentUser

        btn_attach.setOnClickListener(View.OnClickListener {
            notify = true
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK)
        })


        btn_send.setOnClickListener {
            notify = true
            val msg = text_send.text.toString()
            if (msg != "") {
                sendMessage(fuser!!.uid, userid, msg)
            } else {
                Toast.makeText(this@MessageActivity, "You can't send empty message", Toast.LENGTH_SHORT).show()
            }
            text_send.setText("")
        }


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>(User::class.java!!)
                username.setText(user!!.username)
                if (user!!.imageurl == "default") {
                    profile_image.setImageResource(R.mipmap.ic_launcher)
                } else {
                    //and this
                    Glide.with(applicationContext).load(user!!.imageurl).into(profile_image)
                }

                readMesagges(fuser!!.uid, userid, user!!.imageurl!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        seenMessage(userid)
    }

    private fun seenMessage(userid: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue<Chat>(Chat::class.java!!)
                    if (chat!!.receiver == fuser!!.uid && chat!!.sender == userid) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun sendMessage(sender: String, receiver: String, message: String) {

        var reference = FirebaseDatabase.getInstance().reference

        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["isseen"] = false

        reference.child("Chats").push().setValue(hashMap)


        // add user to chat fragment
        val chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser!!.uid)
                .child(userid)

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser!!.uid)
        chatRefReceiver.child("id").setValue(fuser!!.uid)

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>(User::class.java!!)
                if (notify) {
                    sendNotifiaction(receiver, user!!.username!!, message)
                }
                notify = false
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun sendNotifiaction(receiver: String, username: String, message: String) {
        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
        val query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val token = snapshot.getValue<Token>(Token::class.java!!)
                    val data = Data(fuser!!.uid, R.mipmap.ic_launcher, "$username: $message", "New Message",
                            userid)

                    val sender = Sender(data, token!!.token!!)

                    apiService.sendNotification(sender)
                            .enqueue(object : Callback<MyResponse> {
                                override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                                    if (response.code() == 200) {
                                        if (response.body()!!.success != 1) {
                                            Toast.makeText(this@MessageActivity, "Your Message Has Been Sent!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                                }
                            })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun readMesagges(myid: String, userid: String, imageurl: String) {
        mchat = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mchat.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue<Chat>(Chat::class.java!!)
                    if (chat!!.receiver == myid && chat!!.sender == userid || chat!!.receiver == userid && chat!!.sender == myid) {
                        mchat.add(chat)
                    }

                    messageAdapter = MessageAdapter(this@MessageActivity, mchat, imageurl)
                    recyclerView.adapter = messageAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun currentUser(userid: String) {
        val editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        editor.putString("currentuser", userid)
        editor.apply()
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status

        reference.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
        currentUser(userid)
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
        status("offline")
        currentUser("none")
    }
    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK
                && data != null && data.data != null) {
            imageUri = data.data
            btn_attach.setImageURI(imageUri)

            val pd = ProgressDialog(this)
            pd.setMessage("Uploading")
            pd.show()

            mImageStorage = FirebaseStorage.getInstance().getReference("message_images")

            if (imageUri != null) {
                val fileReference = mImageStorage!!.child(System.currentTimeMillis().toString()
                        + "." + getFileExtension(imageUri!!))

                uploadTask = fileReference.putFile(imageUri!!)

                uploadTask!!.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.getException()!!
                    }

                    fileReference.getDownloadUrl()
                }.addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val mUri = downloadUri!!.toString()

                        var reference = FirebaseDatabase.getInstance().reference

                        val hashMap = HashMap<String, Any>()
                        hashMap["sender"] = fuser!!.getUid()
                        hashMap["receiver"] = userid
                        hashMap["message"] = "" + mUri
                        hashMap["type"] = "image"
                        hashMap["isseen"] = false

                        reference.child("Chats").push().setValue(hashMap)

                        pd.dismiss()

                        // add user to chat fragment
                        val chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                .child(fuser!!.getUid())
                                .child(userid)

                        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    chatRef.child("id").setValue(userid)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })

                        val chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                                .child(userid)
                                .child(fuser!!.getUid())
                        chatRefReceiver.child("id").setValue(fuser!!.getUid())

                        val msg = "*Foto*"

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.getUid())
                        reference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val User = dataSnapshot.getValue(User::class.java)
                                if (notify) {
                                    sendNotifiaction(receiver, User!!.username!!, msg)
                                }
                                notify = false
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })


                    } else {
                        Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                        pd.dismiss()
                    }
                }).addOnFailureListener(OnFailureListener { e ->
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    pd.dismiss()
                })
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
