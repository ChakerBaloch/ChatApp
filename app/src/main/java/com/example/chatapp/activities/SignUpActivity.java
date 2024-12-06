package com.example.chatapp.activities;

import static android.content.Intent.ACTION_PICK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.example.chatapp.utilities.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.example.chatapp.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Handles user registration and setting up a new user profile.
 */
public class SignUpActivity extends AppCompatActivity {

    // Binding to access UI components in the sign-up layout
    private ActivitySignUpBinding binding;

    // Manager for handling user preferences
    private PreferenceManager preferenceManager;

    // Stores encoded image string of user profile picture
    private String encodeImage;

    /**
     * Initializes the sign-up activity and sets listeners for UI interactions.
     *
     * @param savedInstanceState the saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view binding for accessing UI elements
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize preference manager
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Set up listeners for UI interactions
        setListeners();
    }

    /**
     * Sets click listeners for the sign-in link, sign-up button, and profile image selector.
     */
    private void setListeners() {
        // Navigate back to sign-in screen if "Sign In" text is clicked
        binding.textSignIn.setOnClickListener(view -> onBackPressed());

        // Trigger sign-up process when sign-up button is clicked
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidateSignUpDetails()) {
                signUp();
            }
        });

        // Allow the user to select a profile image when layout image is clicked
        binding.layoutImage.setOnClickListener(view -> {
            Intent intent = new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    /**
     * Displays a toast message with the specified text.
     *
     * @param message the message to be displayed
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Registers a new user in Firebase Firestore and handles success or failure.
     */
    private void signUp() {
        // Display loading indicator
        loading(true);

        // Initialize Firebase Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Create a new user entry with the provided details
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_LAST_NAME, binding.lastName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodeImage);

        // Add user data to Firestore
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);

                    // Save user session data in preferences
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, binding.lastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodeImage);

                    // Navigate to main activity and clear the back stack
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(exception -> {
                    // If sign-up fails, hide loading and show error message
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    /**
     * Encodes a Bitmap image into a Base64 string for storage in Firestore.
     *
     * @param bitmap the bitmap image to encode
     * @return a Base64 encoded string representing the image
     */
    private String encodeImage(Bitmap bitmap) {
        // Set preview size to optimize image storage
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        // Resize the bitmap for encoding
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        // Compress the bitmap to JPEG format and store in ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        // Convert to byte array and encode as Base64 string
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // Launcher for the image picker, allowing the user to select an image from the gallery
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Open input stream and decode the selected image
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Set the selected image as profile picture and encode it
                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodeImage = encodeImage(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    /**
     * Validates sign-up details entered by the user.
     *
     * @return true if all details are valid, false otherwise
     */
    private Boolean isValidateSignUpDetails() {
        // Check if image is selected
        if (encodeImage == null) {
            showToast("Please select your image");
            return false;
        }
        // Check if name is entered
        if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your First Name");
            return false;
        }
        // Check if name is entered
        if (binding.lastName.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your Last Name");
            return false;
        }
        // Validate email format
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please Enter a valid Email");
            return false;
        }
        // Check if password is entered
        if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your Password");
            return false;
        }
        // Check if confirm password is entered and matches password
        if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Please Confirm Password");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Password and Confirm Password must be the same");
            return false;
        }
        return true;
    }

    /**
     * Manages visibility of the loading spinner and sign-up button.
     *
     * @param isLoading true if loading, false otherwise
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            // Show progress bar and hide sign-up button during loading
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            // Hide progress bar and show sign-up button once loading is complete
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }
}
