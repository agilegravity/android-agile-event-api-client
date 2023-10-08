package com.agilegravity.event_api_client_demo

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BricksAdapter(private var bricks: List<APIClient.Brick>) : RecyclerView.Adapter<BricksAdapter.BrickViewHolder>() {

    class BrickViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentTextView: TextView = view.findViewById(R.id.contentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrickViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.brick_item, parent, false)
        return BrickViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrickViewHolder, position: Int) {
        if(bricks[position].content.data.get("content") != null){
            holder.contentTextView.text = bricks[position].content.data.get("content").toString()
        }

    }

    override fun getItemCount() = bricks.size

    fun updateBricks(newBricks: List<APIClient.Brick>) {
        this.bricks += newBricks
        notifyDataSetChanged()
    }
}
