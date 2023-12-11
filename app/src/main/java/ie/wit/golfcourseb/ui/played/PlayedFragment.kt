package ie.wit.golfcourseb.ui.played

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.adapters.GolfcourseAdapter
import ie.wit.golfcourseb.adapters.GolfcourseClickListener
import ie.wit.golfcourseb.databinding.FragmentPlayedBinding
import ie.wit.golfcourseb.main.GolfcourseBApp
import ie.wit.golfcourseb.models.GolfcourseModel
import ie.wit.golfcourseb.ui.auth.LoggedInViewModel
import ie.wit.golfcourseb.utils.*
import timber.log.Timber

class PlayedFragment : Fragment(), GolfcourseClickListener {

    private var _fragBinding: FragmentPlayedBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader : AlertDialog
    private val playedViewModel: PlayedViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentPlayedBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = PlayedFragmentDirections.actionPlayedFragmentToCourseFragment()
            findNavController().navigate(action)
        }
        showLoader(loader,"Downloading Golfcourses")
        playedViewModel.observableGolfcoursesList.observe(viewLifecycleOwner, Observer {
                golfcourses ->
            golfcourses?.let {
                render(golfcourses as ArrayList<GolfcourseModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader,"Deleting Golfcourse")
                val adapter = fragBinding.recyclerView.adapter as GolfcourseAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                playedViewModel.delete(playedViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as GolfcourseModel).uid!!)

                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onGolfcourseClick(viewHolder.itemView.tag as GolfcourseModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
    }


    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_played, menu)

                val item = menu.findItem(R.id.toggleGolfcourses) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val toggleGolfcourses: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                toggleGolfcourses.isChecked = false

                toggleGolfcourses.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) playedViewModel.loadAll()
                    else playedViewModel.load()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(golfcoursesList: ArrayList<GolfcourseModel>) {
        fragBinding.recyclerView.adapter = GolfcourseAdapter(golfcoursesList,this,
            playedViewModel.readOnly.value!!)
        if (golfcoursesList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.golfcoursesNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.golfcoursesNotFound.visibility = View.GONE
        }
    }

    override fun onGolfcourseClick(golfcourse: GolfcourseModel) {
        val action = PlayedFragmentDirections.actionPlayedFragmentToGolfcourseDetailFragment(golfcourse.uid!!)
        if(!playedViewModel.readOnly.value!!)
            findNavController().navigate(action)
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader,"Downloading Golfcourses")
            if(playedViewModel.readOnly.value!!)
                playedViewModel.loadAll()
            else
                playedViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader,"Downloading Golfcourses")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                playedViewModel.liveFirebaseUser.value = firebaseUser
                playedViewModel.load()
            }
        })
        //hideLoader(loader)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}