package com.tudio.scregproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts, recyclerViewCategories;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Product> productList;
    private List<String> categoryList;
    private EditText editTextSearch;
    private TextView textViewUserName;
    private ImageView imageViewProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);

        // Firestore'dan ad soyad çek
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String firstName = doc.getString("firstName");
                        String lastName = doc.getString("lastName");

                        if (firstName != null && lastName != null) {
                            textViewUserName.setText(firstName + " " + lastName);
                        }
                    });
        }
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);

        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        productList = new ArrayList<>();
        categoryList = new ArrayList<>();

        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerViewProducts.setAdapter(productAdapter);

        categoryAdapter = new CategoryAdapter(categoryList, selectedCategory -> {
            if (selectedCategory.equals("Menü")) {
                loadAllProducts();
            } else {
                loadProductsByCategory(selectedCategory);
            }
        });
        recyclerViewCategories.setAdapter(categoryAdapter);

        loadCategories();
        loadAllProducts(); // Varsayılan olarak "Menü" = tüm ürünler
        editTextSearch = view.findViewById(R.id.editTextSearch);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // Profil resmine tıklanınca ayarlar sayfasına git
        imageViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileSettingsActivity.class);
            startActivity(intent);
        });

        return view;
    }
    private void filterProducts(String query) {
        if (query.isEmpty()) {
            loadAllProducts(); // Tüm ürünleri göster
            return;
        }

        FirebaseFirestore.getInstance().collection("products")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    productList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Product product = doc.toObject(Product.class);
                        if ((product.getName() != null && product.getName().toLowerCase().contains(query.toLowerCase())) ||
                                (product.getDescription() != null && product.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                            productList.add(product);
                        }

                    }
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Arama hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.add("Menü"); // Varsayılan

        FirebaseFirestore.getInstance().collection("category")
                .get()
                .addOnSuccessListener(query -> {
                    Log.d("CATEGORY", "Toplam kategori: " + query.size());
                    for (DocumentSnapshot doc : query) {
                        String category = doc.getString("name");
                        Log.d("CATEGORY", "Kategori bulundu: " + category);
                        if (category != null) categoryList.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("CATEGORY", "Hata: " + e.getMessage());
                });
    }


    private void loadAllProducts() {
        FirebaseFirestore.getInstance().collection("products")
                .get()
                .addOnSuccessListener(query -> {
                    productList.clear();
                    for (DocumentSnapshot doc : query) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId()); // 🔥 Belge ID’yi ürün nesnesine ekliyoruz
                            productList.add(p);
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("HOME_FRAGMENT", "Ürünler alınamadı: " + e.getMessage());
                });
    }


    private void loadProductsByCategory(String category) {
        FirebaseFirestore.getInstance().collection("products")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(query -> {
                    productList.clear();
                    for (DocumentSnapshot doc : query) {
                        Product p = doc.toObject(Product.class);
                        productList.add(p);
                    }
                    productAdapter.notifyDataSetChanged();
                });
    }
}
