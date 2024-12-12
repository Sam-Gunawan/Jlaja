package com.example.jlaja;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jlaja.model.UserModel;
import com.example.jlaja.utils.AndroidUtil;
import com.example.jlaja.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Profile extends AppCompatActivity  {

   ImageView profilePic;
   EditText usernameInput;
   EditText phoneInput;
   Button updateProfileBtn;
   ImageButton backBtn;
   ProgressBar progressBar;
   TextView logoutBtn;

   UserModel currentUserModel;
   ActivityResultLauncher<Intent> imagePickLauncher;
   Uri selectedImageUri;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_profile);

       profilePic = findViewById(R.id.profile_image_view);
       usernameInput = findViewById(R.id.profile_username);
//       phoneInput = findViewById(R.id.profile_phone);
       updateProfileBtn = findViewById(R.id.profle_update_btn);
       progressBar = findViewById(R.id.profile_progress_bar);
       logoutBtn = findViewById(R.id.logout_btn);
       backBtn = findViewById(R.id.BackToHomePageButton);

       imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
               result -> {
                   if (result.getResultCode() == Activity.RESULT_OK) {
                       Intent data = result.getData();
                       if (data != null && data.getData() != null) {
                           selectedImageUri = data.getData();
                           AndroidUtil.setProfilePic(this, selectedImageUri, profilePic);
                       }
                   }
               }
       );

       getUserData();

       updateProfileBtn.setOnClickListener(v -> updateBtnClick());

       logoutBtn.setOnClickListener(v -> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               FirebaseUtil.logout();
               Intent intent = new Intent(this, UserAuth.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
           }
       }));

       backBtn.setOnClickListener(v -> FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Intent intent = new Intent(this, FirstTimeUserHome.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
           }
       }));


       profilePic.setOnClickListener(v -> ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
               .createIntent(new Function1<Intent, Unit>() {
                   @Override
                   public Unit invoke(Intent intent) {
                       imagePickLauncher.launch(intent);
                       return null;
                   }
               }));


   }

   void updateBtnClick() {
       String newUsername = usernameInput.getText().toString();
       if (newUsername.isEmpty() || newUsername.length() < 3) {
           usernameInput.setError("Username length should be at least 3 chars");
           return;
       }
       currentUserModel.setUsername(newUsername);
       setInProgress(true);

       if (selectedImageUri != null) {
           FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                   .addOnCompleteListener(task -> updateToFirestore());
       } else {
           updateToFirestore();
       }
   }

   void updateToFirestore() {
       FirebaseUtil.currentUserDetails().set(currentUserModel)
               .addOnCompleteListener(task -> {
                   setInProgress(false);
                   if (task.isSuccessful()) {
                       AndroidUtil.showToast(this, "Updated successfully");
                   } else {
                       AndroidUtil.showToast(this, "Update failed");
                   }
               });
   }

   void getUserData() {
       setInProgress(true);

       FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
               .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       Uri uri = task.getResult();
                       AndroidUtil.setProfilePic(this, uri, profilePic);
                   }
               });

       FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
           setInProgress(false);
           currentUserModel = task.getResult().toObject(UserModel.class);
           usernameInput.setText(currentUserModel.getUsername());
//           phoneInput.setText(currentUserModel.getPhone());
       });
   }

   void setInProgress(boolean inProgress) {
       if (inProgress) {
           progressBar.setVisibility(View.VISIBLE);
           updateProfileBtn.setVisibility(View.GONE);
       } else {
           progressBar.setVisibility(View.GONE);
           updateProfileBtn.setVisibility(View.VISIBLE);
       }
   }

}









