package com.eighthour.makers.sis.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.RequiresActivityViewModel
import com.eighthour.makers.sis.libs.network.model.Topic
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.util.setImageWithGlide
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.libs.util.startActivity
import com.eighthour.makers.sis.libs.util.startActivityWithFinish
import com.eighthour.makers.sis.viewmodels.MainViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.viewholder_main_topic.view.*
import org.jetbrains.anko.onClick

@RequiresActivityViewModel(value = MainViewModel::class)
class MainActivity : BaseActivity<MainViewModel>() {

    private val adapter by lazy { RecyclerAdapter({ it -> viewModel.inPuts.clickToTopicItem(it) }) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (applicationContext as? App)?.component?.inject(this)
        actionbarInit(toolbar, isEnableNavi = false)
        recyclerViewInit()

        clickOptionItems.filter { it.itemId == R.id.action_like }
                .map { Parameter.CLICK }
                .bindToLifecycle(this)
                .subscribe { viewModel.inPuts.clickToLike(it) }

        clickOptionItems.filter { it.itemId == R.id.action_chat }
                .map { Parameter.CLICK }
                .bindToLifecycle(this)
                .subscribe { viewModel.inPuts.clickToChat(it) }

        clickOptionItems.filter { it.itemId == R.id.action_logout }
                .map { Parameter.CLICK }
                .bindToLifecycle(this)
                .subscribe { viewModel.inPuts.clickToLogout(it) }

        viewModel.outPuts.goChatRoom()
                .bindToLifecycle(this)
                .subscribe { startActivity(Intent(this, ChatRoomActivity::class.java).putExtra(Intents.TOPIC_ID, 1L)) }

        viewModel.outPuts.goChildTopics()
                .bindToLifecycle(this)
                .subscribe { startActivity(Intent(this, ChildTopicListActivity::class.java).putExtra(Intents.TOPIC_ID, it)) }

        viewModel.outPuts.onSuccessRefresh()
                .bindToLifecycle(this)
                .subscribe {
                    adapter.datas.clear()
                    adapter.datas.addAll(it)
                    adapter.notifyDataSetChanged()
                }

        viewModel.outPuts.goLogin()
                .bindToLifecycle(this)
                .subscribe { startActivityWithFinish(LoginActivity::class.java) }

        viewModel.outPuts.goToLikes()
                .bindToLifecycle(this)
                .subscribe { startActivity(LikeTopicsActivity::class.java) }

        viewModel.error
                .bindToLifecycle(this)
                .subscribe {
                    it.printStackTrace()
                    finish()
                }
    }

    private fun recyclerViewInit() =
            with(recyclerView) {
                layoutManager = GridLayoutManager(this@MainActivity, 2)
                adapter = this@MainActivity.adapter
            }


    override fun onCreateOptionsMenu(menu: android.view.Menu?): kotlin.Boolean =
            super.onCreateOptionsMenu(menu.apply { menuInflater.inflate(R.menu.menu_main, this) })

    private class RecyclerAdapter(val onClickItem: (Long) -> Unit) : RecyclerView.Adapter<RecyclerAdapter.TopicViewHolder>() {

        var datas = mutableListOf<Topic>()

        override fun getItemCount(): Int = datas.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TopicViewHolder =
                TopicViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.viewholder_main_topic, parent, false))

        override fun onBindViewHolder(holder: TopicViewHolder, position: Int) =
                with(holder.itemView) {
                    val topic = datas[position]
                    textView_title.text = topic.name
                    imageView_background.setImageWithGlide(topic.backgroundImage)
                    onClick { onClickItem(topic.id) }
                }

        private class TopicViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}