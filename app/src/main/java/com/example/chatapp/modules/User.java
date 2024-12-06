/**
 * The User class represents a user in the chat application.
 * It contains information about the user's name, profile image, email, token, and ID.
 * Implements Serializable to allow user objects to be passed between activities.
 */
package com.example.chatapp.modules;

import java.io.Serializable;

public class User implements Serializable {
    /**
     * The first name of the user.
     */
    public String name;

    /**
     * The last name of the user.
     */
    public String lastName;

    /**
     * The profile image of the user, represented as a Base64 encoded string.
     */
    public String image;

    /**
     * The email address of the user.
     */
    public String email;

    /**
     * The FCM token for the user, used for sending push notifications.
     */
    public String token;

    /**
     * The unique ID of the user.
     */
    public String id;
}
