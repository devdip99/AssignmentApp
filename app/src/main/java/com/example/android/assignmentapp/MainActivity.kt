package com.example.android.assignmentapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.android.assignmentapp.Adapter.ItemRecyclerAdapter
import com.example.android.assignmentapp.DataModel.ItemDataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private var items = mutableListOf<ItemDataModel>()
    private lateinit var mAdapter: ItemRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar

        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mAdapter = ItemRecyclerAdapter(this@MainActivity, items as ArrayList<ItemDataModel>)
        recycler_view.adapter = mAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        checkforDataChange()
    }

    private fun checkforDataChange() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myref: DatabaseReference = database.getReference("Items")
        myref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapShot in dataSnapshot.children) {
                    val item = snapShot.getValue(ItemDataModel::class.java)
                    if (item != null) {
                        if (!items.contains(item)) {
                            items.add(item)
                            mAdapter.notifyDataSetChanged()

                        }

                        if (items.size != 0) {
                            progress_bar.visibility = View.GONE
                        }
                    }


                }
            }

        })
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.log_out -> {
                logoutUser()
                true
            }
            R.id.add_item -> {
                startActivity(Intent(this, SaveItemActivity::class.java))
                true
            }
            else -> false
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        this.removeUserEmail()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()

    }
}
