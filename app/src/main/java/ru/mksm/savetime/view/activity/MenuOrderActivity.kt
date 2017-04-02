package ru.mksm.savetime.view.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View

import ru.mksm.savetime.R
import ru.mksm.savetime.util.ActivityCompanion

class MenuOrderActivity : AppCompatActivity() {

    companion object : ActivityCompanion(MenuOrderActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }
}
