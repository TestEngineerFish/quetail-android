package com.geek.umbra.feature.theme

/**
 * 主题版本比较（2026-11-14，同 PC `src/theme/version.ts`）：只认三段数字 `1.2.3`，
 * 段数不对 / 有非数字 → null（**不比较**，当作没有新版本，不猜）。允许前后空格和前导零。
 */
object ThemeVersion {
    /** a > b → 1；a < b → -1；相等 0；格式不合法 → null。 */
    fun compare(a: String, b: String): Int? {
        val pa = parse(a) ?: return null
        val pb = parse(b) ?: return null
        for (i in 0..2) {
            if (pa[i] != pb[i]) return if (pa[i] > pb[i]) 1 else -1
        }
        return 0
    }

    /** 目录版本比本机已装的大才返回它（可更新）；否则 null。 */
    fun newer(catalog: String, local: String): String? = if (compare(catalog, local) == 1) catalog else null

    private val PATTERN = Regex("""^(\d+)\.(\d+)\.(\d+)$""")

    private fun parse(v: String): LongArray? {
        val m = PATTERN.matchEntire(v.trim()) ?: return null
        val n = LongArray(3)
        for (i in 0..2) {
            // 只收 64 位有符号整数里放得下的（同 PC 的 Number.isSafeInteger：超过 JS 安全整数的也不比）
            val x = m.groupValues[i + 1].toLongOrNull() ?: return null
            if (x > 9_007_199_254_740_991L) return null
            n[i] = x
        }
        return n
    }
}
