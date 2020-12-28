package com.soul.android.single.world

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.soul.android.single.world.bean.DemoItem

/**
 * @author : yueqi.zhou
 * @date : 2020-10-11 15:49
 * describe :
 */
class DemoItemAdapter : BaseQuickAdapter<DemoItem, BaseViewHolder>(R.layout.item_demo) {
    override fun convert(holder: BaseViewHolder, item: DemoItem) {
        holder.setImageResource(R.id.icon, item.iconRes)
        holder.setText(R.id.demoName, item.name)
    }
}