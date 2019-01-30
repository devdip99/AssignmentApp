package com.example.android.assignmentapp.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.assignmentapp.CommentActivity
import com.example.android.assignmentapp.DataModel.ItemDataModel
import com.example.android.assignmentapp.R
import com.example.android.assignmentapp.getUserEmail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemRecyclerAdapter(private val mcontext: Context, private val items: ArrayList<ItemDataModel>) : RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.recycler_view_layout, p0, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mcontext).load(items[position].image).apply(RequestOptions().placeholder(R.drawable.placeholder_posts_image)).into(holder.imageView)
        holder.titleText.text = Html.fromHtml("<b>Title</b>: ${items[position].title}")
        holder.descriptionText.text = Html.fromHtml("<b>Description</b>: ${items[position].description}")
        holder.categoryText.text = Html.fromHtml("<b>Category</b>: ${items[position].category}")

        holder.likeImage.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().reference.child(items[position].id).child("likes")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChild(mcontext.getUserEmail()!!.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("[", ""))) {
                        p0.child(mcontext.getUserEmail()!!.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("[", "")).ref.removeValue()
                        holder.likeImage.setImageDrawable(mcontext.getDrawable(R.drawable.favorite_border_white))
                    } else {
                        p0.ref.child(mcontext.getUserEmail()!!.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("[", "")).setValue(true)
                        holder.likeImage.setImageDrawable(mcontext.getDrawable(R.drawable.favorite_border_filled))
                    }
                }

            })
        }

        val likereference = FirebaseDatabase.getInstance().reference.child(items[position].id).child("likes")
        likereference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                holder.likeCount.text = p0.childrenCount.toString()

            }

        })

        likereference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(mcontext.getUserEmail()!!.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("[", ""))) {
                    holder.likeImage.setImageDrawable(mcontext.getDrawable(R.drawable.favorite_border_filled))
                } else {
                    holder.likeImage.setImageDrawable(mcontext.getDrawable(R.drawable.favorite_border_white))
                }
            }

        })

        val commentreference = FirebaseDatabase.getInstance().reference.child(items[position].id).child("comment")
        commentreference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                holder.commentCount.text = p0.childrenCount.toString()
            }

        })

        holder.commentContainer.setOnClickListener {
            val intent = Intent(mcontext, CommentActivity::class.java)
            intent.putExtra("item_id", items[position].id)
            mcontext.startActivity(intent)
        }


    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val titleText = view.findViewById<TextView>(R.id.title_text)
        val descriptionText = view.findViewById<TextView>(R.id.description_text)
        val categoryText = view.findViewById<TextView>(R.id.category_text)
        val likeImage = view.findViewById<ImageView>(R.id.star)
        val likeCount = view.findViewById<TextView>(R.id.like_count)
        val commentContainer = view.findViewById<LinearLayout>(R.id.comment)
        val commentCount = view.findViewById<TextView>(R.id.comment_count)
    }

}