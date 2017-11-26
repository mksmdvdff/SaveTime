package ru.mksm.savetime.view.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.mksm.savetime.R
import ru.mksm.savetime.util.ActivityCompanion
import ru.mksm.savetime.util.Locator

class RegistrationActivity : AppCompatActivity() {

    companion object : ActivityCompanion(RegistrationActivity::class.java) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Locator.accountInteractor.id.isNotEmpty()) {
            LoadingActivity.createWithoutHistory(this)
        }
        setContentView(R.layout.activity_registration);
        val next = findViewById(R.id.login_next) as Button;
        val phoneET = findViewById(R.id.login_phone_et) as EditText;
        with(next) {
            setOnClickListener {
                val phone = phoneET.text.toString();
                setButtonState(false)
                Locator.apiHelper.
                        sendPhone(phone)
                        .subscribeOn(Schedulers.computation())
                        .map {
                            it.result}
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally {
                            setButtonState(true)
                        }
                        .subscribe({
                            Locator.accountInteractor.name = "SaveTime Местная Еда"
                            Locator.accountInteractor.setPhoneNumber(phone)
                            PasswordActivity.create(this.context)
                        }, {
                            Toast.makeText(this.context, "Что-то пошло не так. Попробуйте еще раз",
                                    Toast.LENGTH_SHORT).show()
                        })

            }
        }

        with(phoneET) {
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
                    checkButtonVisibility(p0 ?: "")
                    if (isPhoneNumber(p0 ?: "")) {
                        hideKeyboard(this@RegistrationActivity)
                    }
                }

            })
            setText(Locator.accountInteractor.number)
        }
    }


    private fun checkButtonVisibility(text: CharSequence) {
        setButtonState(isPhoneNumber(text))
    }

    private fun setButtonState(enabled: Boolean) {
        with(findViewById(R.id.login_next) as Button) {
            if (enabled) {
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
