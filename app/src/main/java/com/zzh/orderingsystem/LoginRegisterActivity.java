package com.zzh.orderingsystem;

// The code annotated with "MAKR" indicates code that has been modified or optimized by Hexiang Mao after the audit.

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class LoginRegisterActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    OrderSys orderSys;
    private String verifyCode;
    private EditText et_username;
    private EditText et_phone;
    private EditText et_password;
    private RadioButton rb_business;
    private RadioButton rb_customer;
    private EditText et_verifycode;
    private EditText et_authcode;
    private TextView tv_authcode;
    private String authcode ="123456";//商家授权码

    public DBFunction db_ops;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        et_username = findViewById(R.id.et_username);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        et_verifycode = findViewById(R.id.et_verifycode);
        et_authcode = findViewById(R.id.et_authcode);
        rb_business = findViewById(R.id.rb_business);
        rb_customer = findViewById(R.id.rb_customer);
        tv_authcode = findViewById(R.id.tv_authcode);

        Button btn_verifycode = findViewById(R.id.btn_verifycode);
        Button btn_confirm = findViewById(R.id.btn_confirm);
        btn_verifycode.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        RadioGroup rg_user_type = findViewById(R.id.rg_user_type);
        rg_user_type.setOnCheckedChangeListener(this);

        //获取OrderSys实例
        orderSys = new OrderSys(LoginRegisterActivity.this);

        // MARK
        db_ops = (DBFunction) orderSys;
    }

    //MARK
    @Override
    public void onClick(View v) {
        int phone;
        if(et_phone.getText().toString().isEmpty()){
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
             return;
        }
        else
            phone = Integer.parseInt(et_phone.getText().toString());


        boolean authorization;
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        String input_authcode = " ";
        if(et_authcode.getVisibility() == View.VISIBLE ) {
            // et_authcode处于可见状态
            input_authcode = et_authcode.getText().toString();
        }
        if(rb_business.isChecked()){
            authorization = true;//商家
        }
        else
            authorization = false;//顾客

        // MARK HERE, THE UUID IS JUST A PLACEHOLDER NO REAL MEANING.
        users user = new users(-1,phone,username,password,authorization);

        if(v.getId() == R.id.btn_verifycode)  {
            // 生成六位随机数字的验证码
            verifyCode = String.format("%06d", new Random().nextInt(999999));
            // 弹出验证码对话框
            AlertDialog.Builder buider = new AlertDialog.Builder(this);
            buider.setTitle("请记住验证码");
            buider.setMessage("手机号为" + phone + "的用户，您好！,本次注册的验证码是" + verifyCode + ",请输入验证码。");
            buider.setPositiveButton("好的", null);
            AlertDialog dialog = buider.create();
            dialog.show();
        }

        else if (v.getId()== R.id.btn_confirm) {
            if (!verifyCode.equals(et_verifycode.getText().toString())) {
                Toast.makeText(this, "验证码错误！", Toast.LENGTH_SHORT).show();
                return;
            }
            //注册商家用户
            if (rb_business.isChecked()) {
                if (input_authcode.equals(authcode)) {
                    db_ops.createUsers(user);
                    Toast.makeText(this, "注册成功!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "授权码无效!", Toast.LENGTH_SHORT).show();
                }
            }
            //注册普通用户
            else {
                db_ops.createUsers(user);
                Toast.makeText(this, "注册成功!", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 注册商家用户,显示授权码组件
        if (checkedId == R.id.rb_business) {
            tv_authcode.setVisibility(View.VISIBLE);
            et_authcode.setVisibility(View.VISIBLE);
        }
        // 注册普通用户，隐藏授权码组件
        if (checkedId == R.id.rb_customer) {
            tv_authcode.setVisibility(View.GONE);
            et_authcode.setVisibility(View.GONE);
        }
    }
}