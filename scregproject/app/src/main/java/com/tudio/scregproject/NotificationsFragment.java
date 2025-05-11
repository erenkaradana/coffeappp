package com.tudio.scregproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationCombinedAdapter adapter;
    private List<NotificationItem> notificationItems;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();

        notificationItems = new ArrayList<>();
        adapter = new NotificationCombinedAdapter(getContext(), notificationItems);
        recyclerView.setAdapter(adapter);

        loadCampaigns();

        return view;
    }

    private void loadCampaigns() {
        firestore.collection("campaigns")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    notificationItems.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String title = doc.getString("title");
                        String description = doc.getString("description");
                        String imageUrl = doc.getString("imageUrl");

                        NotificationItem item = new NotificationItem(title, description, imageUrl);
                        notificationItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        System.out.println("tırt"));    }
}
