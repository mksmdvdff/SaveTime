package ru.mksm.savetime.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import io.reactivex.disposables.Disposable
import ru.mksm.savetime.R
import ru.mksm.savetime.util.ActivityCompanion
import ru.mksm.savetime.util.Locator
import java.util.concurrent.TimeUnit

class LoadingActivity : AppCompatActivity() {

    @Volatile private var disposable: Disposable? = null

    companion object : ActivityCompanion(LoadingActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_activity)
        val progressBar = findViewById(R.id.progress_bar)
        Locator.dishesInteractor.getAllDishes()
        disposable = Locator.dishesInteractor.requestDishes()
                        .flatMap { Locator.orderTypeInteractor.requestOrderTypes() }
                        .flatMap { Locator.promocodeInteractor.requestPromocodes() }
                        .flatMap { Locator.ordersInteractor.requestOrders() }
                        .timeout(45, TimeUnit.SECONDS).subscribe(
                {
                    this.disposable?.dispose()
                    MainActivity.createWithoutHistory(this, false, false)
                }, {
            progressBar.post {
                Toast.makeText(this,
                        "Что-то пошло нет так. " +
                                "Проверьте наличие интернета или попробуйте позднее",
                        Toast.LENGTH_LONG).show()
                Log.e("MKSM", "loading", it)
                Locator.accountInteractor.logout()
                RegistrationActivity.createWithoutHistory(this)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
