package com.eighthour.makers.sis.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.RequiresActivityViewModel
import com.eighthour.makers.sis.libs.util.addTextWatcher
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.util.showToast
import com.eighthour.makers.sis.viewmodels.RegisterViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.onClick

@RequiresActivityViewModel(value = RegisterViewModel::class)
class RegisterActivity : BaseActivity<RegisterViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        (application as App).component.inject(this)
        actionbarInit(toolbar)

        val upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)

        editText_id.addTextWatcher({ viewModel.inPuts.id(it) })

        editText_password.addTextWatcher({ viewModel.inPuts.password(it) })

        editText_password_confirm.addTextWatcher({ viewModel.inPuts.passwordConfirm(it) })

        editText_name.addTextWatcher({ viewModel.inPuts.name(it) })

        button_register.onClick { viewModel.inPuts.registerClick(Parameter.CLICK) }

        viewModel.outPuts.setRegisterButtonIsEnabled()
                .bindToLifecycle(this)
                .subscribe { button_register.isEnabled = it }

        viewModel.outPuts.registerSuccess()
                .bindToLifecycle(this)
                .subscribe {
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

        viewModel.error
                .bindToLifecycle(this)
                .subscribe { showToast("알 수 없는 에러.") }
    }
}