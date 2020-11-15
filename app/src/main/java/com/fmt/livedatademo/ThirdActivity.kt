package com.fmt.livedatademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fmt.livedatabus.LiveDataBus
import kotlinx.android.synthetic.main.activity_sencond.*

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        LiveDataBus.with<User>("login").observe(this, {
            tv_text.text = it.name
        })
    }

    fun sendData(view: View) {
        val user = User("fmt java")
        LiveDataBus.with<User>("login").postData(user)
    }
}