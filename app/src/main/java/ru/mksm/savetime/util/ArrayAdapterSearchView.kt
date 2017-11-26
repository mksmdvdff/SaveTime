package ru.mksm.savetime.util

import android.content.Context
import android.support.v4.widget.CursorAdapter
import android.support.v7.widget.SearchView
import android.util.AttributeSet
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter


/**
 * Created by mac on 02.05.17.
 */
class ArrayAdapterSearchView : SearchView {

    private var mSearchAutoComplete: SearchView.SearchAutoComplete? = null

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    fun initialize() {
        mSearchAutoComplete = findViewById(android.support.v7.appcompat.R.id.search_src_text) as SearchAutoComplete
        this.setAdapter(null)
        this.setOnItemClickListener(null)
    }

    override fun setSuggestionsAdapter(adapter: CursorAdapter) {
        // don't let anyone touch this
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mSearchAutoComplete!!.setOnItemClickListener(listener)
    }

    fun setAdapter(adapter: ArrayAdapter<*>?) {
        mSearchAutoComplete!!.setAdapter(adapter)
    }

    fun setText(text: String) {
        mSearchAutoComplete!!.setText(text)
    }

}
