import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(
    val id: String?,         // 来源 id，可能为空，所以用可空类型

    val name: String         // 来源名称
): Parcelable