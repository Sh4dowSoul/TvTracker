package de.schnettler.tvtracker.ui.account

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.tvtracker.ui.AuthViewModel
import de.schnettler.tvtracker.data.api.TraktAPI
import de.schnettler.tvtracker.databinding.AccountFragmentBinding
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull


@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AccountFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = authViewModel

        authViewModel.startAuthentication.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val authorizeUrl = (TraktAPI.BASE_URL + "oauth/authorize").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter(
                        "client_id",
                        TraktAPI.CLIENT_ID
                    )
                    ?.addQueryParameter("redirect_uri", "de.schnettler.tvtrack://auth")
                    ?.addQueryParameter("response_type", "code")
                    ?.build();

                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                context?.let {ctx -> customTabsIntent.launchUrl(
                    ctx, Uri.parse(authorizeUrl?.toUri().toString())
                ) }
                authViewModel.onLoginHandled()
            }

        })
        return binding.root
    }
}