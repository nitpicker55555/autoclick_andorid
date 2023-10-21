package com.example.assistance_project;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MyAccessibilityService extends AccessibilityService {

    private static Context sContext;
    private static int sEditTextId;
    private List<String> triggerWords;
    private String currentAppName;


    private String getAppNameFromPackageName(String packageName) {
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            return "(unknown)";
        }
        return packageManager.getApplicationLabel(applicationInfo).toString();
    }

    public String getCurrentAppName() {
        return currentAppName;
    }
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
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            String className = event.getClassName().toString(); // 获取触发事件的类名




            System.out.println("Triggered by class: " + className); // 输出触发事件的类名
            if (className.equals("com.example.assistance_project.MainActivity")){
                currentAppName = getAppNameFromPackageName(packageName);
                System.out.println("currentAppName=====  " + currentAppName);
            }
        }
        System.out.println("event.getEventType()  " + event.getEventType() );
//        System.out.println("AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED  " +AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED );
//        System.out.println("AccessibilityEvent.TYPE_VIEW_SCROLLED " +AccessibilityEvent.TYPE_VIEW_SCROLLED);
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                List<String> editTextWords = getTextFromEditText();
                triggerWords = new ArrayList<>();
                triggerWords.addAll(editTextWords);
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
