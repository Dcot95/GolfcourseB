package ie.wit.golfcourseb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.databinding.CardGolfcourseBinding
import ie.wit.golfcourseb.models.GolfcourseModel
import ie.wit.golfcourseb.utils.customTransformation

interface GolfcourseClickListener {
    fun onGolfcourseClick(golfcourse: GolfcourseModel)
}

class GolfcourseAdapter constructor(private var golfcourses: ArrayList<GolfcourseModel>,
                                  private val listener: GolfcourseClickListener,
                                  private val readOnly: Boolean)
    : RecyclerView.Adapter<GolfcourseAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardGolfcourseBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding,readOnly)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val golfcourse = golfcourses[holder.adapterPosition]
        holder.bind(golfcourse,listener)
    }

    fun removeAt(position: Int) {
        golfcourses.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = golfcourses.size

    inner class MainHolder(val binding : CardGolfcourseBinding, private val readOnly : Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly

        fun bind(golfcourse: GolfcourseModel, listener: GolfcourseClickListener) {
            binding.root.tag = golfcourse
            binding.golfcourse = golfcourse
            Picasso.get().load(golfcourse.profilepic.toUri())
                .resize(200, 200)
                .transform(customTransformation())
                .centerCrop()
                .into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onGolfcourseClick(golfcourse) }
            binding.executePendingBindings()
        }
    }
}