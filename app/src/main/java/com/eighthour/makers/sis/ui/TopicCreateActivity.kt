package com.eighthour.makers.sis.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.RequiresActivityViewModel
import com.eighthour.makers.sis.libs.util.addTextWatcher
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.util.setImageWithGlide
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.viewmodels.TopicCreateViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_create_topic.*
import org.jetbrains.anko.onClick
import java.util.concurrent.TimeUnit


@RequiresActivityViewModel(TopicCreateViewModel::class)
class TopicCreateActivity : BaseActivity<TopicCreateViewModel>() {

    private var menuItem_confirm: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_topic)
        (applicationContext as? App)?.component?.inject(this)
        actionbarInit(toolbar, title = "새로운 토픽 생성")

        editText_name.addTextWatcher({ viewModel.inPuts.name(it) })

        clickOptionItems.filter { it.itemId == R.id.action_create }
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .bindToLifecycle(this)
                .subscribe { viewModel.inPuts.clickCreate(Parameter.CLICK) }

        textView_image.onClick {
            Album.album(this)
                    .requestCode(Intents.REQUEST_SELECT_PHOTO)
                    .toolBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .statusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .navigationBarColor(ActivityCompat.getColor(this, R.color.white))
                    .selectCount(1)
                    .columnCount(3)
                    .camera(true)
                    .start()
        }

        viewModel.outPuts.image()
                .bindToLifecycle(this)
                .subscribe { imageView_background.setImageWithGlide(it) }

        viewModel.outPuts.parentName()
                .bindToLifecycle(this)
                .subscribe { textView_help.text = "'$it'의 하위 채팅방을 생성합니다. " }

        viewModel.outPuts.enableCreateButton()
                .bindToLifecycle(this)
                .subscribe { menuItem_confirm?.isEnabled = it }

        viewModel.outPuts.successCreatedTopic()
                .bindToLifecycle(this)
                .subscribe { finish() }

        viewModel.outPuts.nameLength()
                .bindToLifecycle(this)
                .subscribe { textView_length.text = it.toString() }

        viewModel.outPuts.showLoadingDialog()
                .bindToLifecycle(this)
                .subscribe { loadingDialog.show() }

        viewModel.error.bindToLifecycle(this)
                .subscribe { loadingDialog.hide() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
            super.onCreateOptionsMenu(menu.apply {
                menuInflater.inflate(R.menu.menu_create_topic, menu)
                menuItem_confirm = menu?.findItem(R.id.action_create)
            })

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == Intents.REQUEST_SELECT_PHOTO)
            if (resultCode == RESULT_OK)
                Album.parseResult(data).firstOrNull()?.let { viewModel.inPuts.image(it) }
    }
}