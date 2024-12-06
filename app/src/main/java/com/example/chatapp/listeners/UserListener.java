/**
 * The UserListener interface is used for handling user click events in the chat application.
 * It defines a callback method that is triggered when a user is clicked.
 */
package com.example.chatapp.listeners;

import com.example.chatapp.modules.User;

public interface UserListener {
    /**
     * Callback method that is triggered when a user is clicked.
     *
     * @param user The user that was clicked.
     */
    void onUserClicked(User user);
}
