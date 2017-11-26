package ru.mksm.savetime.view.activity


import android.content.Context
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.daimajia.swipe.SwipeLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.mksm.savetime.R
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderStage
import ru.mksm.savetime.util.Locator

/**
 * Created by mac on 02.04.17.
 */
class PlaceholderFragment : Fragment() {

    var adapter: Adapter? = null
    var disposable: Disposable? = null
    var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)
        recyclerView = rootView.findViewById(R.id.orders_recycler_view) as RecyclerView
        return rootView
    }

    override fun onResume() {
        super.onResume()
        adapter = Adapter(context, emptyList())
        adapter!!.setHasStableIds(true)
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = LinearLayoutManager(activity);
        val orderTypes = MainActivity.TabType.values()[arguments.getInt(TAB_ORDER_TYPE)].orderStages
        if (disposable != null) disposable!!.dispose()
        disposable = Locator.ordersInteractor.observeOrders(
                *orderTypes).observeOn(AndroidSchedulers.mainThread()).subscribe {
            adapter!!.onDataChanged(it)
        }
    }

    override fun onPause() {
        super.onPause()
        if (disposable != null) disposable!!.dispose()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val TAB_ORDER_TYPE = "tab_order_type"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(tabType: MainActivity.TabType): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val args = Bundle()

            args.putInt(TAB_ORDER_TYPE, tabType.ordinal)
            fragment.arguments = args
            return fragment
        }
    }
}

class Adapter(val context: Context, val orders: List<Order>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<Order> = ArrayList<Order>()

    init {
        onDataChanged(orders)
    }

    override fun getItemId(position: Int): Long {

        val order = list[position]
        return order.id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.order_recycler_view, parent, false))
    }


    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val view = holder!!.itemView
        val order = list[position]
        setupSwipeLayout(view, order, position)

        val header = view.findViewById(R.id.header) as RelativeLayout
        val idText = view.findViewById(R.id.order_id) as TextView
        val timeText = view.findViewById(R.id.order_time) as TextView
        val priceText = view.findViewById(R.id.order_price) as TextView
        val descText = view.findViewById(R.id.order_description) as TextView

        idText.text = if (order.id > 0) order.getId() else ""
        timeText.text = if (order.stage.priority == 0) order.date.substring(0, 5) + " " + order.time else order.time
        priceText.text = view.context.getString(R.string.price_format, order.getSummaryPrice())
        descText.text = order.desc.map { pairToKey(it) }.joinToString(separator = ", ")
        header.setBackgroundResource(order.stage.headerResId)
        idText.setTextColor(context.resources.getColor(order.stage.idColorResId))
        timeText.setTextColor(context.resources.getColor(order.stage.otherColorResid))
        priceText.setTextColor(context.resources.getColor(order.stage.idColorResId))
        view.setOnClickListener {
            OrderActivity.create(context, order)
        }


    }

    private fun setupSwipeLayout(view: View, order: Order, position: Int) {
        val swipeView = view.findViewById(R.id.swipe) as SwipeLayout
        swipeView.showMode = SwipeLayout.ShowMode.LayDown
        if (order.stage.priority == 0) {
            swipeView.isSwipeEnabled = false
        }
        swipeView.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onOpen(layout: SwipeLayout?) {

            }

            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                view.setOnClickListener(null)
            }

            override fun onStartOpen(layout: SwipeLayout?) {
                view.setOnClickListener(null)
            }

            override fun onStartClose(layout: SwipeLayout?) {
            }

            override fun onClose(layout: SwipeLayout?) {
                view.postDelayed({
                    view.setOnClickListener {
                        OrderActivity.create(context, order)
                    }
                }, 200)
            }

            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
            }
        })
        view.findViewById(R.id.button_swipe).setOnClickListener {
            list.remove(order)
            notifyDataSetChanged()
            val nextType = OrderStage.values()[order.stage.ordinal + 1]
            if (nextType == OrderStage.Cooked) {
                val snackbar = Snackbar.make(view, R.string.are_you_sure, Snackbar.LENGTH_LONG)
                snackbar.setAction(R.string.cancel_action) {

                }
                snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                            list.remove(order)
                            notifyDataSetChanged()
                            Locator.ordersInteractor.changeOrder(Order(order.id,
                                    nextType,
                                    order.time,
                                    order.date,
                                    order.phone,
                                    order.comment,
                                    order.orderTypeId,
                                    order.promocodes,
                                    order.guestsCount,
                                    order.desc), true)
                        } else {
                            Locator.ordersInteractor.changeOrder(order, false)
                        }
                    }

                })
                snackbar.show()
                if (!Locator.internetInteractor.isInternetConnected()) {
                    Toast.makeText(context, "СМС о готовности будет доставлена после появления сети"
                            , Toast.LENGTH_LONG).show()
                }
            } else {
                Locator.ordersInteractor.changeOrder(Order(order.id,
                        nextType,
                        order.time,
                        order.date,
                        order.phone,
                        order.comment,
                        order.orderTypeId,
                        order.promocodes,
                        order.guestsCount,
                        order.desc), true)
            }
            swipeView.close(true)
        }
    }

    private fun pairToKey(pair: Map.Entry<Dish, Int>): String {
        if (pair.value == 1) {
            return pair.key.name
        } else {
            return pair.key.name + " x" + pair.value
        }
    }

    fun onDataChanged(newOrders: Iterable<Order>) {
        list = ArrayList(newOrders.sortedWith(Comparator<Order> { o1: Order, o2: Order ->
            if (o1.stage != o2.stage) {
                o1.stage.ordinal - o2.stage.ordinal
            } else {
                if (o1.stage.priority != 0) {
                    o1.id.compareTo(o2.id)
                } else {
                    o2.id.compareTo(o1.id)
                }

            }
        }))
        notifyDataSetChanged()
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
}