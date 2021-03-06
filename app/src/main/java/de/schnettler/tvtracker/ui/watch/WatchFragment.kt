package de.schnettler.tvtracker.ui.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.schnettler.tvtracker.databinding.WatchFragmentBinding
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel

class WatchFragment : Fragment() {

    private lateinit var viewModel: DiscoverViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = WatchFragmentBinding.inflate(inflater)
        //viewModel = ViewModelProviders.of(this).get(DiscoverViewModel::class.java)
        return binding.root
    }
}
