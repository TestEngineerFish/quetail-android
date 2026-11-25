// 官方主题的 id 与英文对照（iOS `ThemeOfficial.swift` + `ThemeMigration.swift` 里的 `ThemeCopy`）。
// 2026-10-21 起**只有「默认」内置**（iOS 02cf7a9，sam：「主题就应该上架在商城，不应该内置」）：海盐 / 石墨是商城里的免费主题，
// tokens 不抄进 App —— 正本只在 umbra-plugins `themes/com.umbra.theme.{sea-salt,graphite}/`，装的时候按包验签、过判据。
//
// 「默认」就是 `designsystem/UmbraTokens.kt` 里的内置值（照 iOS v2 底色，和 PC 默认包不同，设计侧有意的平台差异），
// 选默认 = 不装调色板。官方的默认包（PC 值）只在用例里拿来对拍判据。
//
// Android 是新装，**不移 iOS 的 ThemeMigration**（升级上来的人在后台自动装回海盐 / 石墨那一截）：这里没有「以前内置过」的版本。
// ⚠️ 只许纯 Kotlin：和判据一起进 JUnit。
package com.geek.umbra.feature.theme

object ThemeOfficial {
    const val DEFAULT_ID = "com.quetail.theme.default"
    const val SEA_SALT_ID = "com.quetail.theme.sea-salt"
    const val GRAPHITE_ID = "com.quetail.theme.graphite"

    /** 内置主题（外观页卡片最前面那几张）：只有「默认」。 */
    val BUILTIN_IDS = listOf(DEFAULT_ID)
}

/**
 * 插件 / 主题 id 的别名互认（切标识过渡期，庭宇 11-25 点名）：id 由 `com.umbra.*` 搬到 `com.quetail.*`，
 * 这一对前缀互换着认（`com.umbra.theme.sea-salt` ↔ `com.quetail.theme.sea-salt`），其它前缀（`com.nobody.here`）没有别名。
 * 已装好的旧 id 包、prefs 里存的旧 id、许可证里签的旧 id 都照旧算数；查表 / 比较先精确，没中再按别名认。
 */
object PluginId {
    private const val OLD = "com.umbra."
    private const val NEW = "com.quetail."

    /** [id] 连同它的别名：自己在前（先精确），没有别名就只有自己。 */
    fun aliases(id: String): List<String> = when {
        id.startsWith(NEW) -> listOf(id, OLD + id.removePrefix(NEW))
        id.startsWith(OLD) -> listOf(id, NEW + id.removePrefix(OLD))
        else -> listOf(id)
    }

    /** 两个 id 算不算同一个（前缀别名互认）。 */
    fun same(a: String, b: String): Boolean = a in aliases(b)
}

/**
 * 官方主题的英文名与简介：包和后台只有中文 `name` / `brief`，英文界面时查这张表，没命中退回服务端给的（iOS `ThemeCopy`，批次 275 §一）。
 * 中文一律以包里的为准，这里不抄中文。
 */
object ThemeCopy {
    private val english = mapOf(
        ThemeOfficial.SEA_SALT_ID to ("Sea Salt" to "Cool blue-gray with an indigo accent. The furthest from the default warm gray."),
        ThemeOfficial.GRAPHITE_ID to ("Graphite" to "Neutral gray with no tint and a muted mauve accent. The quietest of the set."),
    )

    /** 界面上显示的名字：英文界面且表里有就用英文，否则用给的那个。旧 `com.umbra.*` id 按别名也认（[PluginId]）。 */
    fun name(id: String, served: String, english: Boolean): String =
        if (english) PluginId.aliases(id).firstNotNullOfOrNull { this.english[it] }?.first ?: served else served

    /** 界面上显示的简介：规则同上。 */
    fun brief(id: String, served: String, english: Boolean): String =
        if (english) PluginId.aliases(id).firstNotNullOfOrNull { this.english[it] }?.second ?: served else served
}
