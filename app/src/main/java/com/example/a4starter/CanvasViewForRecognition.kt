package com.example.a4starter

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider

@SuppressLint("AppCompatCustomView")
open class CanvasViewForRecognition(context: Context?): ImageView(context) {
    val LOGNAME = "panzoom"

    // drawing
    var path: Path? = null
    var paths: ArrayList<Path?> = ArrayList()
    var paintbrush = Paint(Color.BLUE)
    var background: Bitmap? = null
    var sharedViewModel: SharedViewModel? = null
    // we save a lot of points because they need to be processed
    // during touch events e.g. ACTION_MOVE
    var x1 = 0f
    var y1 = 0f
    var p1_id = 0
    var p1_index = 0

    // store cumulative transformations
    // the inverse matrix is used to align points with the transformations - see below
    var currentMatrix = Matrix()
    var inverse = Matrix()

    fun addSharedViewModel(model: SharedViewModel) {
        sharedViewModel = model
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        var inverted: FloatArray
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
//                Log.d(LOGNAME, "Action down")
                path = Path()
                paths.clear()
                paths.add(path)
                path!!.moveTo(x1, y1)
            }
            MotionEvent.ACTION_MOVE -> {
//                Log.d(LOGNAME, "Action move")
                path!!.lineTo(x1, y1)
            }
//            MotionEvent.ACTION_UP -> Log.d(LOGNAME, "Action up")
        }
        when (event.pointerCount) {
            1 -> {
                p1_id = event.getPointerId(0)
                p1_index = event.findPointerIndex(p1_id)

                // invert using the current matrix to account for pan/scale
                // inverts in-place and returns boolean
                inverse = Matrix()
                currentMatrix.invert(inverse)

                // mapPoints returns values in-place
                inverted = floatArrayOf(event.getX(p1_index), event.getY(p1_index))
                inverse.mapPoints(inverted)
                x1 = inverted[0]
                y1 = inverted[1]
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
//                        Log.d(LOGNAME, "Action down")
                        path = Path()
                        paths.clear()
                        paths.add(path)
                        path!!.moveTo(x1, y1)
                    }
                    MotionEvent.ACTION_MOVE -> {
//                        Log.d(LOGNAME, "Action move")
                        path!!.lineTo(x1, y1)
                    }
//                    MotionEvent.ACTION_UP -> Log.d(LOGNAME, "Action up")
                }
            }

        }
        return true

    }

    fun setImage(bitmap: Bitmap?) {
        background = bitmap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // apply transformations from the event handler above
        canvas.concat(currentMatrix)

        // draw background
        if (background != null) {
            setImageBitmap(background)
        }

        // draw lines over it
        for (path in paths) {
//            println("path length "+paths.size)
            canvas.drawPath(path!!, paintbrush)
        }
//        canvas.drawPath(path!!, paintbrush)
    }

    fun clearCanvas() {
        paths.clear()
        println("Clear called")
    }

    fun getCurrentPath(): Path? {
        if (paths.size >= 1) {
            return paths[paths.size - 1]
        }
        return null
    }


    init {
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeWidth = 5f
        val image = BitmapFactory.decodeResource(resources, R.drawable.drawable_recognition_paper)
        this.setImage(image)
    }
}