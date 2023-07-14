package com.example.messagemaster

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

class WhatsappAccessibilityService: AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(rootInActiveWindow == null)
            return
        val rootNodeInfo: AccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)
        val messageList:List<AccessibilityNodeInfoCompat> = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
        if(messageList == null ||messageList.isEmpty())
            return

        val messageField:AccessibilityNodeInfoCompat = messageList.get(0)
        if(messageField==null||messageField.text.length==0||!messageField.text.toString().endsWith("   "))
            return
        val sendMessageList:List<AccessibilityNodeInfoCompat> = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
        if(sendMessageList == null ||sendMessageList.isEmpty())
            return
        val sendMessage:AccessibilityNodeInfoCompat = sendMessageList.get(0)
        if(!sendMessage.isVisibleToUser)
            return
        sendMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        try {
            Thread.sleep(2000)
            performGlobalAction(GLOBAL_ACTION_BACK)
            Thread.sleep(2000)
            performGlobalAction(GLOBAL_ACTION_BACK)
            Thread.sleep(2000)
        }
        catch(ignored:InterruptedException){

        }

    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}