package com.example.a4starter

import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.codebot.listview.GesturesAdapter

class LibraryFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null
    private var gestureNames:ArrayList<String>? = null
    private var gesturePaths:ArrayList<Path>? = null
    private var adaptor:GesturesAdapter? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_library, container, false)
        val arrayOfGestures = ArrayList<Gesture?>()
        adaptor = GesturesAdapter(this.context, arrayOfGestures)
        adaptor!!.fragment = this
        val listView: ListView = root.findViewById<View>(R.id.lvGesturesItems) as ListView
        listView.adapter = adaptor
        gestureNames = mViewModel!!.getGestureNames()
        gesturePaths = mViewModel!!.getPaths()
        for (i in 0 until gestureNames!!.size) {
            adaptor!!.add(Gesture(gestureNames!![i], gesturePaths!![i]))
        }
        return root
    }

    fun deleteGesture(gesture:Gesture) {
        adaptor!!.remove(gesture)
        adaptor!!.notifyDataSetChanged()
        mViewModel!!.deleteStroke(gesture.path, gesture.name)
    }

}