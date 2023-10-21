package com.example.assistance_project;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private static Context sContext;
    private static int sEditTextId;
    private List<String> triggerWords;

    // 静态方法来设置数据
    public static void setData(Context context, int editTextId) {
        sContext = context;
        sEditTextId = editTextId;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        List<String> editTextWords = getTextFromEditText();
        triggerWords = new ArrayList<>();
        triggerWords.addAll(editTextWords);
    }

    public List<String> getTextFromEditText() {
        EditText editText = ((Activity) sContext).findViewById(sEditTextId);
        String[] splitText = editText.getText().toString().split(",");
        return new ArrayList<>(Arrays.asList(splitText));
    }


    public void onAccessibilityEvent(AccessibilityEvent event) {
        System.out.println("event.getEventType()  " + event.getEventType() );
//        System.out.println("AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED  " +AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED );
//        System.out.println("AccessibilityEvent.TYPE_VIEW_SCROLLED " +AccessibilityEvent.TYPE_VIEW_SCROLLED);
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                checkAndClickButtonBasedOnList(rootNode);
            }
            else{
                System.out.println("rootNode: " + rootNode);
            }
        }
    }


//    private final List<String> triggerWords = Arrays.asList("登录", "跳过","跳过广告");  // 可以根据需要添加或删除词汇
    private void checkAndClickButtonBasedOnList(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo!= null) {

//            System.out.println("View ID: " + nodeInfo.getViewIdResourceName());
//            System.out.println("View Name: " + nodeInfo.getClassName());
            CharSequence nodeText = nodeInfo.getText();
            if (nodeText != null) {
                System.out.println("View Text: " + nodeInfo.getText()+"  "+nodeInfo.getClassName());
                for (String word : triggerWords) {
                    if (nodeText.toString().contains(word) && !nodeText.toString().isEmpty() && !word.isEmpty()) {
                        System.out.println("Click!!!   " + word);
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        System.out.println("  "+nodeText+"  -------------------------"+word);
                        return;  // 执行点击后返回，避免重复点击
                    }
                }
            }
            // 遍历子节点
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                checkAndClickButtonBasedOnList(nodeInfo.getChild(i));
            }
        }
    }




    @Override
    public void onInterrupt() {
        // Handle interrupts
    }
}
