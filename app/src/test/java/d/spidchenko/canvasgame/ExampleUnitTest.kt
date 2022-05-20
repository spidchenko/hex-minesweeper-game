package d.spidchenko.canvasgame

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
        GameView.canvasCenter = FloatPoint(0.0, 0.0)

        val cell = Cell(q = 0, r = 0)
        cell.state = Cell.State.COVERED
        cell.flag()
        Assert.assertTrue(cell.state == Cell.State.FLAGGED)
        cell.uncover()
        Assert.assertFalse(cell.state == Cell.State.UNCOVERED)
        cell.flag()
        cell.uncover()
        Assert.assertEquals(cell.state, Cell.State.UNCOVERED)
        cell.state = Cell.State.FLAGGED
        cell.uncover()
        Assert.assertTrue(cell.state == Cell.State.FLAGGED)
    }
}