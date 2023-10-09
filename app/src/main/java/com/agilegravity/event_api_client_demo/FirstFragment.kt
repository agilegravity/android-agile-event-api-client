package com.agilegravity.event_api_client_demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agilegravity.event_api_client_demo.databinding.FragmentFirstBinding
import com.agilegravity.event_api_client_demo.APIClient

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val view = _binding!!.root
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val bricksAdapter = BricksAdapter(listOf())
        recyclerView.adapter = bricksAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val eventApiClient =  APIClient("<YOUR_CHANNEL_ID>", "<YOUR_API_SECRET>")



        val editText: EditText = view.findViewById(R.id.editText)
        val sendButton: Button = view.findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val inputText = editText.text.toString()

            if (inputText.isNotEmpty()) {
                val event = listOf(APIClient.EventBody(name = "prompt", text = inputText, payload =
                APIClient.EventPayload(eventCategory = "userMesssgage", eventAction = "Send", source = "AndroidApp")))

                eventApiClient.eventAPICall(event) { bricks, error ->
                    activity?.runOnUiThread {
                        if (error != null) {
                            println("Error: $error")
                        } else if (bricks != null) {

                            bricksAdapter.updateBricks(bricks)
                        }
                    }
                }
            }
            editText.text.clear()
        }




        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // binding.buttonFirst.setOnClickListener {
        //    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        //}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}