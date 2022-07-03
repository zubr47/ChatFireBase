package com.example.chatfirebase

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setAvatar()
        val database = Firebase.database
        val myRef = database.getReference("message")
        binding.bSend.setOnClickListener() {
            myRef.child(myRef.push().key ?: "ermm?")
                .setValue(User(auth.currentUser?.displayName, binding.textMessage.text.toString()))
        }
      onChangeListener(myRef)
      initRcView()
    }
    private fun initRcView() = with(binding) {
    adapter = UserAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sign_out) {
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun onChangeListener(dRef: DatabaseReference) {
     dRef.addValueEventListener(object: ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
             val list = ArrayList<User>()
            for (i in snapshot.children) {
                val user = i.getValue(User::class.java)
                if(user != null) list.add(user)
            }
             adapter.submitList(list)
             }


         override fun onCancelled(error: DatabaseError) {
             TODO("Not yet implemented")
         }
     })

     }
    private fun setAvatar() {
        val bar = supportActionBar
        Thread {
            val bMap = Picasso.get().load(auth.currentUser?.photoUrl).get() // получаем аватарку
            val dIcon = BitmapDrawable(resources, bMap)   // ава в drawable
            runOnUiThread {                               // запускаем на основном потоке
                bar?.setDisplayShowHomeEnabled(true)
                bar?.setHomeAsUpIndicator(dIcon)
                bar?.title = auth.currentUser?.displayName
            }
        }.start()
    }
    }

