package ie.wit.golfcourseb.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ie.wit.golfcourseb.databinding.FragmentGolfcourseDetailBinding
import ie.wit.golfcourseb.ui.auth.LoggedInViewModel
import ie.wit.golfcourseb.ui.played.PlayedViewModel
import timber.log.Timber


class GolfcourseDetailFragment : Fragment() {

    private lateinit var detailViewModel: GolfcourseDetailViewModel
    private val args by navArgs<GolfcourseDetailFragmentArgs>()
    private var _fragBinding: FragmentGolfcourseDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val playedViewModel : PlayedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentGolfcourseDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(GolfcourseDetailViewModel::class.java)
        detailViewModel.observableGolfcourse.observe(viewLifecycleOwner, Observer { render() })

        fragBinding.editGolfcourseButton.setOnClickListener {
            detailViewModel.updateGolfcourse(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                args.golfcourseid, fragBinding.golfcoursevm?.observableGolfcourse!!.value!!)
            findNavController().navigateUp()
        }

        fragBinding.deleteGolfcourseButton.setOnClickListener {
            playedViewModel.delete(loggedInViewModel.liveFirebaseUser.value?.email!!,
                detailViewModel.observableGolfcourse.value?.uid!!)
            findNavController().navigateUp()
        }

        return root
    }

    private fun render() {
        fragBinding.editMessage.setText("A Message")
        fragBinding.editUpvotes.setText("0")
        fragBinding.golfcoursevm = detailViewModel
        Timber.i("Retrofit fragBinding.golfcoursevm == $fragBinding.golfcoursevm")
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getGolfcourse(loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.golfcourseid)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}