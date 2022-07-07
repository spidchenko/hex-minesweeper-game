package d.spidchenko.canvasgame

import android.graphics.PointF
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun uncover_covered_cell_isCorrect() {
        GameEngine.canvasCenter = PointF(0.0F, 0.0F)

        val cell = Cell(q = 0, r = 0)
        cell.isCovered
        cell.flag()
        Assert.assertTrue(cell.isFlagged)
        cell.uncover()
        Assert.assertFalse(cell.isUncovered)
        cell.flag()
        cell.uncover()
        Assert.assertTrue(cell.isUncovered)
        cell.flag()
        cell.uncover()
        Assert.assertTrue(cell.isFlagged)
    }
}