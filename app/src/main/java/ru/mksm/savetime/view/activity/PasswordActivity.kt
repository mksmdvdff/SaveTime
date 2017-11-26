package ru.mksm.savetime.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.mksm.savetime.R
import ru.mksm.savetime.util.ActivityCompanion
import ru.mksm.savetime.util.Locator

class PasswordActivity : AppCompatActivity() {

    private var disposable : Disposable? = null

    companion object : ActivityCompanion(PasswordActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        if (Locator.accountInteractor.id.isNotEmpty()) {
            LoadingActivity.create(this)
            if (disposable != null) {
                disposable!!.dispose()
            }
        }
        findViewById(R.id.progress_bar).visibility = GONE
        findViewById(R.id.registration_content).visibility = VISIBLE
        findViewById(R.id.pass_one_more_button).setOnClickListener {
            Locator.accountInteractor.logout()
            RegistrationActivity.createWithoutHistory(this)
        }
        findViewById(R.id.pass_next_button).setOnClickListener {
            if (Locator.accountInteractor.id.isEmpty()) {
                findViewById(R.id.progress_bar).visibility = VISIBLE
                disposable = Locator.apiHelper.getSessionId(
                        (findViewById(R.id.login_pass_et) as EditText).text.toString())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally { findViewById(R.id.progress_bar).visibility = GONE }
                        .map { if (it.error == null) it else null }
                        .subscribe ({
                            Locator.accountInteractor.id = it?.clientId!!
                            Locator.accountInteractor.token = it?.token!!
                            Locator.accountInteractor.provider = it?.provider!!
                            LoadingActivity.create(this)
                        }, {
                            Toast.makeText(this, "Что-то пошло не так. Попробуйте еще раз",
                                    Toast.LENGTH_SHORT).show()
                        })
            } else {
                LoadingActivity.create(this)
            }
        }
    }

}
