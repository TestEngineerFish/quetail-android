package com.geek.umbra.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * 通知权限只在用到时申请（sam 2026-11-16）：冷启动不许弹。这里读源码钉住时机 ——
 * onCreate 里只登记 launcher，不调 requestIfNeeded；使用入口那几处必须还在（提醒保存后、执勤开启后）。
 * 为什么读源码：这是 Activity 生命周期里的调用，纯 JVM 用例跑不了 Activity；而这条规矩最贵的坏法
 * 就是有人把「顺手问一下」又加回冷启动。
 */
class NotificationAskTimingTest {
    private val root = File("src/main/java/com/geek/umbra")

    private fun read(rel: String): String = File(root, rel).readText()

    @Test fun coldStartDoesNotAsk() {
        val activity = read("app/MainActivity.kt")
        val onCreate = activity.substringAfter("override fun onCreate").substringBefore("override fun onResume")
        assertTrue("launcher 仍要在 onCreate 里登记", "NotificationPermission.launcher = registerForActivityResult" in onCreate)
        assertFalse("onCreate 不许申请通知权限", "NotificationPermission.requestIfNeeded()" in onCreate)
        assertFalse("onCreate 不许直接启动权限请求", "launcher.launch(" in onCreate)
    }

    @Test fun usageSitesStillAsk() {
        assertTrue("提醒保存后要问", "askNotify()" in read("feature/reminder/ReminderScreens.kt"))
        assertTrue("执勤开启成功后要问", "DutyPush.requestIfNeeded()" in read("feature/duty/DutyViews.kt"))
        assertTrue("执勤待弹标记的接住处在", "DutyPush.pending.collect" in read("feature/duty/DutyUi.kt"))
        assertTrue("「去开启」按钮仍走 enableTapped", "NotificationPermission.enableTapped" in read("feature/reminder/ReminderScreens.kt"))
    }
}
