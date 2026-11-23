package com.geek.umbra.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * 通知权限只在用到时申请（sam 2026-11-16）：冷启动不许弹。这里读源码钉住时机 ——
 * onCreate 里只登记 launcher，不调 requestIfNeeded；使用入口那几处必须还在（提醒保存后、执勤开启 /
 * 新建成功后）。为什么读源码：这是 Activity 生命周期里的调用，纯 JVM 用例跑不了 Activity；而这条规矩
 * 最贵的坏法就是有人把「顺手问一下」又加回冷启动。
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
        assertTrue("执勤开启成功后要走 NotificationPermission 问", "NotificationPermission.requestIfNeeded()" in read("feature/duty/DutyViews.kt"))
        assertTrue("新建执勤成功后要走 NotificationPermission 问", "NotificationPermission.requestIfNeeded()" in read("feature/duty/DutyEditView.kt"))
        assertTrue("「去开启」按钮仍走 enableTapped", "NotificationPermission.enableTapped" in read("feature/reminder/ReminderScreens.kt"))
    }

    /**
     * 执勤收敛到 NotificationPermission（issue #4）：申请走同一个 requestIfNeeded —— 真正发起时才在那里写
     * notifyAsked，「我 › 通知」的 status() 才看得到「问过」；执勤不再自备 launcher / pending / 自己的
     * openSettings，「被拒」展示态也读同一份 status()。
     */
    @Test fun dutyConvergedOnNotificationPermission() {
        val ui = read("feature/duty/DutyUi.kt")
        assertFalse("执勤不再自备 Compose 权限 launcher", "rememberLauncherForActivityResult" in ui)
        assertFalse("执勤不再有 pending 待弹标记", "pending" in ui)
        assertFalse("执勤不再自己跳通知设置", "ACTION_APP_NOTIFICATION_SETTINGS" in ui)
        assertTrue("被拒展示态读 NotificationPermission.status()", "NotificationPermission.status()" in ui)
        assertTrue("执勤被拒行改走 enableTapped", "NotificationPermission.enableTapped" in read("feature/duty/DutyViews.kt"))
    }
}
