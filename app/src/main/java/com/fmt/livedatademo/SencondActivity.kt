package com.fmt.livedatademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fmt.livedatabus.LiveDataBus
import kotlinx.android.synthetic.main.activity_sencond.*

class SencondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sencond)

        LiveDataBus.with<String>("name").observeStick(this, {
            tv_text.text = it
        })

        LiveDataBus.with<User>("login").observe(this, {
            tv_text.text = it.name
        })

        LiveDataBus.with<User>("login").observe(this, {
            tv_text1.text = it.name
        })
    }

    fun go(view: View) {
        val intent = Intent(this, ThirdActivity::class.java)
        startActivity(intent)
    }
}