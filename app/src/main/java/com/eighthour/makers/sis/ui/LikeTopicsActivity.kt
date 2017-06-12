package com.eighthour.makers.sis.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.RequiresActivityViewModel
import com.eighthour.makers.sis.libs.util.findWithIndex
import com.eighthour.makers.sis.libs.model.Topic
import com.eighthour.makers.sis.libs.util.setImageWithGlide
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.viewmodels.LikeTopicsViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_like_topics.*
import kotlinx.android.synthetic.main.viewholder_like_topics.view.*
import org.jetbrains.anko.onClick

@RequiresActivityViewModel(LikeTopicsViewModel::class)
class LikeTopicsActivity : BaseActivity<LikeTopicsViewModel>() {

    private val adapter by lazy {
        RecyclerAdapter()
                .apply {
                    onClickItem = { viewModel.inPuts.clickTopic(it) }
                    onLikeTopic = { viewModel.inPuts.likeTopic(it) }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_topics)
        (applicationContext as? App)?.component?.inject(this)
        actionbarInit(toolbar, title = "즐겨찾기", naviColor = R.color.white)
        recyclerViewInit()

        viewModel.outPuts.goToTopic()
                .bindToLifecycle(this)
                .subscribe { startActivity(Intent(this, ChildTopicListActivity::class.java).putExtra(Intents.TOPIC_ID, it)) }

        viewModel.outPuts.onLoadTopics()
                .bindToLifecycle(this)
                .subscribe {
                    adapter.datas.clear()
                    adapter.datas.addAll(it)
                    adapter.notifyDataSetChanged()
                }

        viewModel.outPuts.onLikeTopic()
                .bindToLifecycle(this)
                .subscribe { (first, second) ->
                    adapter.datas.findWithIndex { it.id == first }?.let {
                        it.first.isFavorite = second
                        adapter.notifyItemChanged(it.second)
                    }
                }
    }

    private fun recyclerViewInit() =
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@LikeTopicsActivity)
                adapter = this@LikeTopicsActivity.adapter
            }

    private class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

        val datas = mutableListOf<Topic>()

        var onClickItem: (Long) -> Unit = {}
        var onLikeTopic: (Long) -> Unit = {}

        override fun getItemCount(): Int = datas.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerAdapter.ViewHolder =
                ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.viewholder_like_topics, parent, false))

        override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
            with(holder.itemView) {
                val data = datas[position]
                title.text = data.name
                subTitle.text = "${data.subtopic_num}개 아메바"
                image.setImageWithGlide(data.backgroundImage)
                like.setImageResource(if (data.isFavorite) R.drawable.ic_like_filled else R.drawable.ic_like)
                onClick { onClickItem(data.id) }
                like.onClick { onLikeTopic(data.id) }
            }
        }

        private class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}