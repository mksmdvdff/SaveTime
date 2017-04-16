package ru.mksm.savetime.view.activity

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.w3c.dom.Text

import ru.mksm.savetime.R
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.util.ActivityCompanion
import ru.mksm.savetime.util.Locator
import java.util.*
import kotlin.collections.HashMap

class OrderCreatingActivity : AppCompatActivity() {

    companion object : ActivityCompanion(OrderCreatingActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_creating_activity)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val recyclerView = findViewById(R.id.dishes_recycler_view) as RecyclerView
        val nextButton = findViewById(R.id.creating_next) as Button
        recyclerView.layoutManager = LinearLayoutManager(this)
        Locator.dishesInteractor.getAllDishes().subscribe {
            recyclerView.adapter = DishesAdapter(this, ArrayList(it), nextButton)
        }

        nextButton.setOnClickListener {
            OrderFinishingActivity.create(this)
        }
    }
}

class DishesAdapter(val context: Context, val dishes: List<Dish>, val nextButton: Button) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val order : HashMap<Dish, Int>
    init {
        order = Locator.dishesInteractor.currOrder
        checkNextButtonState()
    }




    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.dish_recycler_view, parent, false))
    }


    override fun getItemCount() = dishes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val view = holder!!.itemView
        val title = view.findViewById(R.id.dish_name) as TextView
        val price = view.findViewById(R.id.dish_price) as TextView
        val button = view.findViewById(R.id.dish_button) as FloatingActionButton
        val minusButton = view.findViewById(R.id.minus_button) as ImageButton
        minusButton.setColorFilter(context.resources.getColor(R.color.order_gray_text))
        val countView = view.findViewById(R.id.dish_count) as TextView
        val dish = dishes[position]
        title.text = dish.name
        price.text = context.getString(R.string.price_format, dish.prise)
        checkFabState(position, button, countView, minusButton)
        button.setOnClickListener {
            val dish = dishes[position]
            order[dish] = (order[dish] ?: 0) + 1
            checkFabState(position, button, countView, minusButton)
            checkNextButtonState()
        }
        minusButton.setOnClickListener {
            val dish = dishes[position]
            order[dish] = (order[dish] ?: 0) - 1
            checkFabState(position, button, countView, minusButton)
            checkNextButtonState()
        }


    }

    private fun checkNextButtonState() {
        if (order.values.filter { it > 0 }.isNotEmpty()) {
            nextButton.isEnabled = true
            nextButton.setBackgroundResource(R.color.lightBlue)
            nextButton.setTextColor(context.resources.getColor(R.color.white))
        } else {
            nextButton.isEnabled = false
            nextButton.setBackgroundResource(R.color.icon_gray_color)
        }
    }

    private fun checkFabState(elementNumber : Int, fab : FloatingActionButton, countView : TextView, minus : ImageButton) {
        val dish = dishes[elementNumber]
        var count = order[dish] ?: 0
        if (count == 0) {
            fab.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.order_gray_text))
            fab.setImageResource(R.drawable.ic_add_white_24dp)
            countView.text = ""
            minus.visibility = View.INVISIBLE
        } else {
            fab.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.lightBlue))
            fab.setImageDrawable(null)
            countView.text = count.toString()
            minus.visibility = View.VISIBLE
        }
    }

}
