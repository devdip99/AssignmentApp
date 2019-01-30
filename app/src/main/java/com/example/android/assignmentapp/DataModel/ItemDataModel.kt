package com.example.android.assignmentapp.DataModel

data class ItemDataModel(val id:String,val title:String,val description:String,val category:String,val image: String)
{
    constructor():this("","","","","")
}