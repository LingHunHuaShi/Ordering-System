package com.zzh.orderingsystem;

// The code annotated with "MAKR" indicates code that has been modified or optimized by Hexiang Mao after the audit.

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class LoginForgetActivity extends AppCompatActivity implements View.OnClickListener {

    OrderSys orderSys;
    private String verifyCode;
    private EditText et_phone;
    private EditText et_password_first;
    private EditText et_password_second;
    private EditText et_verifycode;
    private int phone;

    // MARK
    DBFunction db_ops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_forget);

        et_phone = findViewById(R.id.et_phone);
        et_password_first = findViewById(R.id.et_password_first);
        et_password_second = findViewById(R.id.et_password_second);
        et_verifycode = findViewById(R.id.et_verifycode);

        findViewById(R.id.btn_verifycode).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);


        Intent intent = getIntent();
        phone = intent.getIntExtra("phone", 0);
        et_phone.setText(String.valueOf(phone));

        //获取OrderSys实例
        orderSys = new OrderSys(LoginForgetActivity.this);

        // MARK
        db_ops = (DBFunction) orderSys;

    }

    @Override
    public void onClick(View v) {
        users user = db_ops.find_user(phone);
        String password = et_password_first.getText().toString();
        //MARK
        if (user == null) {
            Toast.makeText(this, "用户不存在", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginForgetActivity.this, LoginMainActivity.class);
            startActivity(intent);
            return;
        }
        //END OF MARK

        // 点击“获取验证码”按钮
        if (v.getId() == R.id.btn_verifycode) {
            // 生成六位随机数字的验证码
            verifyCode = String.format("%06d", new Random().nextInt(999999));
            // 弹出验证码对话框
            AlertDialog.Builder buider = new AlertDialog.Builder(this);
            buider.setTitle("请记住验证码");
            buider.setMessage("手机号为" + phone + "的用户，您好！,本次验证码是" + verifyCode + ",请输入验证码");
            buider.setPositiveButton("好的", null);
            AlertDialog dialog = buider.create();
            dialog.show();
        }
        // 点击“确定”按钮
        else if (v.getId() == R.id.btn_confirm) {
            String password_first = et_password_first.getText().toString();
            String password_second = et_password_second.getText().toString();
            if (!password_first.equals(password_second)) {
                Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.passwd = password_first;
            }

            if (!verifyCode.equals(et_verifycode.getText().toString())) {
                Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                return;
            }

            //MARK
            if (db_ops.update_users(user.uuid, user)) {
                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
