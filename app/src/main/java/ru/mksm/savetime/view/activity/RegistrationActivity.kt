package ru.mksm.savetime.view.activity

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import ru.mksm.savetime.R
import ru.mksm.savetime.util.ActivityCompanion
import ru.mksm.savetime.util.Locator

class RegistrationActivity : AppCompatActivity() {

    companion object : ActivityCompanion(RegistrationActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Locator.registrationInteractor.name.isNotEmpty()) {
            MainActivity.create(this)
        }
        setContentView(R.layout.activity_registration);
        findViewById(R.id.login_next).setOnClickListener {
            Locator.registrationInteractor.name = "Александр Нечаев"
            Locator.registrationInteractor.number = (findViewById(R.id.login_phone_et) as EditText).text.toString()
            MainActivity.create(this)
        }
        with(findViewById(R.id.login_phone_et) as EditText) {
            onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (!b && !isPhoneNumber(text)) {
                    error = "error"
                }
                checkButtonVisibility(text)
            }
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    checkButtonVisibility(p0?:"")
                    if (isPhoneNumber(p0?:"")) {
                        hideKeyboard(this@RegistrationActivity)
                    }
                }

            })
        }
    }

    private fun  checkButtonVisibility(text: CharSequence) {
            with(findViewById(R.id.login_next) as Button) {
                if (isPhoneNumber(text)) {
                    isEnabled = true
                    setBackgroundResource(ru.mksm.savetime.R.color.lightBlue)
                    setTextColor(resources.getColor(ru.mksm.savetime.R.color.white))
                } else {
                    isEnabled = false
                    setBackgroundResource(ru.mksm.savetime.R.color.icon_gray_color)
                    setTextColor(resources.getColor(ru.mksm.savetime.R.color.order_gray_text))
                }
            }
    }

    private fun isPhoneNumber(number: CharSequence): Boolean {
        return (((number.startsWith("+7") && number.length == 12) || (number.startsWith("8") && number.length == 11)))
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
