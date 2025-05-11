package com.tudio.scregproject;


import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailFragment extends Fragment {

    private ImageView imageProduct;
    private TextView textName, textDesc, textPrice, textRating;
    private ImageButton buttonBack, buttonFavorite;
    private Button buttonBuyNow;
    private LinearLayout layoutSizeButtons, layoutMilkButtons;
    private boolean isFavorite = false;

    private String selectedSize = "M";
    private String selectedMilk = "Tam Yağlı";

    private Product product;
    private double basePrice = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        checkIfFavorite();
        imageProduct = view.findViewById(R.id.imageProductDetail);
        textName = view.findViewById(R.id.textProductName);
        textDesc = view.findViewById(R.id.textProductDescription);
        textPrice = view.findViewById(R.id.textProductPrice);
        textRating = view.findViewById(R.id.textProductRating);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonFavorite = view.findViewById(R.id.buttonFavorite);
        buttonBuyNow = view.findViewById(R.id.buttonBuyNow);
        layoutSizeButtons = view.findViewById(R.id.layoutSizeButtons);
        layoutMilkButtons = view.findViewById(R.id.layoutMilkButtons);

        if (getArguments() != null) {
            product = getArguments().getParcelable("selectedProduct");
            if (product != null) {
                textName.setText(product.getName());
                textRating.setText(String.valueOf(product.getRating()));


                basePrice = product.getPrice();
                updatePrice();

                FirebaseFirestore.getInstance().collection("products")
                        .document(product.getId())
                        .get().addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String longDesc = doc.getString("descriptionLong");
                                if (longDesc != null && !longDesc.isEmpty()) {
                                    textDesc.setText(longDesc);
                                } else {
                                    textDesc.setText("Açıklama bulunamadı.");
                                }

                                // 🔥 Yeni: bigImageUrl'i kullan
                                String bigImageUrl = doc.getString("bigImageUrl");
                                if (bigImageUrl != null && !bigImageUrl.isEmpty()) {
                                    Glide.with(requireContext()).load(bigImageUrl).into(imageProduct);
                                }
                            }
                        });
                checkIfFavorite();
            }
        }

        setupSizeButtons();
        setupMilkButtons();

        buttonBuyNow.setOnClickListener(v -> addToCart());

        buttonBack.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        buttonFavorite.setOnClickListener(v -> {
            if (isFavorite) {
                removeFromFavorites();
            } else {
                addToFavorites();
            }
        });


        return view;
    }

    private void setupSizeButtons() {
        String[] sizes = {"S", "M", "L"};
        for (String size : sizes) {
            Button btn = new Button(requireContext());
            btn.setText(size);
            btn.setBackgroundResource(R.drawable.button_unselected);
            btn.setTextColor(getResources().getColor(android.R.color.black));
            btn.setElevation(0f);
            btn.setStateListAnimator(null);
            Typeface tf = ResourcesCompat.getFont(requireContext(), R.font.gr);
            btn.setTypeface(tf);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(8, 2, 8, 2);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                selectedSize = size;
                highlightSelected(layoutSizeButtons, size);
                updatePrice();
            });

            layoutSizeButtons.addView(btn);
        }
        highlightSelected(layoutSizeButtons, selectedSize);
    }

    private void setupMilkButtons() {
        String[] milks = {"Tam Yağlı", "Yağsız", "Soya", "Badem"};
        for (String milk : milks) {
            Button btn = new Button(requireContext());
            btn.setText(milk);
            btn.setBackgroundResource(R.drawable.button_unselected);
            btn.setTextColor(getResources().getColor(android.R.color.black));
            btn.setElevation(0f);
            btn.setStateListAnimator(null);
            Typeface tf = ResourcesCompat.getFont(requireContext(), R.font.gr);
            btn.setTypeface(tf);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(8, 2, 8, 2);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                selectedMilk = milk;
                highlightSelected(layoutMilkButtons, milk);
                updatePrice();
            });

            layoutMilkButtons.addView(btn);
        }
        highlightSelected(layoutMilkButtons, selectedMilk);
    }

    private void highlightSelected(LinearLayout layout, String selectedText) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                if (btn.getText().toString().equals(selectedText)) {
                    btn.setBackgroundResource(R.drawable.button_selected);
                } else {
                    btn.setBackgroundResource(R.drawable.button_unselected);
                }
            }
        }
    }

    private void updatePrice() {
        double totalPrice = calculateFinalPrice();
        textPrice.setText("\u20BA " + String.format("%.2f", totalPrice));
    }


    private void addToCart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || product == null) return;

        String itemKey = product.getName() + "_" + selectedSize + "_" + selectedMilk;
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts")
                .child(user.getUid())
                .child(itemKey);

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("name", product.getName());
        cartItem.put("description", product.getDescription());
        cartItem.put("imageUrl", product.getImageUrl());
        cartItem.put("price", calculateFinalPrice()); // sabit fiyat
        cartItem.put("rating", product.getRating());
        cartItem.put("category", product.getCategory());
        cartItem.put("quantity", 1);
        cartItem.put("size", selectedSize);
        cartItem.put("milk", selectedMilk);

        cartRef.setValue(cartItem).addOnSuccessListener(unused ->
                Toast.makeText(getContext(), "Sepete eklendi", Toast.LENGTH_SHORT).show());
    }
    private double calculateFinalPrice() {
        double totalPrice = basePrice;

        if (selectedSize.equals("M")) {
            totalPrice += basePrice * 0.10;
        } else if (selectedSize.equals("L")) {
            totalPrice += basePrice * 0.20;
        }

        if (selectedMilk.equals("Soya") || selectedMilk.equals("Badem")) {
            totalPrice += basePrice * 0.10;
        }

        return totalPrice;
    }

    private void addToFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || product == null) return;

        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("name", product.getName());

        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .collection("userFavorites")
                .document(product.getId())
                .set(favoriteData)
                .addOnSuccessListener(unused -> {
                    isFavorite = true;
                    buttonFavorite.setImageResource(R.drawable.ic_favorite_filled); // Turuncu kalp
                    Toast.makeText(getContext(), "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                });
    }
    private void removeFromFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || product == null) return;

        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .collection("userFavorites")
                .document(product.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    isFavorite = false;
                    buttonFavorite.setImageResource(R.drawable.ic_favorite_border); // Boş kalp
                    Toast.makeText(getContext(), "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkIfFavorite() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || product == null) return;

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUid())
                .collection("userFavorites")
                .document(product.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isFavorite = true;
                        buttonFavorite.setImageResource(R.drawable.ic_favorite_filled); // Turuncu kalp ikonu
                    } else {
                        isFavorite = false;
                        buttonFavorite.setImageResource(R.drawable.ic_favorite_border); // Boş kalp ikonu
                    }
                });
    }

}