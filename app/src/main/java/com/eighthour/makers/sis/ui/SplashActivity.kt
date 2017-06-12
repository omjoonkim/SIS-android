package com.eighthour.makers.sis.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.CurrentUser
import com.eighthour.makers.sis.libs.util.startActivityWithFinish
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {


    @Inject lateinit var currentUser: CurrentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        (applicationContext as? App)?.component?.inject(this)
        Completable.complete()
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribe({ startActivityWithFinish(if (currentUser.user != null) MainActivity::class.java else LoginActivity::class.java) })
    }
}