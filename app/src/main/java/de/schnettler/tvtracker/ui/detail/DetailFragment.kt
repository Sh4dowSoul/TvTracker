package de.schnettler.tvtracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.schnettler.tvtracker.MainActivity
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.DetailFragmentBinding
import de.schnettler.tvtracker.util.*
import dev.chrisbanes.insetter.doOnApplyWindowInsets


class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var binding: DetailFragmentBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Shared Element Enter
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        binding = DetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val args = DetailFragmentArgs.fromBundle(arguments!!)
        val show = args.show
        viewModel = ViewModelProviders.of(this, DetailViewModel.Factory(show, this.activity!!.application)).get(DetailViewModel::class.java)
        binding.viewModel = viewModel

        val controller = DetailTypedController()
        val recycler = binding.recyclerView
        recycler.adapter = controller.adapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewModel.observeState(this) {
            controller.setData(it)
        }

        //StatusBar Icon Color
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangedListener() {
            override fun onStateChanged(state: AppBarStateChangedListener.State) {
                when(state) {
                    State.COLLAPSED -> {
                        if (!isDarkTheme(resources)) {
                            setLightStatusBar(activity!!.window.decorView)
                        }
                    }
                    State.EXPANDED -> clearLightStatusBar(activity!!.window.decorView)
                }
            }
        })

        //WindowInsets
        binding.recyclerView.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }

        if(activity is MainActivity){
            val ac = activity as MainActivity
            ac.setSupportActionBar(binding.toolbar)
            ac.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        //Click Listener Callback
        controller.callbacks = object: DetailTypedController.Callbacks {
            override fun onEpisodeClicked(episode: EpisodeDomain) {
                viewModel.onEpisodeSelected(episode)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }

            override fun onSeasonClicked(season: SeasonDomain, isExpanded: Boolean) {
                viewModel.onChangeSeasonExpansion(season, !isExpanded)
            }

            override fun onShowClicked(view: View, item: ShowDomain) {
                val extras = FragmentNavigatorExtras(
                    view to "showPoster"
                )
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentSelf(item, view.transitionName), extras)
            }

        }
        return binding.root
    }
}
