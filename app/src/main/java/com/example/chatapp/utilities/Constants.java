/**
 * Holds constant values used across the chat app.
 * These constants act as keys for accessing values in Firebase Firestore and SharedPreferences.
 */
package com.example.chatapp.utilities;

public class Constants {

    /**
     * Firebase Firestore collection name for storing user details.
     */
    public static final String KEY_COLLECTION_USERS = "Users";

    /**
     * Key for storing and retrieving the user's first name.
     */
    public static final String KEY_NAME = "name";

    /**
     * Key for storing and retrieving the user's last name.
     */
    public static final String KEY_LAST_NAME = "lastName";

    /**
     * Key for storing and retrieving the user's email address.
     */
    public static final String KEY_EMAIL = "email";

    /**
     * Key for storing and retrieving the user's password.
     */
    public static final String KEY_PASSWORD = "password";

    /**
     * Key for storing and retrieving the unique user ID.
     */
    public static final String KEY_USER_ID = "Userid";

    /**
     * Key indicating whether the user is signed in (used in SharedPreferences).
     */
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    /**
     * Name for the SharedPreferences file used by the chat app.
     */
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";

    /**
     * Key for storing and retrieving the user's profile image (Base64 encoded).
     */
    public static final String KEY_IMAGE = "image";

    /**
     * Key for storing and retrieving the user's FCM token (used for push notifications).
     */
    public static final String KEY_FCM_TOKEN = "fcmToken";

    /**
     * Key for storing and retrieving user objects.
     */
    public static final String KEY_USER = "user";

    /**
     * Firebase Firestore collection name for storing chat messages.
     */
    public static final String KEY_COLLECTION_CHAT = "chat";

    /**
     * Key for storing and retrieving the sender's user ID in a chat message.
     */
    public static final String KEY_SENDER_ID = "senderId";

    /**
     * Key for storing and retrieving the receiver's user ID in a chat message.
     */
    public static final String KEY_RECEIVER_ID = "receiverId";

    /**
     * Key for storing and retrieving the content of a chat message.
     */
    public static final String KEY_MESSAGE = "message";

    /**
     * Key for storing and retrieving the timestamp of a chat message.
     */
    public static final String KEY_TIMESTAMP = "timeStamp";
}
