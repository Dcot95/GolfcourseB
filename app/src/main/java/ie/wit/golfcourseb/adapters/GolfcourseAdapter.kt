package ie.wit.golfcourseb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.databinding.CardGolfcourseBinding
import ie.wit.golfcourseb.models.GolfcourseModel

interface GolfcourseClickListener {
    fun onGolfcourseClick(golfcourse: GolfcourseModel)
}

class GolfcourseAdapter constructor(private var golfcourses: List<GolfcourseModel>,
                                  private val listener: GolfcourseClickListener)
    : RecyclerView.Adapter<GolfcourseAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardGolfcourseBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val golfcourse = golfcourses[holder.adapterPosition]
        holder.bind(golfcourse,listener)
    }

    override fun getItemCount(): Int = golfcourses.size

    inner class MainHolder(val binding : CardGolfcourseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(golfcourse: GolfcourseModel, listener: GolfcourseClickListener) {
//            binding.paymentamount.text = golfcourse.amount.toString()
//            binding.paymentmethod.text = golfcourse.paymentmethod

            binding.golfcourse = golfcourse
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onGolfcourseClick(golfcourse) }
            binding.executePendingBindings()
        }
    }
}