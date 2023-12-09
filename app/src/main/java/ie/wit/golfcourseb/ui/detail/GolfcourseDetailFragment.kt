package ie.wit.golfcourseb.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import ie.wit.golfcourseb.R

class GolfcourseDetailFragment : Fragment() {
    companion object {
        fun newInstance() = GolfcourseDetailFragment()
    }

    private lateinit var viewModel: GolfcourseDetailViewModel
    private val args by navArgs<GolfcourseDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_golfcourse_detail, container, false)

        Toast.makeText(context,"Golfcourse ID Selected : ${args.golfcourseid}", Toast.LENGTH_LONG).show()

        return view
    }


}