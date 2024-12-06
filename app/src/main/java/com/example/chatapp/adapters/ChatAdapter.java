/**
 * The ChatAdapter class is responsible for managing the chat messages displayed in a RecyclerView.
 * It differentiates between sent and received messages and displays them accordingly.
 */
package com.example.chatapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.chatapp.databinding.ItemContainerSentMessageBinding;
import com.example.chatapp.modules.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Profile image of the receiver.
     */
    private Bitmap receiverProfileImage;

    /**
     * List of chat messages to be displayed.
     */
    private final List<ChatMessage> chatMessages;

    /**
     * Constant representing the view type for sent messages.
     */
    public static final int VIEW_TYPE_SENT = 1;

    /**
     * Constant representing the view type for received messages.
     */
    public static final int VIEW_TYPE_RECEIVED = 2;

    /**
     * ID of the sender.
     */
    private final String sendId;

    /**
     * Constructor for initializing ChatAdapter with chat messages, receiver's profile image, and sender ID.
     *
     * @param chatMessages List of chat messages.
     * @param receiverProfileImage Profile image of the receiver.
     * @param sendId ID of the sender.
     */
    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String sendId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.sendId = sendId;
    }

    /**
     * Creates and returns the appropriate ViewHolder based on the message type (sent or received).
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view (sent or received).
     * @return A ViewHolder representing either a sent or received message.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHOlder(ItemContainerSentMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    /**
     * Binds the chat message data to the appropriate ViewHolder.
     *
     * @param holder The ViewHolder for the chat message.
     * @param position The position of the message in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHOlder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    /**
     * Returns the total number of chat messages.
     *
     * @return The total count of chat messages.
     */
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    /**
     * Determines the type of view (sent or received) based on the sender ID.
     *
     * @param position The position of the message in the list.
     * @return The type of view (sent or received).
     */
    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(sendId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    /**
     * ViewHolder class for sent messages.
     */
    static class SentMessageViewHOlder extends RecyclerView.ViewHolder {

        /**
         * Binding for accessing UI elements in the sent message layout.
         */
        private final ItemContainerSentMessageBinding binding;

        /**
         * Constructor for initializing SentMessageViewHolder.
         *
         * @param itemContainerSentMessageBinding The binding for the sent message layout.
         */
        public SentMessageViewHOlder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        /**
         * Sets the data for a sent message.
         *
         * @param chatMessage The chat message to be displayed.
         */
        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    /**
     * ViewHolder class for received messages.
     */
    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {

        /**
         * Binding for accessing UI elements in the received message layout.
         */
        private final ItemContainerReceivedMessageBinding binding;

        /**
         * Constructor for initializing ReceiverMessageViewHolder.
         *
         * @param itemContainerReceivedMessageBinding The binding for the received message layout.
         */
        public ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        /**
         * Sets the data for a received message, including the receiver's profile image.
         *
         * @param chatMessage The chat message to be displayed.
         * @param receiverProfileImage The profile image of the message receiver.
         */
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }
    }
}
