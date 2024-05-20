import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class Obstacle(val position: Offset, val size: Size)

class PocketSoccerViewModel : ViewModel() {
    private val _center = MutableStateFlow(Offset.Zero)
    val center: StateFlow<Offset> = _center

    private val _goalsLeft = MutableStateFlow(0)
    val goalsLeft: StateFlow<Int> = _goalsLeft

    private val _goalsRight = MutableStateFlow(0)
    val goalsRight: StateFlow<Int> = _goalsRight

    private val fieldWidth = 1080f
    private val fieldHeight = 1920f
    private val goalWidth = 200f
    private val goalHeight = 100f
    private val goalX = (fieldWidth - goalWidth) / 2

    val obstacles = listOf(
        Obstacle(Offset(goalX, 0f), Size(goalWidth, goalHeight)),
        Obstacle(Offset(goalX, fieldHeight - goalHeight), Size(goalWidth, goalHeight))
    )

    init {
        _center.value = Offset(fieldWidth / 2, fieldHeight / 2)
    }

    internal fun isCollided(center: Offset, obstacle: Obstacle, radius: Float): Boolean {
        return center.x + radius > obstacle.position.x && center.x - radius < obstacle.position.x + obstacle.size.width &&
                center.y + radius > obstacle.position.y && center.y - radius < obstacle.position.y + obstacle.size.height
    }

    fun checkCollisions(radius: Float) {
        val currentCenter = _center.value
        if (isCollided(currentCenter, obstacles[0], radius)) {
            _center.value = Offset(fieldWidth / 2, fieldHeight / 2)
            _goalsLeft.value++
        }

        if (isCollided(currentCenter, obstacles[1], radius)) {
            _center.value = Offset(fieldWidth / 2, fieldHeight / 2)
            _goalsRight.value++
        }
    }

    fun updateCenter(x: Float, y: Float, radius: Float, orientation: Int) {
        val newCenter = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Offset(
                x = (_center.value.x - x).coerceIn(radius, fieldWidth - radius),
                y = (_center.value.y + y).coerceIn(radius, fieldHeight - radius),
            )
        } else {
            Offset(
                x = (_center.value.x + y).coerceIn(radius, fieldWidth - radius),
                y = (_center.value.y + x).coerceIn(radius, fieldHeight - radius),
            )
        }
        _center.value = newCenter
        checkCollisions(radius)
    }
}