package com.example.a4starter

import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.codebot.listview.GesturesAdapter
import net.codebot.listview.MatchedGestureAdaptor
import java.util.ArrayList

class HomeFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null
    var drawingView: CanvasViewForRecognition? = null
    private var adaptor: MatchedGestureAdaptor? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle? ): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        --------------
//        drawingView = root.findViewById(R.id.canvasForHome)
//        drawingView!!.addSharedViewModel(mViewModel!!)
//        ---------
        drawingView = CanvasViewForRecognition(context)
        drawingView!!.addSharedViewModel(mViewModel!!)
        val layoutView = root.findViewById<LinearLayout>(R.id.canvasForHome)
        layoutView.addView(drawingView)
        layoutView.isEnabled = true
//        --------
        val clearButton = root.findViewById<Button>(R.id.clearButtonHome)
        val listView: ListView = root.findViewById<View>(R.id.list_match_view) as ListView
        val arrayOfGestures = ArrayList<Gesture?>()
        adaptor = MatchedGestureAdaptor(this.context, arrayOfGestures)
        adaptor!!.fragment = this
        listView.adapter = adaptor

        val okButton = root.findViewById<Button>(R.id.ok_button_home)
        clearButton.setOnClickListener {
            drawingView!!.clearCanvas()
        }

        okButton.setOnClickListener{
            var drawnPath:Path? = drawingView!!.getCurrentPath()
            if (drawnPath == null) {
                println( "Please draw a path before continue")
            } else {
                var gestureMatchResults: ArrayList<Gesture>? = mViewModel!!.comparePath(drawnPath)
                adaptor!!.clear()
                for(i in 0 until gestureMatchResults!!.size) {
                    adaptor!!.add(gestureMatchResults[i])
                }
                adaptor!!.notifyDataSetChanged()
            }
        }

        return root
    }
}