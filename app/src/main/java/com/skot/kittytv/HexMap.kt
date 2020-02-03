package com.skot.kittytv

import android.graphics.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicBoolean


data class Hex(val row: Int, val col: Int) {

    var isSelected: Boolean = false
    var poly: Path? = null
    var occupyingUnit: Entity? = null

    fun isOccupied() = occupyingUnit != null
}


class HexMap(
    private val rows: Int,
    private val columns: Int,
    private val hexSize: Int = 50
) {

    private val width = 850
    private val height = 650

    private val hexArray: Array<Array<Hex>>

    private var bufferedImage: Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) // DO NOT ACCESS DIRECTLY
    private var imageDirty = AtomicBoolean(true) // DO NOT ACCESS DIRECTLY

    val renderFlow: Flow<Bitmap> = flow {
        while (true) {
            emit(renderHexMap(width, height))
        }
    }

    init {
        hexArray = Array(rows) { rowNum ->
            Array(columns) { colNum ->
                Hex(rowNum, colNum)
            }
        }

        val troop = Entity.TROOPER
        val mech = Entity.MECH
        val tank = Entity.TANK

//        hexArray[1][0].occupyingUnit = troop
//        hexArray[1][1].occupyingUnit = mech
//        hexArray[1][2].occupyingUnit = tank

        println(hexArray[1][1].isOccupied())
    }

    fun getBufferedImage(): Bitmap {
        synchronized(this) {
            return bufferedImage
        }
    }

    fun setBufferedImage(img: Bitmap) {
        synchronized(this) {
            bufferedImage = img
            setIsImageDirty(false)
        }
    }

    fun setIsImageDirty(toThis: Boolean) {
        imageDirty.set(toThis)
    }

    fun getIsImageDirty(): Boolean {
        return imageDirty.get()
    }

//    fun selectHex(point: Point) {
//        hexArray.forEach { subArray ->
//            subArray.forEach {
//                if (it.poly != null && it.poly!!.contains(point)) {
//                    it.isSelected = !it.isSelected
//                    setIsImageDirty(true)
//                }
//            }
//        }
//    }

    fun selectHex(touchX: Int, touchY: Int): Boolean {


        val offsetX = (0.5 * hexSize).toInt()
        val offsetY = (0.5 * hexSize).toInt()


        // find row
//        for (i in hexArray.indices) {
//            for (j in hexArray[i].indices) {
//                println(">>> $i x $j ${hexArray[i][j]}")
//            }
//        }

        val hexX = (touchX / (2 * hexSize))
        val hexY = (touchY / (2 * hexSize))

        println(">>> TOUCH $touchX x $touchY -> $hexX x $hexY")


        return try {
            hexArray[hexY][hexX].isSelected = !hexArray[hexY][hexX].isSelected
            setIsImageDirty(true)
            true

        } catch (e: Exception) {
            println(">>> NOPE!")
            false
        }


    }

    fun getHexAt(row: Int, column: Int): Hex? {
        return hexArray[row][column]
    }

    fun renderHexMap(width: Int, height: Int): Bitmap {

        if (!getIsImageDirty()) {
            return getBufferedImage()
        }

        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        val paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias =
            true //  setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        paint.style = Paint.Style.FILL

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)


        var beginDrawingFromX = (0.5 * hexSize).toInt()
        var beginDrawingFromY = (0.5 * hexSize).toInt()

        var x = beginDrawingFromX
        var y = beginDrawingFromY

        for (i in 0 until rows) {

            for (j in 0 until columns) {
                if (j % 2 != 0)
                    y = beginDrawingFromY + (.8660 * hexSize).toInt()
                else
                    y = beginDrawingFromY

                val poly = Path()
                poly.reset()
                poly.moveTo((x + hexSize / 2).toFloat(), y.toFloat())
                poly.lineTo((x + hexSize / 2 + hexSize).toFloat(), y.toFloat())
                poly.lineTo((x + 2 * hexSize).toFloat(), (.8660 * hexSize + y).toFloat())
                poly.lineTo(
                    (x + hexSize / 2 + hexSize).toFloat(),
                    (.8660 * 2.0 * hexSize.toDouble() + y).toFloat()
                )
                poly.lineTo(
                    (x + hexSize / 2).toFloat(),
                    (.8660 * 2.0 * hexSize.toDouble() + y).toFloat()
                )
                poly.lineTo(x.toFloat(), (y + (.8660 * hexSize).toFloat()))
                poly.lineTo((x + hexSize / 2).toFloat(), y.toFloat())

                val hex = getHexAt(i, j)!!
                if (hex.poly == null) {
                    hex.poly = poly
                }

                if (hex.isSelected) {
                    paint.color = Color.RED
                    paint.style = Paint.Style.FILL
                    canvas.drawPath(poly, paint)
                }

                paint.style = Paint.Style.STROKE
                paint.color = Color.BLACK
                canvas.drawPath(poly, paint)

                if (hex.isOccupied()) {
//                    val unitImg = hex.occupyingUnit!!.image
//                    canvas.drawImage(unitImg, x, y, unitImg.width, unitImg.height, null)
                    paint.color = Color.RED
                    paint.style = Paint.Style.FILL
                    canvas.drawPath(poly, paint)
                }

                //Move the pencil over
                x += (hexSize / 2) + hexSize

            }

            beginDrawingFromY += (2 * (.8660 * hexSize)).toInt()

            x = beginDrawingFromX
            y += (2.0 * .8660 * hexSize.toDouble()).toInt()

            y = if (i % 2 != 0)
                beginDrawingFromY + (.8660 * hexSize).toInt()
            else
                beginDrawingFromY

        }

        setBufferedImage(image)
        return image
    }

}