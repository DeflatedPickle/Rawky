import com.deflatedpickle.haruhi.api.config.Config
import com.deflatedpickle.rawky.settings.api.range.FloatRange
import kotlinx.serialization.Serializable

@Serializable
data class FLChanSettings(
    override val version: Int = 1,
    var action: FLChanAction = FLChanAction.WAITING,
    @FloatRange(0f, 1f) var reflectOpacity: Float = 0.25f
) : Config
