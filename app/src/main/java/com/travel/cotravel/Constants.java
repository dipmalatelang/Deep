package com.travel.cotravel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class Constants {
    public static final DatabaseReference PicturesInstance = FirebaseDatabase.getInstance().getReference("Pictures");
    public static final DatabaseReference PhotoRequestInstance=FirebaseDatabase.getInstance().getReference("PhotoRequest");
    public static final DatabaseReference TrashInstance = FirebaseDatabase.getInstance().getReference("Trash");
    public static final DatabaseReference ChatListInstance = FirebaseDatabase.getInstance().getReference("Chatlist");
    public static final DatabaseReference ChatsInstance = FirebaseDatabase.getInstance().getReference("Chats");
    public static final DatabaseReference FavoritesInstance = FirebaseDatabase.getInstance().getReference("Favorites");
    public static final DatabaseReference ProfileVisitorInstance = FirebaseDatabase.getInstance().getReference("ProfileVisitor");
    public static final DatabaseReference TokensInstance = FirebaseDatabase.getInstance().getReference("Tokens");
    public static final DatabaseReference TripsInstance = FirebaseDatabase.getInstance().getReference("Trips");
    public static final DatabaseReference UsersInstance = FirebaseDatabase.getInstance().getReference("Users");
}
