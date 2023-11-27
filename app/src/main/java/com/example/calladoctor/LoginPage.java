package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    private TextInputLayout email;
    private TextInputLayout password;
    private TextView forgetPasswordButton;
    private TextView joinProgramButton;
    private Button loginButton;
    private Button registerButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressBar loadingIndicator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        setReference();
    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.signupButton);
        forgetPasswordButton = findViewById(R.id.forgetPassword);
        joinProgramButton = findViewById(R.id.joingProgramButton);


        forgetPasswordButton.setOnClickListener(v -> {
            showForgetPasswordDialog();
        });


        registerButton.setOnClickListener(view -> {
            //Go to registration page
            Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
            startActivity(intent);
        });

        joinProgramButton.setOnClickListener(v -> {
            //Add function for clinic to register into program
            Intent intent = new Intent(LoginPage.this, JoinProgramPage.class);
            startActivity(intent);
        });


        loginButton.setOnClickListener(view -> {

            //TODO: Validate the information then navigate to the home page

            // Retrieve user input
            String enteredEmail = email.getEditText().getText().toString().trim();
            String enteredPassword = password.getEditText().getText().toString().trim();

            if (enteredEmail.isEmpty()){
                email.setError("Email is required!");
            } else if (enteredPassword.isEmpty()) {
                password.setError("Password is required!");

            }  else {
                email.setError(null);
                password.setError(null);
                // Perform login
                loginUser(enteredEmail, enteredPassword);
            }
        });
    }

    private void loginUser(String emailStr, String password) {
        email.setError(null);
        this.password.setError(null);
        loadingIndicator.setVisibility(View.VISIBLE);

        // Sign in with email and password using Firebase Authentication
        auth.signInWithEmailAndPassword(emailStr, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Now you have the authenticated user, you can proceed to store additional user data
                            getUserData(emailStr);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        email.setError("Email or Password is Incorrect");
                        this.password.setError("Email or Password is Incorrect");
                        loadingIndicator.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private void getUserData(String userEmail) {
        // Query Firestore to get additional user data
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if a user with the entered email exists in Firestore
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot userSnapshot = task.getResult().getDocuments().get(0);

                            String role = userSnapshot.getString("role");
                            Log.d("LoginPage", role);


                            Intent intent;

                            if (role.equals("patient")){
                                String id = userSnapshot.getId();
                                String fNameStr = userSnapshot.getString("firstName");
                                String lNameStr = userSnapshot.getString("lastName");
                                String genderStr = userSnapshot.getString("gender");
                                String icStr = userSnapshot.getString("ic");
                                String birthDateStr = userSnapshot.getString("birthDate");
                                String phoneStr = userSnapshot.getString("phone");
                                String emailStr = userSnapshot.getString("email");
                                String addressStr = userSnapshot.getString("address");

                                SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("documentID", id);
                                editor.putString("firstName", fNameStr);
                                editor.putString("lastName", lNameStr);
                                editor.putString("gender", genderStr);
                                editor.putString("ic", icStr);
                                editor.putString("birthDate", birthDateStr);
                                editor.putString("phone", phoneStr);
                                editor.putString("email", emailStr);
                                editor.putString("address", addressStr);
                                editor.putString("status", "login");
                                editor.putString("role", role);

                                editor.apply();

                                intent = new Intent(LoginPage.this, PatientHomePage.class);
                                startActivity(intent);
                                finish();

                            } else if (role.equals("doctor")) {
                                // TODO: Go to doctor home page
                                
                            } else if (role.equals("clinic")) {
                                // TODO: Go to clinic home page
                                String id = userSnapshot.getId();
                                String clinicName = userSnapshot.getString("clinicName");

                                SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("documentID", id);
                                editor.putString("clinicName", clinicName);
                                editor.putString("status", "login");
                                editor.putString("role", role);


                                editor.apply();

                                intent = new Intent(LoginPage.this, ClinicHomePage.class);
                                startActivity(intent);
                                finish();

                                
                            } else if (role.equals("government")) {
                                // TODO: Go to government page
                                String id = userSnapshot.getId();
                                String email = userSnapshot.getString("email");

                                SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("documentID", id);
                                editor.putString("email", email);
                                editor.putString("status", "login");
                                editor.putString("role", role);

                                editor.apply();

                                intent = new Intent(LoginPage.this, GovernmentHomePage.class);
                                startActivity(intent);
                                finish();


                            }
                            loadingIndicator.setVisibility(View.INVISIBLE);



                        } else {
                            // User not found, display an error message
                            email.setError("Email or Password is Incorrect");
                            this.password.setError("Email or Password is Incorrect");
                            loadingIndicator.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        // Handle errors
                        String errorMessage = "Error checking credentials: " + task.getException().getMessage();
                        Toast.makeText(LoginPage.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("LoginPage", errorMessage);
                        loadingIndicator.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void showForgetPasswordDialog() {
        Dialog forgetPasswordDialog = new Dialog(this);
        forgetPasswordDialog.setContentView(R.layout.forget_password_dialog);
        forgetPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        forgetPasswordDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        forgetPasswordDialog.setCancelable(true);

        TextInputLayout email = forgetPasswordDialog.findViewById(R.id.emailInput);
        MaterialButton buttonSubmitMatricNo = forgetPasswordDialog.findViewById(R.id.continueButton);
        TextView buttonCancelMatricNo = forgetPasswordDialog.findViewById(R.id.forgetPasswordReturnButton);
//        ProgressBar loadingIndicator = forgetPasswordDialog.findViewById(R.id.forgetPasswordLoadingIndicator);

        // Set onClickListener for the submit button
        buttonSubmitMatricNo.setOnClickListener(v -> {
            String emailText = email.getEditText().getText().toString().trim();

            // Perform validation on enteredMatricNo (you may want to check for empty input, etc.)
            if (!emailText.isEmpty()) {
                sendPasswordResetEmail(emailText);
            } else {
                email.setError("Email is required");
            }

            forgetPasswordDialog.dismiss();

        });

        // Set onClickListener for the cancel button
        buttonCancelMatricNo.setOnClickListener(v -> forgetPasswordDialog.dismiss());

        // Show the Matric No input dialog
        forgetPasswordDialog.show();
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Password reset email sent successfully
                        Toast.makeText(LoginPage.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle password reset email failure
                        Toast.makeText(LoginPage.this, "Error sending password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginPage", "Error sending password reset email", task.getException());
                    }
                });
    }

    private void showExitDialog(){
        Dialog exitConfirmDialog;

        exitConfirmDialog = new Dialog(this);
        exitConfirmDialog.setContentView(R.layout.confirm_exit_dialog);
        exitConfirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        exitConfirmDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        exitConfirmDialog.setCancelable(true);

        MaterialButton exitDialogConfirmButton, exitDialogCancelButton;

        exitDialogConfirmButton = exitConfirmDialog.findViewById(R.id.confirmButton);
        exitDialogCancelButton = exitConfirmDialog.findViewById(R.id.cancelButton);

        exitDialogCancelButton.setOnClickListener(v -> {
            exitConfirmDialog.dismiss();
        });

        exitDialogConfirmButton.setOnClickListener(v -> {
            super.onBackPressed();
        });

        exitConfirmDialog.show();
    }

    @Override
    public void onBackPressed() {
        //Check for confirmation to exit or not, need to apply for all page
        showExitDialog();

        //This is add due to ide error
        if(false){
            super.onBackPressed();
        }

    }
}