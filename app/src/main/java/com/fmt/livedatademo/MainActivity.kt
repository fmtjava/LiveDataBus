package com.fmt.livedatademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fmt.livedatabus.LiveDataBus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LiveDataBus.with<User>("login").observe(this, {
            tv_text.text = it.name
        })

    }

    fun go(view: View) {
        val intent = Intent(this, SencondActivity::class.java)
        startActivity(intent)
    }

    fun sendStick(view: View) {
        LiveDataBus.with<String>("name").postStickData("fmt Stick from Main")
    }

    fun sendData(view: View) {
        val user = User("fmt sendData from Main")
        LiveDataBus.with<User>("login").postData(user)
    }

}