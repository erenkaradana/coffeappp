package com.tudio.scregproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private Button btnConfirmOrder;
    private TextView textViewEmptyCart;
    private LinearLayout priceContainer;
    private DatabaseReference cartRef;
    private FirebaseFirestore firestore;
    private String userId;
    private TextView textViewTotalPrice;
    private ImageButton buttonBack;

    public CartFragment() {}
    double total = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        buttonBack = view.findViewById(R.id.buttonBack);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        btnConfirmOrder = view.findViewById(R.id.btnConfirmOrder);
        textViewEmptyCart = view.findViewById(R.id.textViewEmptyCart);
        priceContainer = view.findViewById(R.id.priceContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItems);
        recyclerView.setAdapter(cartAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);
        firestore = FirebaseFirestore.getInstance();

        loadCartItems();
        setupConfirmButton();
        buttonBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear(); // listeyi sıfırla
                double total = 0;  // toplamı sıfırla

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    CartItem item = itemSnap.getValue(CartItem.class);
                    if (item != null) {
                        item.setId(itemSnap.getKey());
                        cartItems.add(item);
                        total += item.getPrice() * item.getQuantity(); // doğru hesap
                    }
                }

                cartAdapter.notifyDataSetChanged();
                if (cartItems.isEmpty()) {
                    textViewEmptyCart.setVisibility(View.VISIBLE);
                    btnConfirmOrder.setVisibility(View.GONE);
                    priceContainer.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    textViewEmptyCart.setVisibility(View.GONE);
                    btnConfirmOrder.setVisibility(View.VISIBLE);
                    priceContainer.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                textViewTotalPrice.setText(String.format(Locale.getDefault(), "%.2f", total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CART_FRAGMENT", "Veri çekme hatası: " + error.getMessage());
            }
        });
    }


    private void setupConfirmButton() {
        btnConfirmOrder.setOnClickListener(v -> {
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(getContext(), "Sepet boş", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Map<String, Object>> orderItems = new ArrayList<>();
                    double totalPrice = 0;

                    for (DataSnapshot itemSnap : snapshot.getChildren()) {
                        CartItem item = itemSnap.getValue(CartItem.class);
                        if (item == null) continue;

                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("name", item.getName());
                        itemMap.put("price", item.getPrice());
                        itemMap.put("quantity", item.getQuantity());
                        itemMap.put("size", item.getSize());
                        itemMap.put("milk", item.getMilk());
                        totalPrice += item.getPrice() * item.getQuantity();
                        orderItems.add(itemMap);
                    }

                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("userId", userId);
                    orderData.put("items", orderItems);
                    orderData.put("totalPrice", totalPrice);

                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    orderData.put("timestamp", time);

                    firestore.collection("orders")
                            .document(date)
                            .collection("userOrders")
                            .add(orderData)
                            .addOnSuccessListener(docRef -> {
                                cartRef.removeValue(); // Sepet temizlenir
                                Toast.makeText(getContext(), "Sipariş onaylandı", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CART_FRAGMENT", "Sipariş kaydedilemedi: " + e.getMessage());
                                Toast.makeText(getContext(), "Sipariş hatası", Toast.LENGTH_SHORT).show();
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("CART_FRAGMENT", "Sepet erişim hatası: " + error.getMessage());
                }
            });
        });
    }
}
