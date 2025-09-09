package com.rio.rostry.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(@LayoutRes private val layoutId: Int) : Fragment(layoutId) {
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: error("Binding not initialized")

    abstract fun bind(view: View): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = bind(view)
        onBind()
    }

    open fun onBind() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
