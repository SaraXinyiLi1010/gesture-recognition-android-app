package net.codebot.listview

import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a4starter.R
import android.graphics.drawable.Drawable
import android.widget.*
import androidx.fragment.app.Fragment

import com.example.a4starter.CanvasView
import com.example.a4starter.Gesture
import com.example.a4starter.LibraryFragment


class GesturesAdapter(context: Context?, gestures: ArrayList<Gesture?>?) :
    ArrayAdapter<Gesture?>(context!!, 0, gestures!!) {
    var fragment:LibraryFragment? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = 10f
        // Get the data item for this position
        var view: View? = convertView
        val gesture = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_gesture, parent, false)
        }

        // Lookup view for data population
        val gestureName = view?.findViewById(R.id.gestureName) as TextView
        gestureName.text = gesture!!.name
        val gestureImage = view.findViewById<ImageView>(R.id.gestureImage)

        // Populate the data into the template view using the data object
        val returnedBitmap = Bitmap.createBitmap(1080, 1200, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        val tempView  = View(context)
        val bgDrawable = tempView.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas) else
            canvas.drawColor(Color.parseColor("#808BC34A"))
        canvas.drawPath(gesture.path, mPaint)
        view.draw(canvas)
        var pm:PathMeasure = PathMeasure(gesture.path, false)
//        println("Path length is !!!!")
//        println(pm.length)
        gestureImage!!.setImageBitmap(returnedBitmap)

//        val inflater = LayoutInflater.from(context)
//        val row:View = inflater.inflate(R.layout.item_gesture, parent, false)
        var deleteGestureButton = view.findViewById<ImageButton>(R.id.deleteButton)
        deleteGestureButton.setOnClickListener{
            println("I want to delete "+position)
           fragment!!.deleteGesture(gesture)
        }

        return view
    }

}