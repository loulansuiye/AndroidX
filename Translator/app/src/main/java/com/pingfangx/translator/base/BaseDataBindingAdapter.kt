package com.pingfangx.translator.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pingfangx.translator.R

/**
 * DataBinding的Adapter
 *
 * @author 刘汝寿
 * @date 2017/9/10
 */
abstract class BaseDataBindingAdapter<Bean, VH : BaseDataBindingViewHolder<Bean>>(context: Context, data: List<Bean>) : BaseAdapter<Bean, VH>(context, data) {
    var mOnItemClickListener: BaseViewHolder.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val inflator: LayoutInflater = LayoutInflater.from(mContext)
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflator, R.layout.item_project, parent, false)
        val holder = onCreateViewHolder(binding)
        mOnItemClickListener?.let {
            holder.mOnItemClickListener = mOnItemClickListener
        }
        return holder
    }

    abstract fun onCreateViewHolder(binding: ViewDataBinding): VH
}