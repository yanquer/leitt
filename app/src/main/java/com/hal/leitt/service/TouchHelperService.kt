package com.hal.leitt.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.hal.leitt.ktx.Settings
import java.lang.ref.WeakReference


/**
 * ...
 * @author LeiHao
 * @date 2024/1/12
 * @description 自动触控服务基础 ACC
 */

class TouchHelperService : AccessibilityService() {

    companion object {
        //刷新关键字
        const val ACTION_REFRESH_KEYWORDS = 1

        //刷新检测包列表
        const val ACTION_REFRESH_PACKAGE = 2

        //弹出自定义按钮采集弹窗
        const val ACTION_ACTIVITY_CUSTOMIZATION = 3

        //刷新采集到的控件或者位置信息
        const val ACTION_REFRESH_CUSTOMIZED_ACTIVITY = 4

        private var mService: WeakReference<TouchHelperService>? = null

        fun dispatchAction(action: Int): Boolean {
            val service = mService?.get()
            return if (service?.serviceImpl != null) {
                service.serviceImpl?.receiverHandler?.sendEmptyMessage(action)
                true
            } else {
                false
            }
        }

        fun isServiceRunning(): Boolean {
            val service = mService?.get()
            return service?.serviceImpl != null
        }
    }

    private var serviceImpl: TouchHelperServiceImpl? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        mService = WeakReference(this)
        if (serviceImpl == null) {
            serviceImpl = TouchHelperServiceImpl(this)
        }
        serviceImpl?.onServiceConnected()
        Settings.setFunctionOn(true)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        //判断跳广告功能是否开启，是的话再处理其他的事
        if (Settings.isFunctionOn()) {
            serviceImpl?.onAccessibilityEvent(event)
        }
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent): Boolean {
        serviceImpl?.onUnbind()
        serviceImpl = null
        mService = null
        Settings.setFunctionOn(false)
        return super.onUnbind(intent)
    }

}