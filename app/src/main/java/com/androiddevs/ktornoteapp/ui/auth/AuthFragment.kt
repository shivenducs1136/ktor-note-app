package com.androiddevs.ktornoteapp.ui.auth

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.remote.BasicAuthIntercepter
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_PASSWORD
import com.androiddevs.ktornoteapp.other.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment:BaseFragment(R.layout.fragment_auth) {

    private val viewModel:AuthViewModel by viewModels()
    @Inject
    lateinit var shardPref:SharedPreferences

    @Inject
    lateinit var basicAuthIntercepter: BasicAuthIntercepter
    private var curEmail:String? = null
    private var curPassword:String? =null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isLoggedIn()){
            authenticateApi(curEmail?:"",curPassword?:"")
            redirectLogin()
        }
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        subscribeToObserver()
        btnRegister.setOnClickListener {
            val email = etRegisterEmail.text.toString()
            val password = etRegisterPassword.text.toString()
            val confirmPassword= etRegisterPasswordConfirm.text.toString()
            viewModel.register(email,password,confirmPassword)
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()
            curEmail = email
            curPassword = password
            viewModel.login(email,password)
        }

    }
    private fun authenticateApi(email: String, password:String){
        basicAuthIntercepter.email = email
        basicAuthIntercepter.password = password

    }
    private fun isLoggedIn(): Boolean {
        curEmail = shardPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL)?: NO_EMAIL
        curPassword = shardPref.getString(KEY_PASSWORD, NO_PASSWORD)?: NO_PASSWORD
        return curEmail!= NO_EMAIL && curPassword != NO_PASSWORD

    }
    private fun redirectLogin(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment,true)
            .build()
        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToNotesFragment(),navOptions)
    }
    private fun subscribeToObserver(){
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer {result->
            result?.let {
                when(result.status){
                    Status.SUCCESS ->{
                        showSnackbar(result.data?:"Successfully logged in")
                        loginProgressBar.visibility = View.GONE

                        shardPref.edit().putString(KEY_LOGGED_IN_EMAIL,curEmail).apply()
                        shardPref.edit().putString(KEY_PASSWORD,curPassword).apply()
                        authenticateApi(curEmail?:"",curPassword?:"")
                        redirectLogin()
                    }
                    Status.ERROR ->{
                        showSnackbar(result.message?:"An unknown error occurred")
                        loginProgressBar.visibility = View.GONE
                    }
                    Status.LOADING ->{
                        loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
        viewModel.registerStatus.observe(viewLifecycleOwner, Observer {result->

            result?.let {
                when(result.status){
                    Status.SUCCESS ->{
                        registerProgressBar.visibility =View.GONE
                        showSnackbar(result.data?:"Successfully registered an account")
                    }
                    Status.ERROR->{
                        registerProgressBar.visibility = View.GONE
                        showSnackbar((result.message?:"An unknown error occurred "))
                    }
                    Status.LOADING->{
                        registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }

        })
    }
}