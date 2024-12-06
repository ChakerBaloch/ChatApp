/**
 * ChatActivity class for the Chat App.
 * This activity is responsible for handling chat messages between users.
 * It initializes chat messages, listens for incoming messages, and manages sending messages.
 */
package com.example.chatapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.adapters.ChatAdapter;
import com.example.chatapp.databinding.ActivityChatBinding;
import com.example.chatapp.modules.ChatMessage;
import com.example.chatapp.modules.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    /**
     * Binding object for accessing views in the activity_chat.xml layout.
     */
    private ActivityChatBinding binding;

    /**
     * User object representing the receiver of the chat messages.
     */
    private User receiverUser;

    /**
     * List to store chat messages.
     */
    private List<ChatMessage> chatMessages;

    /**
     * Adapter for displaying chat messages in the RecyclerView.
     */
    private ChatAdapter chatAdapter;

    /**
     * PreferenceManager object to manage shared preferences for user session data.
     */
    private PreferenceManager preferenceManager;

    /**
     * Firebase Firestore database instance.
     */
    private FirebaseFirestore database;

    /**
     * Called when the activity is first created.
     * Initializes binding, loads receiver details, sets listeners, initializes components, and starts listening for messages.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();  // Load receiver details from intent extras
        setListeners();  // Set click listeners for UI components
        init();  // Initialize chat components
        listenMessage();  // Start listening for incoming messages
    }

    /**
     * Initializes chat messages list, chat adapter, and Firebase Firestore instance.
     */
    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>(); // Initialize chat messages list
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter); // Set adapter for RecyclerView
        database = FirebaseFirestore.getInstance(); // Initialize Firestore instance
    }

    /**
     * Sends a message from the user to the receiver.
     * The message is stored in the Firebase Firestore database.
     */
    private void sendMessages() {
        HashMap<String, Object> message = new HashMap<>();
        // Add sender and receiver information to the message
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        // Add the message content and timestamp
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        // Add the message to Firestore
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        // Clear the input message box after sending
        binding.inputMessage.setText(null);
    }

    /**
     * Listens for incoming messages between the sender and receiver.
     * Updates the UI when new messages are added to the database.
     */
    private void listenMessage() {
        // Listen for messages sent by the current user to the receiver
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);

        // Listen for messages sent by the receiver to the current user
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    /**
     * EventListener for monitoring changes in the Firestore chat collection.
     * Adds new messages to the chat list and updates the UI.
     */
    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return; // Exit if there is an error
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    // Set chat message details from Firestore document
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(
                            documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            // Sort chat messages by timestamp
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeChanged(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE); // Show chat RecyclerView
        }
        binding.progressBar.setVisibility(View.GONE); // Hide progress bar after loading messages
    });

    /**
     * Converts an encoded image string to a Bitmap.
     *
     * @param encodedImage The Base64 encoded string representing the image.
     * @return The decoded Bitmap image.
     */
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Loads details of the receiver user from the intent.
     * Sets the receiver's name in the UI.
     */
    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name); // Set receiver's name in the TextView
    }

    /**
     * Sets click listeners for UI components.
     * Allows user to navigate back or send a message.
     */
    private void setListeners() {
        // Listener for back button
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        // Listener for send button
        binding.layoutSend.setOnClickListener(v -> sendMessages());
    }

    /**
     * Formats a Date object to a readable date-time string.
     *
     * @param date The Date object to be formatted.
     * @return A formatted string representing the date and time.
     */
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}
