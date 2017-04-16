package ru.mksm.savetime.view.activity


import android.content.Context
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.util.SortedList
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.daimajia.swipe.SwipeLayout
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import org.w3c.dom.Text
import ru.mksm.savetime.R
import ru.mksm.savetime.interactors.OrdersInteractor
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.util.Application
import ru.mksm.savetime.util.Locator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = LinearLayoutManager(activity);
        val orderTypes = MainActivity.TabType.values()[arguments.getInt(TAB_ORDER_TYPE)].orderTypes
        disposable = Locator.ordersInteractor.observeOrders(
                *orderTypes).observeOn(AndroidSchedulers.mainThread()).subscribe {
            adapter!!.onDataChanged(it)
        }
    }

    override fun onPause() {
        super.onPause()
        disposable ?: disposable!!.dispose()
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

    val list = SortedList(Order::class.java, object : SortedList.Callback<Order>() {
        override fun onChanged(position: Int, count: Int) {
            this@Adapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            this@Adapter.notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            this@Adapter.notifyItemMoved(fromPosition, toPosition)
        }

        override fun areContentsTheSame(oldItem: Order?, newItem: Order?): Boolean {
            return oldItem!! == newItem
        }

        override fun areItemsTheSame(item1: Order?, item2: Order?): Boolean {
            return item1!!.getId() == item2!!.getId()
        }

        override fun compare(o1: Order?, o2: Order?): Int {
            if (o1!!.type != o2!!.type) {
                return o1.type.ordinal - o2.type.ordinal
            } else {
                return o1.time.compareTo(o2.time)
            }
        }

        override fun onInserted(position: Int, count: Int) {
            this@Adapter.notifyItemRangeInserted(position, count)
        }

    })

    init {
        list.addAll(orders)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.order_recycler_view, parent, false))
    }


    override fun getItemCount() = list.size()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val view = holder!!.itemView
        val order = list[position]
        setupSwipeLayout(view, order, position)

        val header = view.findViewById(R.id.header) as RelativeLayout
        val idText = view.findViewById(R.id.order_id) as TextView
        val timeText = view.findViewById(R.id.order_time) as TextView
        val priceText = view.findViewById(R.id.order_price) as TextView
        val descText = view.findViewById(R.id.order_description) as TextView

        idText.text = order.getId()
        val format = SimpleDateFormat("HH:mm")
        timeText.text = format.format(order.time.time)
        priceText.text = view.context.getString(R.string.price_format, order.price)
        descText.text = order.desc.map { pairToKey(it) }.joinToString(separator = ", ")
        header.setBackgroundResource(order.type.headerResId)
        idText.setTextColor(context.resources.getColor(order.type.idColorResId))
        timeText.setTextColor(context.resources.getColor(order.type.otherColorResid))
        priceText.setTextColor(context.resources.getColor(order.type.idColorResId))
        view.setOnClickListener {
            OrderActivity.create(context, order)
        }


    }

    private fun setupSwipeLayout(view: View, order: Order, position : Int) {
        val swipeView = view.findViewById(R.id.swipe) as SwipeLayout
        swipeView.showMode = SwipeLayout.ShowMode.LayDown
        if (order.type.priority == 0) {
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
            val nextType = OrderType.values()[order.type.ordinal + 1]
            if (nextType == OrderType.Cooked) {
                val snackbar = Snackbar.make(view, R.string.are_you_sure, Snackbar.LENGTH_LONG)
                snackbar.setAction(R.string.cancel_action) {

                }
                snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event !=  BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                            list.remove(order)
                            Locator.ordersInteractor.changeOrder(Order(order.id,
                                    nextType,
                                    order.time,
                                    order.price,
                                    order.phone,
                                    order.comment,
                                    order.desc))
                        } else {
                            Locator.ordersInteractor.changeOrder(order)
                        }
                    }

                })
                snackbar.show()
            } else {
                Locator.ordersInteractor.changeOrder(Order(order.id,
                        nextType,
                        order.time,
                        order.price,
                        order.phone,
                        order.comment,
                        order.desc))
            }
            swipeView.close(true)
        }
    }

    private fun pairToKey(pair: Map.Entry<String, Int>): String {
        if (pair.value == 1) {
            return pair.key
        } else {
            return pair.key + " x" + pair.value
        }
    }

    fun onDataChanged(newOrders: Collection<Order>) {
        list.addAll(newOrders)
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
}