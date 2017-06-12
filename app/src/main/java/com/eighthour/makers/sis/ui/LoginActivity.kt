package com.eighthour.makers.sis.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.RequiresActivityViewModel
import com.eighthour.makers.sis.libs.util.addTextWatcher
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.util.showToast
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.libs.util.startActivityForResult
import com.eighthour.makers.sis.libs.util.startActivityWithFinish
import com.eighthour.makers.sis.viewmodels.LoginViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.onClick

@RequiresActivityViewModel(value = LoginViewModel::class)
class LoginActivity : BaseActivity<LoginViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as App).component.inject(this)

        editText_id.addTextWatcher({ viewModel.inPuts.id(it) })

        editText_password.addTextWatcher({ viewModel.inPuts.password(it) })

        button_login.onClick { it?.let { viewModel.inPuts.loginClick(Parameter.CLICK) } }

        button_register.onClick { it?.let { viewModel.goToRegisterClick(Parameter.CLICK) } }

        viewModel.outPuts.loginSuccess()
                .bindToLifecycle(this)
                .subscribe { startActivityWithFinish(MainActivity::class.java) }

        viewModel.outPuts.goToRegister()
                .bindToLifecycle(this)
                .subscribe { startActivityForResult(RegisterActivity::class.java, Intents.REQUEST_REGISTER) }

        viewModel.outPuts.setLoginButtonIsEnabled()
                .bindToLifecycle(this)
                .subscribe { button_login.isEnabled = it }

        viewModel.error
                .bindToLifecycle(this)
                .subscribe { showToast("알 수 없는 에러가 발생하였습니다.") }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Intents.REQUEST_REGISTER && resultCode == Activity.RESULT_OK)
            startActivityWithFinish(MainActivity::class.java)
    }
}