package ie.wit.golfcourseb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.databinding.CardGolfcourseBinding
import ie.wit.golfcourseb.models.GolfcourseModel

class GolfcourseAdapter constructor(private var golfcourses: List<GolfcourseModel>)
    : RecyclerView.Adapter<GolfcourseAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardGolfcourseBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val golfcourse = golfcourses[holder.adapterPosition]
        holder.bind(golfcourse)
    }

    override fun getItemCount(): Int = golfcourses.size

    inner class MainHolder(val binding : CardGolfcourseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(golfcourse: GolfcourseModel) {
            binding.paymentamount.text = golfcourse.amount.toString()
            binding.paymentmethod.text = golfcourse.paymentmethod
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
        }
    }
}