package com.timper.module.view.emptyview

import android.databinding.BindingAdapter
import com.timper.bindingadapter.action.Command
import com.timper.view.EmptyLayout

object ViewBindingAdapter {

    @BindingAdapter("onRetry")
    fun retry(emptyLayout: EmptyLayout, command: Command?) {
        emptyLayout.setOnRefreshListener {
            command?.execute()
        }
    }

    @BindingAdapter(value = ["state"], requireAll = false)
    fun setStatus(emptyLayout: EmptyLayout, status: EmptyLayout.Status?) {
        if (status != null) {
            emptyLayout.setStatus(status)
        }
    }

    @BindingAdapter("emptyInfo")
    fun setEmptyInfo(emptyLayout: EmptyLayout, emptyInfo: String) {
        emptyLayout.setInfo(emptyInfo)
    }
}

