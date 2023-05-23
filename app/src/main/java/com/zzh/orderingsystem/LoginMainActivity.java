package com.zzh.orderingsystem;


// The code annotated with "MAKR" indicates code that has been modified or optimized by Hexiang Mao after the audit.

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.zzh.orderingsystem.DBFunction;
import com.zzh.orderingsystem.DataService;


import java.util.Random;

public class LoginMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    OrderSys orderSys;
    private TextView tv_password;
    private TextView tv_phone;
    private EditText et_password;
    private Button btn_forget;
    private CheckBox cb_remember;
    private EditText et_phone;
    private RadioButton rb_password;
    private RadioButton rb_verifycode;
    private Button btn_login;
    private Button btn_register;
    private String passwordForTest = "123456";
    private String verifyCode;
    private SharedPreferences preferences;
    private users user;
    private SharedPreferences.Editor editor;



    // MARK
    public DBFunction db_ops;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        tv_phone = findViewById(R.id.tv_phone);
        tv_password = findViewById(R.id.tv_password);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        btn_forget = findViewById(R.id.btn_forget);
        cb_remember = findViewById(R.id.ck_remember);
        rb_password = findViewById(R.id.rb_password);
        rb_verifycode = findViewById(R.id.rb_verifycode);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        RadioGroup rb_login = findViewById(R.id.rg_login);
        rb_login.setOnCheckedChangeListener(this);

        btn_forget.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        //获取OrderSys实例
        orderSys = new OrderSys(LoginMainActivity.this);

        //创建一个名为"config"的SharedPreferences对象
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        //读取保存在SharedPreferences中的数据，然后将这些数据设置到登录界面的控件中
        reload();



        //Mark
        db_ops = (DBFunction)orderSys;

    }
    private void reload() {
        if (preferences.contains("phone") && preferences.contains("password")) {
            String phone = preferences.getString("phone", "");
            et_phone.setText(phone);
            String password = preferences.getString("password", "");
            et_password.setText(password);
            cb_remember.setChecked(true);
        }
    }

    //选择登录方式
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 密码登录
        if(checkedId == R.id.rb_password)  {
            tv_password.setText("登录密码：");
            et_password.setHint("请输入密码");
            btn_forget.setText("忘记密码");
            cb_remember.setVisibility(View.VISIBLE);
        }
        // 验证码登录
        else if (checkedId == R.id.rb_verifycode)  {
            tv_password.setText("    验证码：");
            et_password.setHint("请输入验证码");
            btn_forget.setText("获取验证码");
            cb_remember.setVisibility(View.GONE);
        }
    }

    //MARK
    @Override
    public void onClick(View v) {
        //MARK
        int phone;
        String tmp;
            //“忘记密码”或“获取验证码”按钮
            if(v.getId() == R.id.btn_forget)  {
                tmp = et_phone.getText().toString();
                if (tmp.isEmpty()) {
                    Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    phone = Integer.parseInt(tmp);
                    tmp = "";
                }
                //String password = et_password.getText().toString();
                // user = orderSys.find_user(phone);
                // 密码登录：’btn_forget‘按钮表示忘记密码，跳转至找回密码页面
                if (rb_password.isChecked()) {
                    Intent intent = new Intent(this, LoginForgetActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }

                //验证码登录：’btn_forget‘按钮表示获取验证码
                else if (rb_verifycode.isChecked()) {
                    // 生成六位随机数字的验证码
                    verifyCode = String.format("%06d", new Random().nextInt(999999));
                    // 弹出验证码对话框
                    AlertDialog.Builder buider = new AlertDialog.Builder(this);
                    buider.setTitle("请记住验证码");
                    buider.setMessage("手机号为" + phone + "的用户，您好！,本次登录验证码是" + verifyCode + ",请输入验证码。");
                    buider.setPositiveButton("好的", null);
                    AlertDialog dialog = buider.create();
                    dialog.show();
                }
            }

                //“登录”按钮
            else if(v.getId() == R.id.btn_login)  {
            // 密码登录：密码校验
            // MARK
            tmp = et_phone.getText().toString();
            if (tmp.isEmpty()) {
                Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                return;
            } else {
                phone = Integer.parseInt(tmp);
                tmp = "";
            }
            String password = et_password.getText().toString();

            // old
            // user = orderSys.find_user(phone);
            // MARK

                /*
                if (rb_password.isChecked()) {
//                    //测试代码
//                    if (!passwordForTest.equals(et_password.getText().toString())) {
//                        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    // 提示用户登录成功
//                    loginSuccess();
                    //校验结果，0密码正确，-1未注册，-2密码错误

                    Integer check_result = orderSys.Login(phone,password);
                    if(check_result.equals(0)){
                        loginSuccess();
                    }
                    else if (check_result.equals(-2)) {
                        Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        Toast.makeText(this, "该用户未注册！", Toast.LENGTH_SHORT).show();
                    }
                }
                // 验证码登录：验证码校验
                else if (rb_verifycode.isChecked()) {
                    if (!verifyCode.equals(et_password.getText().toString())) {
                        Toast.makeText(this, "验证码错误！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 提示用户登录成功
                    loginSuccess();
                }
                */

            //MARK
            if (rb_password.isChecked()) {
                int ret = db_ops.Login(phone, password);
                switch (ret) {
                    case 0:
                        user = db_ops.find_user(phone);
                        loginSuccess();
                        break;
                    case -2:
                        Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
                        break;
                    case -1:
                        Toast.makeText(this, "该用户未注册！", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }

                //"注册"按钮
            else if (v.getId() == R.id.btn_register)  {
                Intent intent = new Intent(this, LoginRegisterActivity.class);
                startActivity(intent);
        }
    }

    // 校验通过，登录成功
    private void loginSuccess() {
        String desc = String.format("用户’%s‘，您好！您已经成功通过登录验证",user.username);
        // 弹出提醒对话框，提示用户登录成功
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登录成功");
        builder.setMessage(desc);
        builder.setPositiveButton("进入主页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(user.authorization){
                    Intent intent = new Intent(LoginMainActivity.this, MainActivityBusiness.class);
                    // TO DO
                    // PASSING USER INFO LIKE UUID, USERNAME... TO THE NEXT ACT.

                    intent.putExtra("uuid", user.uuid);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(LoginMainActivity.this, MainActivityCustomer.class);
                    intent.putExtra("uuid", user.uuid);
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton("返回", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        editor = preferences.edit();
        if (cb_remember.isChecked()) {
            //勾选了“记住密码”的选项后，将用户输入的账号和密码保存到SharedPreferences中，以便下次登录时自动填充
            editor.putString("phone", et_phone.getText().toString());
            editor.putString("password", et_password.getText().toString());
            editor.commit();
        }
        else{
            //清空SharedPreferences中数据
            editor.clear();
            editor.commit();
        }
    }
}