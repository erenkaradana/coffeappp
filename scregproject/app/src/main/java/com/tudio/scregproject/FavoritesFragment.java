package com.tudio.scregproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerViewFavorites;
    private ProductAdapter productAdapter;
    private List<Product> favoriteProducts;
    private TextView textViewEmptyFavorites;
    private ImageButton buttonBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        textViewEmptyFavorites = view.findViewById(R.id.textViewEmptyFavorites);
        buttonBack = view.findViewById(R.id.buttonBack);
        favoriteProducts = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), favoriteProducts);
        recyclerViewFavorites.setAdapter(productAdapter);

        loadFavoriteProducts();
        buttonBack.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }

    private void loadFavoriteProducts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Giriş yapmış kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .document(uid)
                .collection("userFavorites")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Set<String> favoriteNames = new HashSet<>();

                    for (DocumentSnapshot doc : snapshot) {
                        String productName = doc.getString("name");
                        if (productName != null) {
                            favoriteNames.add(productName);
                        }
                    }

                    if (favoriteNames.isEmpty()) {
                        favoriteProducts.clear();
                        productAdapter.notifyDataSetChanged();
                        textViewEmptyFavorites.setVisibility(View.VISIBLE);
                        recyclerViewFavorites.setVisibility(View.GONE);
                        return;
                    }
                    else {
                        textViewEmptyFavorites.setVisibility(View.GONE);
                        recyclerViewFavorites.setVisibility(View.VISIBLE);
                    }
                    // Şimdi tüm ürünleri çekelim ve eşleşenleri listeleyelim
                    db.collection("products")
                            .get()
                            .addOnSuccessListener(productsSnapshot -> {
                                favoriteProducts.clear();
                                for (DocumentSnapshot productDoc : productsSnapshot) {
                                    Product product = productDoc.toObject(Product.class);
                                    if (product != null && favoriteNames.contains(product.getName())) {
                                        product.setId(productDoc.getId()); // ID ataması
                                        favoriteProducts.add(product);
                                    }
                                }
                                productAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FAVORITES", "Ürünler alınamadı: " + e.getMessage());
                                Toast.makeText(getContext(), "Ürünler alınamadı.", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e("FAVORITES", "Favoriler alınamadı: " + e.getMessage());
                    Toast.makeText(getContext(), "Favoriler alınamadı.", Toast.LENGTH_SHORT).show();
                });
    }
}
