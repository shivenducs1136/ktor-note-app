package com.androiddevs.ktornoteapp.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseFragment(layoutid: Int): Fragment(layoutid) {
    fun showSnackbar(s:String){
        Snackbar.make(requireActivity().rootLayout,s,Snackbar.LENGTH_LONG).show()
    }

}