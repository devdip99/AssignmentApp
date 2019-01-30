package com.example.android.assignmentapp.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.android.assignmentapp.DataModel.CommentDataModel
import com.example.android.assignmentapp.R

class CommentRecyclerAdapter(val mcontext: Context, val comments: ArrayList<CommentDataModel>) : RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.comment_recycler_layout, p0, false))
    }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.emailText.text = Html.fromHtml("<b>Commented by: ${comments[position].email}</b>")
        holder.commentText.text = comments[position].message
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emailText = view.findViewById<TextView>(R.id.email_text)
        val commentText = view.findViewById<TextView>(R.id.comment_text)
    }
}