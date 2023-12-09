package ie.wit.golfcourseb.ui.played

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.adapters.GolfcourseAdapter
import ie.wit.golfcourseb.adapters.GolfcourseClickListener
import ie.wit.golfcourseb.databinding.FragmentPlayedBinding
import ie.wit.golfcourseb.main.GolfcourseBApp
import ie.wit.golfcourseb.models.GolfcourseModel

class PlayedFragment : Fragment(), GolfcourseClickListener {

    lateinit var app: GolfcourseBApp
    private var _fragBinding: FragmentPlayedBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var playedViewModel: PlayedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentPlayedBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        //activity?.title = getString(R.string.action_played)
        setupMenu()
        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        playedViewModel = ViewModelProvider(this).get(PlayedViewModel::class.java)
        playedViewModel.observableGolfcoursesList.observe(viewLifecycleOwner, Observer {
                golfcourses ->
            golfcourses?.let { render(golfcourses) }
        })

        val fab: FloatingActionButton = fragBinding.fab
        fab.setOnClickListener {
            val action = PlayedFragmentDirections.actionPlayedFragmentToCourseFragment()
            findNavController().navigate(action)
        }
        return root
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_played, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(golfcoursesList: List<GolfcourseModel>) {
        fragBinding.recyclerView.adapter = GolfcourseAdapter(golfcoursesList,this)
        if (golfcoursesList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.golfcoursesNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.golfcoursesNotFound.visibility = View.GONE
        }
    }

    override fun onGolfcourseClick(golfcourse: GolfcourseModel) {
        val action = PlayedFragmentDirections.actionPlayedFragmentToGolfcourseDetailFragment(golfcourse.id)
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        playedViewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}