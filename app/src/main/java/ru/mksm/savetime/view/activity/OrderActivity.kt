package ru.mksm.savetime.view.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.util.Linkify
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import ru.mksm.savetime.R
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderStage
import ru.mksm.savetime.util.Locator

class OrderActivity : AppCompatActivity() {

    val hashMap = HashMap<FloatingActionButton, OrderStage>()
    var order: Order? = null
    var mainFab: FloatingActionButton? = null

    companion object {

        val EXTRA_ORDER_NUMBER = "EXTRA_ORDER_NUMBER"

        fun create(context: Context, order: Order) {
            context.startActivity(getIntent(context, order))
        }

        fun getIntent(context: Context, order: Order)
                = Intent(context, OrderActivity::class.java).apply {
            putExtra(EXTRA_ORDER_NUMBER, order.getId())
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
        addLine(container, R.drawable.ic_access_time_black_24dp, order!!.date + " " + order!!.time, false)
        addLine(container, R.drawable.ic_info_black_24dp,
                Locator.orderTypeInteractor.repo.get(order!!.orderTypeId.toString())?.name ?: "",
                false)
        val guests = order!!.guestsCount
        addLine(container, R.drawable.ic_people_black_24dp,
                resources.getQuantityString(R.plurals.guests,guests, guests),
                false)
        addDelimeter(container, R.string.order_activity_dishes_delimeter)
        fillContainterWithDishes(container, order!!)
        addDelimeter(container, R.string.order_activity_delimeter)
        addLine(container, R.drawable.ic_phone_black_24dp,if(order!!.phone.isNullOrEmpty()) "" else "+" + order!!.phone, true)
        addLine(container, R.drawable.ic_card_giftcard_black_24dp,
                order!!.promocodes.joinToString(transform = {promocodeid ->
                    val promocode = Locator.promocodeInteractor.repo.get(promocodeid.id.toString())
                    return@joinToString promocode?.name ?: "" + " " + promocode?.descr ?: ""
                }), false)
        addLine(container, R.drawable.ic_chat_bubble_black_24dp, order!!.comment ?: "", false)
        mainFab = findViewById(R.id.fab) as FloatingActionButton
        hashMap.put(findViewById(R.id.fab_cook) as FloatingActionButton, OrderStage.Cooking)
        hashMap.put(findViewById(R.id.fab_ready) as FloatingActionButton, OrderStage.Cooked)
        hashMap.put(findViewById(R.id.fab_done) as FloatingActionButton, OrderStage.Done)
        mainFab!!.setOnClickListener { view ->
            switchFabState(view as FloatingActionButton, hashMap.keys)
        }
        if (order!!.stage.priority == 0) {
            mainFab!!.visibility = GONE
        }
        checkEntriesColors(hashMap.entries)
        setOnClickListeners(container, hashMap.entries)
    }

    private fun setOnClickListeners(view: View, entries: MutableSet<MutableMap.MutableEntry<FloatingActionButton, OrderStage>>) {
        entries.forEach {
            var state = it.value
            it.key.setOnClickListener {
                if (state == OrderStage.Cooked || (order!!.stage != OrderStage.Cooked && state == OrderStage.Done)) {
                    val snackbar = Snackbar.make(view, R.string.are_you_sure, Snackbar.LENGTH_LONG)
                    snackbar.setAction(R.string.cancel_action) {

                    }
                    snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                                Locator.ordersInteractor.changeOrder(Order(order!!.id,
                                        state,
                                        order!!.time,
                                        order!!.date,
                                        order!!.phone,
                                        order!!.comment,
                                        order!!.orderTypeId,
                                        order!!.promocodes,
                                        order!!.guestsCount,
                                        order!!.desc), true)
                                finish()
                            }
                        }

                    })
                    snackbar.show()
                    if (!Locator.internetInteractor.isInternetConnected()) {
                        Toast.makeText(this, "СМС о готовности будет доставлена после появления сети"
                                , Toast.LENGTH_LONG).show()
                    }
                } else {
                    order = Order(order!!.id, state, order!!.time,
                            order!!.date,
                            order!!.phone,
                            order!!.comment,
                            order!!.orderTypeId,
                            order!!.promocodes,
                            order!!.guestsCount,
                            order!!.desc)
                    Locator.ordersInteractor.changeOrder(order!!, true)
                    finish()
                }
                checkEntriesColors(entries)
                switchFabState(mainFab!!, entries.map { it.key })
            }
        }

    }

    private fun checkEntriesColors(entries: MutableSet<MutableMap.MutableEntry<FloatingActionButton, OrderStage>>) {
        entries.forEach {
            if (order!!.stage.ordinal >= it.value.ordinal) {
                it.key.isEnabled = false
                it.key.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.order_gray_text))
            } else {
                it.key.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.lightBlue))
            }
        }
    }

    private fun switchFabState(mainFab: FloatingActionButton, fabs: Iterable<FloatingActionButton>) {
        var linearLayout = findViewById(R.id.buttons_layout)
        var wasVisible = linearLayout.visibility == VISIBLE

        if (wasVisible) {
            linearLayout.visibility = GONE
            mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_done_white_24dp))
            mainFab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.lightBlue))
        } else {
            linearLayout.visibility = VISIBLE
            mainFab.setImageDrawable(resources.getDrawable(R.drawable.ic_close_white_24dp))
            mainFab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.order_black_text))
        }
    }

    private fun fillContainterWithDishes(viewGroup: LinearLayout, order: Order) {
        var inflater = layoutInflater ?: return
        for (entry in order.desc) {
            val view = inflater.inflate(R.layout.dish_line, viewGroup, false)
            (view.findViewById(R.id.order_count) as TextView).text = entry.value.toString()
            (view.findViewById(R.id.order_text) as TextView).text = entry.key.name
            viewGroup.addView(view)
        }
    }

    private fun addDelimeter(viewGroup: LinearLayout, @StringRes textRes : Int) {
        var inflater = layoutInflater ?: return
        val view = inflater.inflate(R.layout.text_line, viewGroup, false)
        val textview = (view.findViewById(R.id.small_text) as TextView)
        textview.visibility = View.VISIBLE
        textview.text = applicationContext.getString(textRes)
        viewGroup.addView(view)
    }

    private fun addLine(viewGroup: LinearLayout, imageRes: Int, text: String, isPhone : Boolean) {
        var inflater = layoutInflater ?: return
        val view = inflater.inflate(R.layout.icon_line, viewGroup, false)
        var imageView = ((view.findViewById(R.id.icon)) as ImageView)
        val drawable = resources.getDrawable(imageRes)
        imageView.setImageDrawable(drawable)
        val color = resources.getColor(R.color.icon_gray_color)
        imageView.setColorFilter(color)
        var textView = view.findViewById(R.id.line_text) as TextView
        if (isPhone) {
            textView.autoLinkMask = Linkify.PHONE_NUMBERS
        }
        textView.visibility = View.VISIBLE
        textView.setText(text)
        viewGroup.addView(view)
    }

}
