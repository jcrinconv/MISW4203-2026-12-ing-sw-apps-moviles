package com.misw.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.misw.app.R
import com.misw.app.viewmodel.HelloViewModel

class HelloFragment : Fragment() {

    private val viewModel: HelloViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_hello, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvGreeting = view.findViewById<TextView>(R.id.tv_greeting)

        viewModel.greeting.observe(viewLifecycleOwner) { message ->
            tvGreeting.text = message
        }
    }
}
