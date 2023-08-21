package com.example.a4starter

import android.graphics.Path
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import android.graphics.PathMeasure
import kotlin.collections.Map
import kotlin.math.*


class SharedViewModel : ViewModel() {
    val desc: MutableLiveData<String> = MutableLiveData()
    private val gestures:ArrayList<Path> = ArrayList()
    private val names: ArrayList<String> = ArrayList()
    val strokeGesturesLive: MutableLiveData<ArrayList<Path>> = MutableLiveData<ArrayList<Path>>()
    private val gestureNamesLive: MutableLiveData<ArrayList<String>> = MutableLiveData<ArrayList<String>>()

    init {
        desc.value = "Shared model"
//        strokeGestures.value?.add(Path()) // empty path for illustration purposes
    }

    fun addStroke(path: Path, name: String) {
        var repeatIndex:Int = -1
        for(i in 0 until names.size) {
            if (names[i]== name) {
                repeatIndex = i
            }
        }
        if (repeatIndex == -1) {
            gestures.add(path)
            strokeGesturesLive.value = gestures
            names.add(name)
            gestureNamesLive.value = names
        } else {
            gestures[repeatIndex] = path
            strokeGesturesLive.value = gestures
        }
//        strokeGesturesLive.value?.add(path)
//        gestureNamesLive.value?.add(name)
//        strokeGesturesLive.value = strokeGesturesLive.value
//        gestureNamesLive.value = gestureNamesLive.value
        println("gestures size "+ gestures.size)
        println("names size "+ names.size)
    }

    fun deleteStroke(path:Path, name: String) {
        gestures.remove(path)
        names.remove(name)
        strokeGesturesLive.value = gestures
        gestureNamesLive.value = names
    }
    fun getGestureNames(): ArrayList<String> {
        return names
    }

    fun getPaths(): ArrayList<Path> {
        return gestures
    }

    private fun findCentroid(points: ArrayList<FloatPoint>):FloatPoint {
        if(points.size != 128) {
            println("error!!!!!points size not equal to 128")
        }
        var sumX:Float = 0.0F
        var sumY:Float = 0.0F
        for (point in points) {
            sumX+=point.x
            sumY+=point.y
        }
        return FloatPoint(sumX/128, sumY/128)
    }

    // function to sort hashmap by values
    fun sortByValue(hm: HashMap<Int, Float>): Map<Int, Float> {
        return hm.toList().sortedBy { (_, value) -> value }.toMap()
    }


    private fun getPointArray(path0:Path): ArrayList<FloatPoint> {
        val pointArray:ArrayList<FloatPoint> = ArrayList()
        val pm = PathMeasure(path0, false)
        val length = pm.length
        var distance = 0f
        val speed = length / 128
        var counter = 0
        val aCoordinates = FloatArray(2)

        while (distance < length && counter < 128) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null)
            pointArray.add(FloatPoint(
                aCoordinates[0],
                aCoordinates[1]
            ))
            counter++
            distance += speed
        }
        if(pointArray.size == 128) {
            println("128 verified")
        }
        println()
        return pointArray
    }

    private fun rotatePointArray(pointArray1:ArrayList<FloatPoint>) {
        var startingPoint1: FloatPoint = pointArray1[0]
        var centroidPoint1:FloatPoint = findCentroid(pointArray1)
        //translate to the origin
//        for (point in pointArray1) {
//            point.x = point.x - centroidPoint1.x
//            point.y = point.y - centroidPoint1.y
//        }
//        for (point in pointArray2) {
//            point.x = point.x - centroidPoint2.x
//            point.y = point.y - centroidPoint2.y
//        }

        var deltaX_1 = startingPoint1.x - centroidPoint1.x
        var deltaY_1 = startingPoint1.y - centroidPoint1.y
        var angle = atan2(deltaY_1, deltaX_1)
        for (point in pointArray1) {
            var x1 = point.x - centroidPoint1.x
            var y1 = point.y - centroidPoint1.y
            var x2 = x1 * cos(angle.toDouble()) - y1 * sin(angle.toDouble())
            var y2 = x1 * sin(angle.toDouble()) + y1 * cos(angle.toDouble())
            point.x = (x2 + centroidPoint1.x).toFloat()
            point.y = ((y2 + centroidPoint1.y).toFloat())
        }
    }

    private fun scalePointArray(pointArray1:ArrayList<FloatPoint>) {
        //translate to origin, for each point
//        (maxX - minX) / 100
        // (maxY - minY) / 100
        var centroidPoint1:FloatPoint = findCentroid(pointArray1)
        for (point in pointArray1) {
            point.x = point.x - centroidPoint1.x
            point.y = point.y - centroidPoint1.y
        }
        var maxX = -1.0F
        var minX = 100000.0F
        var maxY = -1.0F
        var minY = 100000.0F
        for (point in pointArray1) {
            if (point.x < minX) {
                minX = point.x
            }
            if (point.x > maxX) {
                maxX = point.x
            }
            if (point.y < minY) {
                minY = point.y
            }
            if (point.y > maxY) {
                maxY = point.y
            }
        }
        var deltaX = maxX - minX
        var deltaY = maxY - minY
        for (point in pointArray1) {
            point.x = point.x/deltaX * 100
            point.y = point.y/deltaY * 100
        }


    }

    private fun comparePointDisOfTwoPath(path1: Path, path2:Path): Float {

        var pointArray1:ArrayList<FloatPoint> = getPointArray(path1)
        var pointArray2:ArrayList<FloatPoint> = getPointArray(path2)
        rotatePointArray(pointArray1)
        rotatePointArray(pointArray2)
        println("point1 position is " + pointArray1[0].x+ " "+pointArray1[0].y)
        println("point2 position is " + pointArray2[0].x+ " "+pointArray2[0].y)
        scalePointArray(pointArray1)
        scalePointArray(pointArray2)
        var sumOfSquares: Float = 0.0F
        for (i in 0 until 128) {
            sumOfSquares += sqrt(
                ((pointArray1[i].x - pointArray2[i].x).pow(2)) +
                        ((pointArray1[i].y - pointArray2[i].y).pow(2))
            )
        }
        return (sumOfSquares/128)
    }


    fun comparePath(path:Path): ArrayList<Gesture>? {
        var allComparedScores:HashMap<Int, Float> = HashMap()
        var gesturesInOrderMatch:ArrayList<Gesture>? = ArrayList()
        for (i in 0 until names.size) {
            allComparedScores[i] = comparePointDisOfTwoPath(path, gestures[i])
        }

        var matchedResultsInOrder:Map<Int, Float> = sortByValue(allComparedScores)
        for (entry in matchedResultsInOrder) {
            gesturesInOrderMatch!!.add(Gesture(names[entry.key], gestures[entry.key]))
        }
        return gesturesInOrderMatch
    }
}