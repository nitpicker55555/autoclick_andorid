package com.example.assistance_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private EditText editText;
    private Button buttonSubmit;
    private String currentAppName;
    private ArrayList<String> database; // 模拟数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView guideTextView = findViewById(R.id.guideTextView);
        guideTextView.setText("请在系统无障碍设置中激活此服务以开始使用。");

        Button activateServiceButton = findViewById(R.id.activateServiceButton);
        activateServiceButton.setText("激活无障碍服务");
        activateServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
        editText = findViewById(R.id.edit_text);
        buttonSubmit = findViewById(R.id.button_submit);
        database = new ArrayList<>(); // 初始化模拟数据库

        buttonSubmit.setOnClickListener(v -> {
            String[] splitText = editText.getText().toString().split(",");
            for (String text : splitText) {
                if (!database.contains(text.trim())) {
                    database.add(text.trim());
                }
            }
        });

        editText.setOnClickListener(v -> {
            showDropdown();
        });
        MyAccessibilityService.setData(this, R.id.edit_text);

        // 启动服务（如果需要）
        Intent intent = new Intent(this, MyAccessibilityService.class);
        startService(intent);

        // 如果你需要立即调用 getTextFromEditText()，请确保在setData之后进行
        MyAccessibilityService anotherClass = new MyAccessibilityService();
        List<String> textList = anotherClass.getTextFromEditText();
    }

    @SuppressLint("SetTextI18n")
    private void showDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, database);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(adapter, (dialog, which) -> {
            editText.setText(editText.getText().toString() + "," + database.get(which));
        });
        builder.show();
    }
}
