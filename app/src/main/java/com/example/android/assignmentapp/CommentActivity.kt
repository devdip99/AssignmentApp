package com.example.android.assignmentapp

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.android.assignmentapp.Adapter.CommentRecyclerAdapter
import com.example.android.assignmentapp.DataModel.CommentDataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_comment.*


class CommentActivity : AppCompatActivity() {

    lateinit var itemId: String
    private var comments = mutableListOf<CommentDataModel>()
    private lateinit var mAdapter: CommentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Comments"
        itemId = intent.getStringExtra("item_id")
        mAdapter = CommentRecyclerAdapter(this@CommentActivity, comments as ArrayList<CommentDataModel>)

        comment_recycler.adapter = mAdapter
        comment_recycler.layoutManager = LinearLayoutManager(this)

        comment_button.setOnClickListener {
            if (!comment_edittext.text.isNullOrEmpty()) {
                postComment()
            } else {
                Toast.makeText(this, "Can't post empty comments", Toast.LENGTH_SHORT).show()
            }
        }

        fetchComments()
        no_comments.visibility=View.GONE
    }

    private fun postComment() {
        val commentreference = FirebaseDatabase.getInstance().reference.child(itemId).child("comment")
        commentreference.push().setValue(CommentDataModel(getUserEmail()!!, comment_edittext.text.toString()))
        comment_edittext.setText("")
        hideKeyboard()
    }

    private fun fetchComments() {
        val commentreference = FirebaseDatabase.getInstance().reference.child(itemId).child("comment")
        commentreference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                progress_bar.visibility = View.GONE
                for (snapshot in p0.children) {
                    val commentDataModel: CommentDataModel? = snapshot.getValue(CommentDataModel::class.java)
                    if (commentDataModel != null) {
                        if (!comments.contains(commentDataModel)) {
                            comments.add(commentDataModel)
                            mAdapter.notifyDataSetChanged()

                        }

                    }


                }

                if (comments.size==0)
                {
                    no_comments.visibility=View.VISIBLE
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else {
            false
        }
    }


    fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
