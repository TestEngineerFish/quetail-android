package com.geek.umbra.feature.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// 同 PC tests/themeVersion.test.ts（2026-11-14 主题更新）。
class ThemeVersionTest {
    @Test fun numbers() {
        assertEquals(1, ThemeVersion.compare("1.10.0", "1.9.0"))
        assertEquals(-1, ThemeVersion.compare("1.9.0", "1.10.0"))
        assertEquals(1, ThemeVersion.compare("2.0.0", "1.99.99"))
        assertEquals(0, ThemeVersion.compare("1.1.0", "1.1.0"))
        assertEquals(1, ThemeVersion.compare("1.1.1", "1.1.0"))
        assertEquals(-1, ThemeVersion.compare("1.1.0", "1.1.1"))
    }

    @Test fun invalid() {
        for (bad in listOf("", "1.0", "1.0.0.0", "v1.0.0", "1.0.beta", "1.-1.0", "1.0.0-draft")) {
            assertNull(bad, ThemeVersion.compare(bad, "1.0.0"))
            assertNull(bad, ThemeVersion.compare("1.0.0", bad))
        }
    }

    @Test fun spacesAndLeadingZeros() {
        assertEquals(0, ThemeVersion.compare(" 01.02.03 ", "1.2.3"))
        assertEquals(0, ThemeVersion.compare("1.1.0", "01.01.00"))
        assertNull("超出安全整数", ThemeVersion.compare("1.9007199254740992.0", "1.0.0"))
        assertEquals("1.2.0", ThemeVersion.newer("1.2.0", "1.1.0"))
        assertNull("同版本不提示", ThemeVersion.newer("1.1.0", "1.1.0"))
        assertNull("格式不合法不提示", ThemeVersion.newer("v1.2.0", "1.1.0"))
    }
}
