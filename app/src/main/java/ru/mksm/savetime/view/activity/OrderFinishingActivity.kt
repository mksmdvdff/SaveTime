package ru.mksm.savetime.view.activity

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.mksm.savetime.R
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderStage
import ru.mksm.savetime.net.data.PromocodeId
import ru.mksm.savetime.util.ActivityCompanion
import ru.mksm.savetime.util.Locator
import java.text.SimpleDateFormat
import java.util.*


class OrderFinishingActivity : AppCompatActivity() {

    companion object : ActivityCompanion(OrderFinishingActivity::class.java)

    val order = Locator.dishesInteractor.currOrder
    var internetDisposable : Disposable? = null
    var finishButton: Button? = null
    var payCashButton: Button? = null
    var phone: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_finishing)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        var container = findViewById(R.id.order_finishing_container) as LinearLayout
        for (order in order) {
            if (order.value > 0) {
                addDish(container, order.key, order.value)
            }
        }
        addDelimeter(container)
        phone = addPhone(container)
        var comment = addComment(container)

        addButtons(container, phone!!, comment)
        checkButtonsVisibility()
        var offline = toolbar.findViewById(R.id.offline)
        internetDisposable = Locator.internetInteractor.observeInternet()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!it) {
                        offline.visibility = View.VISIBLE
                    } else {
                        offline.visibility = View.GONE
                    }
                }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (internetDisposable != null) {
            internetDisposable!!.dispose()
        }
    }

    private fun addButtons(container: LinearLayout, phone: TextView, comment: TextView) {
        var inflater = layoutInflater ?: return
        val time = SimpleDateFormat("HH:mm").format(Date());
        val date = SimpleDateFormat("dd.MM.yyyy").format(Date())
        with(inflater.inflate(R.layout.order_finish_button, container, false) as Button) {
            container.addView(this)
            finishButton = this
            setOnClickListener {
                Locator.apiHelper.createOrder(Order(Order.tempId(),
                        OrderStage.NotPayed,
                        time,
                        date,
                        Locator.getCorrectNumber(
                                phone.text.toString()).
                                toString(),
                        comment.text.toString(),
                        Order.DEFAULT_TYPE,
                        emptyArray<PromocodeId>(),
                        Order.DEFAULT_GUEST_COUNT,
                        order.filter { it.value > 0 }), false)
                MainActivity.createWithoutHistory(this@OrderFinishingActivity, withNewOrder = true)
            }
        }
        with(inflater.inflate(R.layout.pay_cash_button, container, false) as Button) {
            container.addView(this)
            payCashButton = this
            setOnClickListener {
                Locator.apiHelper.createOrder(Order(Order.tempId(),
                        OrderStage.Payed,
                        time,
                        date,
                        Locator.getCorrectNumber(
                                phone.text.toString()).
                                toString(),
                        comment.text.toString(),
                        Order.DEFAULT_TYPE,
                        emptyArray<PromocodeId>(),
                        Order.DEFAULT_GUEST_COUNT,
                        order.filter { it.value > 0 }), true)
                MainActivity.createWithoutHistory(this@OrderFinishingActivity, withNewOrder = true)
            }
        }
    }

    private fun addComment(container: LinearLayout): EditText {
        var inflater = layoutInflater ?: throw IllegalStateException()
        val view = inflater.inflate(R.layout.icon_line, container, false)
        var imageView = ((view.findViewById(R.id.icon)) as ImageView)
        val drawable = resources.getDrawable(R.drawable.ic_chat_bubble_black_24dp)
        DrawableCompat.setTint(drawable, resources.getColor(R.color.icon_gray_color))
        imageView.setImageDrawable(drawable)
        with(view.findViewById(R.id.icon_line_comment_til) as TextInputLayout) {
            visibility = View.VISIBLE
        }
        container.addView(view)
        return view.findViewById(R.id.icon_line_comment_et) as EditText
    }

    private fun addPhone(container: LinearLayout): EditText {
        var inflater = layoutInflater ?: throw IllegalStateException()
        val view = inflater.inflate(R.layout.icon_line, container, false)
        var imageView = ((view.findViewById(R.id.icon)) as ImageView)
        val drawable = resources.getDrawable(R.drawable.ic_phone_black_24dp)
        DrawableCompat.setTint(drawable, resources.getColor(R.color.icon_gray_color))
        imageView.setImageDrawable(drawable)
        with(view.findViewById(R.id.icon_line_phone_til) as TextInputLayout) {
            visibility = View.VISIBLE
        }
        with(view.findViewById(R.id.icon_line_phone_et) as EditText) {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (isPhoneNumber(p0 ?: "")) {
                        hideKeyboard(this@OrderFinishingActivity)
                    }
                    checkButtonsVisibility()
                }

            })
            onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (!b) {
                    if (!isPhoneNumber(text) && !text.isNullOrEmpty()) {
                        error = "Неправильно введен номер телефона"
                    } else {
                        error = null
                    }
                }
            }
        }
        container.addView(view)
        return view.findViewById(R.id.icon_line_phone_et) as EditText
    }

    private fun checkButtonsVisibility() {
        val button = finishButton
        if (button != null) {
            with(button) {
                if (isPhoneNumber(phone?.text) && Locator.internetInteractor.isInternetConnected()) {
                    isEnabled = true
                    setBackgroundResource(R.color.lightBlue)
                    setTextColor(resources.getColor(R.color.white))
                } else {
                    isEnabled = false
                    setBackgroundResource(R.color.icon_gray_color)
                    setTextColor(resources.getColor(R.color.order_gray_text))
                }
            }
        }
        val pay_cash_button = payCashButton
        if (pay_cash_button != null) {
            with(pay_cash_button) {
                if (isPhoneNumber(phone?.text) || phone?.text.isNullOrEmpty()) {
                    isEnabled = true
                    setBackgroundResource(R.color.white)
                    setTextColor(resources.getColor(R.color.lightBlue))
                } else {
                    isEnabled = false
                    setBackgroundResource(R.color.white)
                    setTextColor(resources.getColor(R.color.order_gray_text))
                }
            }
        }
    }

    private fun addDelimeter(container: ViewGroup) {
        var sum = 0;
        for (dish in order) {
            sum += dish.value * dish.key.price.toInt()
        }
        val inflater = layoutInflater ?: return
        val view = inflater.inflate(R.layout.text_line, container, false)
        val textview = (view.findViewById(R.id.large_text) as TextView)
        textview.visibility = View.VISIBLE
        textview.text = applicationContext.getString(R.string.order_summary, sum)
        container.addView(view)
    }

    private fun addDish(container: ViewGroup, dish: Dish, i: Int) {
        val inflater = layoutInflater ?: return
        val view = inflater.inflate(R.layout.dish_line, container, false)
        (view.findViewById(R.id.order_count) as TextView).text = i.toString()
        (view.findViewById(R.id.order_text) as TextView).text = dish.name
        container.addView(view)
    }

    private fun isPhoneNumber(number: CharSequence?): Boolean {
        return (((number?.startsWith("+7") ?: false && number?.length == 12) || (number?.startsWith("8") ?: false && number?.length == 11)))
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
