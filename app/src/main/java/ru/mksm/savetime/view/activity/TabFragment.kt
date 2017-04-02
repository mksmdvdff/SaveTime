package ru.mksm.savetime.view.activity


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import org.w3c.dom.Text
import ru.mksm.savetime.R
import ru.mksm.savetime.interactors.OrdersInteractor
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.util.Application
import ru.mksm.savetime.util.Locator
import java.util.*

/**
 * Created by mac on 02.04.17.
 */
class PlaceholderFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)
        val recyclerView = rootView.findViewById(R.id.orders_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context);
        val orderTypes = MainActivity.TabType.values()[arguments.getInt(TAB_ORDER_TYPE)].orderTypes
        Locator.mOrdersInteractor.observeOrders(
                *orderTypes).subscribe {
            recyclerView.adapter = Adapter(context, it)
        }
        return rootView
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

class Adapter(val context : Context, val orders : List<Order>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.order_recycler_view, parent))
    }


    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val view = holder!!.itemView
        val header = view.findViewById(R.id.header) as RelativeLayout
        val idText = view.findViewById(R.id.order_id) as TextView
        val timeText = view.findViewById(R.id.order_id) as TextView
        val priceText = view.findViewById(R.id.order_id) as TextView
        val descText = view.findViewById(R.id.order_description) as TextView
        val order = orders[position]
        idText.text = order.getId()
        timeText.text = view.context.getString(R.string.time_format,order.time.get(Calendar.HOUR),
                order.time.get(Calendar.MINUTE))
        priceText.text = view.context.getString(R.string.price_format, order.price)
        descText.text = order.desc.toString()

    }

}

class ViewHolder(view : View) : RecyclerView.ViewHolder(view)