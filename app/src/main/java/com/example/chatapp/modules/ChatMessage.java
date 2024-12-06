/**
 * The ChatMessage class represents a message exchanged between users in the chat application.
 * It contains information about the sender, receiver, content of the message, and the timestamp.
 */
package com.example.chatapp.modules;

import java.util.Date;

public class ChatMessage {
    /**
     * ID of the user who sent the message.
     */
    public String senderId;

    /**
     * ID of the user who received the message.
     */
    public String receiverId;

    /**
     * The content of the message.
     */
    public String message;

    /**
     * The date and time when the message was sent, represented as a String.
     */
    public String dateTime;

    /**
     * The date and time when the message was sent, represented as a Date object.
     */
    public Date dateObject;
}
