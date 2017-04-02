package ru.mksm.savetime.view.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ru.mksm.savetime.R

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration);
        setTitle("Экран регистрации в будущем")


        val button : Button = findViewById(R.id.registration_ok_button) as Button
        button.setOnClickListener {
            MainActivity.create(this)
        }
    }
}
