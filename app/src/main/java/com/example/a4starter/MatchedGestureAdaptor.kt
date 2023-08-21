package net.codebot.listview

import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.drawable.Drawable
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.a4starter.*


class MatchedGestureAdaptor(context: Context?, gestures: ArrayList<Gesture?>?) :
    ArrayAdapter<Gesture?>(context!!, 0, gestures!!) {
    var fragment: HomeFragment? = null

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
            view = LayoutInflater.from(context).inflate(R.layout.matched_item_in_home, parent, false)
        }

        // Lookup view for data population
        val gestureName = view?.findViewById(R.id.matchedGestureName) as TextView
        gestureName.text = gesture!!.name
        val gestureImage = view.findViewById<ImageView>(R.id.matchedGestureImage)

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

        return view
    }

}