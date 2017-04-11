package ru.mksm.savetime.view.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.util.Linkify
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import ru.mksm.savetime.R
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.util.Locator

class OrderActivity : AppCompatActivity() {

    val hashMap = HashMap<FloatingActionButton, OrderType>()
    var order : Order? = null
    var mainFab : FloatingActionButton? = null

    companion object {

        val EXTRA_ORDER_NUMBER = "EXTRA_ORDER_NUMBER"

        fun create(context : Context, order : Order) {
            val intent : Intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(EXTRA_ORDER_NUMBER, order.getId())
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        order = Locator.ordersInteractor.getOrder(intent.getStringExtra(EXTRA_ORDER_NUMBER))
        if (order == null) {
            finish()
            return
        }
        supportActionBar?.title = getString(R.string.order_activity_title, order!!.id)
        val container = findViewById(R.id.order_container) as LinearLayout
        fillContainterWithDishes(container, order!!)
        addDelimeter(container)
        addLine(container, R.drawable.ic_phone_black_24dp, order!!.phone, true)
        addLine(container, R.drawable.ic_chat_bubble_black_24dp, order!!.comment, false)
        mainFab = findViewById(R.id.fab) as FloatingActionButton
        hashMap.put(findViewById(R.id.fab_cook) as FloatingActionButton, OrderType.Cooking)
        hashMap.put(findViewById(R.id.fab_ready) as FloatingActionButton, OrderType.Cooked)
        hashMap.put(findViewById(R.id.fab_done) as FloatingActionButton, OrderType.Done)
        mainFab!!.setOnClickListener { view ->
            switchFabState(view as FloatingActionButton, hashMap.keys)
        }
        checkEntriesColors(hashMap.entries)
        setOnClickListeners(hashMap.entries)
    }

    private fun  setOnClickListeners(entries: MutableSet<MutableMap.MutableEntry<FloatingActionButton, OrderType>>) {
        entries.forEach {
            var state = it.value
            it.key.setOnClickListener {
                order = Order(order!!.id, state, order!!.time, order!!.price, order!!.phone, order!!.comment, order!!.desc)
                checkEntriesColors(entries)
                switchFabState(mainFab!!, entries.map { it.key })
            }
        }

    }

    private fun  checkEntriesColors(entries: MutableSet<MutableMap.MutableEntry<FloatingActionButton, OrderType>>) {
        entries.forEach {
            if (order!!.type.ordinal >= it.value.ordinal) {
                it.key.isEnabled = false
                it.key.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.order_gray_text))
            } else {
                it.key.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.lightBlue))
            }
        }
    }

    private fun switchFabState(mainFab : FloatingActionButton, fabs : Iterable<FloatingActionButton>) {
        var wasVisible = false
        fabs.forEach {
            if (it.visibility == GONE) {
                it.visibility = VISIBLE
            } else {
                wasVisible = true
                it.visibility = GONE
            }
        }
        if (wasVisible) {
            mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_done_white_24dp))
            mainFab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.lightBlue))
        } else {
            mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_close_white_24dp))
            mainFab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.order_black_text))
        }
    }

    private fun fillContainterWithDishes (viewGroup: LinearLayout, order : Order) {
        var inflater = layoutInflater ?: return
        for (entry in order.desc) {
            val view = inflater.inflate(R.layout.dish_line, viewGroup, false)
            (view.findViewById(R.id.order_count) as TextView).text = entry.value.toString()
            (view.findViewById(R.id.order_text) as TextView).text = entry.key
            viewGroup.addView(view)
        }
    }

    private fun addDelimeter (viewGroup : LinearLayout) {
        var inflater = layoutInflater ?: return
        val view = inflater.inflate(R.layout.text_line, viewGroup, false)
        val textview = (view.findViewById(R.id.small_text) as TextView)
        textview.visibility = View.VISIBLE
        textview.text = applicationContext.getString(R.string.order_activity_delimeter)
        viewGroup.addView(view)
    }

    private fun addLine (viewGroup : LinearLayout, imageRes : Int, text : String, isPhone : Boolean) {
        var inflater = layoutInflater ?: return
        val view = inflater.inflate(R.layout.icon_line, viewGroup, false)
        var imageView = ((view.findViewById(R.id.icon)) as ImageView)
        val drawable = resources.getDrawable(imageRes)
        DrawableCompat.setTint(drawable, R.color.icon_gray_color)
        imageView.setImageDrawable(drawable)
        var textView = view.findViewById(R.id.icon_line_text) as TextView
        textView.visibility = View.VISIBLE
        textView.setText(text)
        if (isPhone) {
            textView.isClickable = true
            textView.autoLinkMask = Linkify.ALL
        }
        viewGroup.addView(view)
    }

}
