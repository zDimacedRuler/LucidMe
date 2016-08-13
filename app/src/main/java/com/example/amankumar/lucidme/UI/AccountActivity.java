package com.example.amankumar.lucidme.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amankumar.lucidme.Account.LoginActivity;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Update_Password.UpdatePasswordActivity;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AccountActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 100;
    private final long ONE_MEGABYTE = 1024 * 1024;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    Bitmap profilePic;
    Toolbar toolbar;
    ImageView profileImage;
    TextView userNameText, emailText;
    LinearLayout changePasswordLayout, logOutLayout;
    String currentUser, userName, currentUserEmail;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference, profileRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    Switch pinSwitch;
    EditText pinEdit, rePinEdit;
    Boolean pinState;
    private Bitmap uploadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        toolbar = (Toolbar) findViewById(R.id.AA_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
        String fileName = currentUser + ".png";
        profilePic = getThumbnail(fileName);
        if (profilePic == null) {
            profileRef = storageReference.child(currentUser).child(Constants.CONSTANT_PROFILE_PIC);
            profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profileImage.setImageBitmap(bitmap1);
                    saveImageToInternalStorage(bitmap1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } else {
            profileImage.setImageBitmap(profilePic);
        }
        userNameText.setText(userName);
        emailText.setText(currentUserEmail);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString(Constants.CURRENT_USER, null).apply();
                    spe.putString(Constants.CURRENT_USER_NAME, null).apply();
                    spe.putBoolean(Constants.PIN_STATE,false).apply();
                    spe.putString(Constants.PIN_PASSWORD,"0000").apply();
                    takeUserToLoginScreenOnUnAuth();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);
        logOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Logging out will delete all cached data related to this account?");
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });
        pinState = sp.getBoolean(Constants.PIN_STATE, false);
        pinSwitch.setChecked(pinState);
        pinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.dialog_pin, null);
                    builder.setView(view);
                    builder.setCancelable(false);
                    pinEdit = (EditText) view.findViewById(R.id.AA_pin_dialog);
                    rePinEdit = (EditText) view.findViewById(R.id.AA_repin_dialog);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pinText = pinEdit.getText().toString();
                            String rePinText = rePinEdit.getText().toString();
                            if (pinText.length() == 4 && rePinText.length() == 4 && pinText.equals(rePinText)) {
                                spe.putString(Constants.PIN_PASSWORD, pinText).apply();
                                spe.putBoolean(Constants.PIN_STATE, true).apply();
                                Toast.makeText(AccountActivity.this, "Pin lock activated", Toast.LENGTH_SHORT).show();
                            } else {
                                pinSwitch.setChecked(false);
                                Toast.makeText(AccountActivity.this, "Pin should match and must be of 4 characters", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pinSwitch.setChecked(false);
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    pinEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    });
                    dialog.show();
                } else {
                    spe.putBoolean(Constants.PIN_STATE, false).apply();
                }
            }
        });
    }

    private boolean saveImageToInternalStorage(Bitmap bitmap) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            String fileName = currentUser + ".png";
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    public Bitmap getThumbnail(String filename) {
        Bitmap thumbnail = null;
        try {
            File filePath = getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            Log.e(" internal storage Error", ex.getMessage());
        }
        return thumbnail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spe = sp.edit();
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        userName = sp.getString(Constants.CURRENT_USER_NAME, "");
        currentUserEmail = Utils.decodeEmail(currentUser);
        firebaseStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
        profileImage = (ImageView) findViewById(R.id.AA_avatar);
        userNameText = (TextView) findViewById(R.id.AA_user_name_text);
        emailText = (TextView) findViewById(R.id.AA_email_text);
        changePasswordLayout = (LinearLayout) findViewById(R.id.AA_change_password);
        logOutLayout = (LinearLayout) findViewById(R.id.AA_log_out);
        pinSwitch = (Switch) findViewById(R.id.AA_pin_switch);
    }

    private void takeUserToLoginScreenOnUnAuth() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                profileRef = storageReference.child(currentUser).child(Constants.CONSTANT_PROFILE_PIC);
                if (Build.VERSION.SDK_INT < 19) {
                    String selectedImagePath = getPath(selectedImageUri);
                    uploadedImage = BitmapFactory.decodeFile(selectedImagePath);
                    uploadedImage = getResizedBitmap(uploadedImage, 400);
                    profileImage.setImageBitmap(uploadedImage);
                    saveImageToInternalStorage(uploadedImage);
                    uploadImageHandler(uploadedImage);
                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        uploadedImage = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        uploadedImage = getResizedBitmap(uploadedImage, 400);
                        uploadImageHandler(uploadedImage);
                        profileImage.setImageBitmap(uploadedImage);
                        saveImageToInternalStorage(uploadedImage);
                        parcelFileDescriptor.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void uploadImageHandler(final Bitmap uploadedImage) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    uploadedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    UploadTask uploadTask = profileRef.putBytes(byteArray);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("HomeActivity:", "Upload failed");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("HomeActivity:", "Upload successful");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void updatePasswordHandler(View view) {
        Intent intent=new Intent(this, UpdatePasswordActivity.class);
        startActivity(intent);
    }
}
