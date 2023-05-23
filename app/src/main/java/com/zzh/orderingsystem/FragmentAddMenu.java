package com.zzh.orderingsystem;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class FragmentAddMenu extends Fragment {


    private Button btnChoose;
    private Button btnAdd;
    private ImageView img;
    private Bitmap bitmap;
    public static final int PICK_PHOTO = 102;
    private Uri imageUri;

    ActivityResultLauncher<Intent> launcher;

    public FragmentAddMenu() {
        // Required empty public constructor
    }

    public static FragmentAddMenu newInstance(String param1, String param2) {
        FragmentAddMenu fragment = new FragmentAddMenu();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // 处理从相册选择图片的结果数据
                        if(result.getResultCode() == RESULT_OK && result.getData() != null){
                            imageUri = result.getData().getData();
                            try {
                                InputStream is = getActivity().getContentResolver().openInputStream(imageUri);
                                bitmap = BitmapFactory.decodeStream(is);
                                // do something with bitmap
                                bitmap = rescaleBitmap(bitmap, 500, 500);
                                img.setImageBitmap(bitmap);
                                Log.d("huashi", "set success");

                            } catch (Exception e) {
                                Log.d("huashi", "set failed");
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_menu, container, false);

        btnChoose = view.findViewById(R.id.btnSelectPic);
        img = view.findViewById(R.id.iv);

        btnAdd = view.findViewById(R.id.btnAdd);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开相册
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                launcher.launch(intent); // 打开相册
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewBtn) {
                EditText etName = view.findViewById(R.id.etName);
                EditText etPrice = view.findViewById(R.id.etPrice);
                EditText etDespription = view.findViewById(R.id.etDescription);

                RadioButton rb;
                RadioButton rb1 = view.findViewById(R.id.rbMainfood);
                RadioButton rb2 = view.findViewById(R.id.rbDrink);
                RadioButton rb3 = view.findViewById(R.id.rbSnack);

                if(rb2.isChecked())
                    rb = rb2;
                else if(rb3.isChecked())
                    rb = rb3;
                else
                    rb = rb1;

                OrderSys orderSys = new OrderSys(getActivity());
                DBFunction db = (DBFunction) orderSys;
                foods food = new foods(etName.getText().toString(), -1, false,
                        etDespription.getText().toString(), Double.parseDouble(etPrice.getText().toString()), rb.getText().toString());
                db.insertFood(food);
                File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file = new File(dir, etName.getText().toString()+".jpg");

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    Log.d("huashi", "image saved!");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                etName.setText("");
                etPrice.setText("");
                etDespription.setText("");
            }
        });

        return view;
    }


    private Bitmap rescaleBitmap(Bitmap pic, int newWidth, int newHeight){
        int width = pic.getWidth();
        int height = pic.getHeight();
        float scaleWidth = ((float) newWidth)/width;
        float scaleHeight = ((float) newHeight)/height;
        Matrix mat = new Matrix();
        mat.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(pic, 0, 0, width, height, mat, true);
    }
}