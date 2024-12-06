/**
 * The UsersAdapter class is responsible for displaying a list of users in a RecyclerView.
 * It binds user data to each item in the list and manages click events to initiate chats.
 */
package com.example.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.databinding.ItemContainerUserBinding;
import com.example.chatapp.listeners.UserListener;
import com.example.chatapp.modules.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    /**
     * List of users to be displayed.
     */
    private final List<User> users;

    /**
     * Listener for user click events.
     */
    private final UserListener userListener;

    /**
     * Constructor for initializing UsersAdapter with a list of users and a click listener.
     *
     * @param users List of users to be displayed.
     * @param userListener Listener for handling user click events.
     */
    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    /**
     * Creates and returns a UserViewHolder for displaying user information.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view (not used here since all items are users).
     * @return A UserViewHolder representing a user item.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(itemContainerUserBinding);
    }

    /**
     * Binds the user data to the appropriate UserViewHolder.
     *
     * @param holder The UserViewHolder for the user.
     * @param position The position of the user in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    /**
     * Returns the total number of users.
     *
     * @return The total count of users.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder class for displaying user information.
     */
    class UserViewHolder extends RecyclerView.ViewHolder {

        /**
         * Binding for accessing UI elements in the user item layout.
         */
        ItemContainerUserBinding binding;

        /**
         * Constructor for initializing UserViewHolder.
         *
         * @param itemContainerUserBinding The binding for the user item layout.
         */
        public UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        /**
         * Sets the user data to be displayed in the user item.
         *
         * @param user The user whose data is to be displayed.
         */
        void setUserData(User user) {
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));

            // Set an on-click listener for the user item to initiate chat
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    /**
     * Decodes a Base64 encoded image string to a Bitmap.
     *
     * @param encodeImage The Base64 encoded image string.
     * @return The decoded Bitmap image.
     */
    private Bitmap getUserImage(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
