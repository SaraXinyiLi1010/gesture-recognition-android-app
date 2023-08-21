package com.example.a4starter

import android.R.attr
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import android.R.attr.button
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog


class AdditionFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null
    var drawingView: CanvasView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_addition, container, false)
        drawingView = CanvasView(context)
        drawingView!!.addSharedViewModel(mViewModel!!)
        val layoutView = root.findViewById<LinearLayout>(R.id.additionCanvas)
        layoutView.addView(drawingView)
        layoutView.isEnabled = true

//
//        mViewModel!!.desc.observe(viewLifecycleOwner, { s:String -> textView.text = "$s - Addition" })
//        mViewModel!!.strokeGestures.observe(viewLifecycleOwner, { s:ArrayList<Path> -> textView.text = "stroke count: ${s.size}"})

        val clearButton = root.findViewById<Button>(R.id.clearButton)
        val addButton = root.findViewById<Button>(R.id.addButton)
        clearButton.setOnClickListener {
            drawingView!!.clearCanvas()
        }

        addButton.setOnClickListener {
            val builder: AlertDialog.Builder? = context?.let { it1 -> AlertDialog.Builder(it1) }
            builder!!.setTitle("Gesture Name")

            val input = EditText(context)
            input.setHint("Enter Gesture Name")
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setNegativeButton("OK", DialogInterface.OnClickListener { dialog, which ->
                var text = input.text.toString()
                var path: Path? = drawingView!!.getCurrentPath()
                if (path != null && text != "") {
                    println("get Path!!!")
                    mViewModel!!.addStroke(path, text)
                    drawingView!!.clearCanvas()
                } else {
                    val warningBuilder: AlertDialog.Builder? = context?.let { it1 -> AlertDialog.Builder(it1) }
                    warningBuilder!!.setTitle("Empty Gesture or Gesture Name")
                    warningBuilder.setMessage("Warning: Unable to add, empty Gesture or Gesture Name")
                    warningBuilder.setPositiveButton("OK", null)
                    warningBuilder.show()
                }
            })
            builder.setPositiveButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }
        return root
    }
}

