package com.tudio.scregproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private String userId;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.textName.setText(item.getName());
        holder.textPrice.setText("₺ " + item.getPrice());
        holder.textQuantity.setText(String.valueOf(item.getQuantity()));
        String sizeMilkText = item.getSize() + " - " + item.getMilk();
        holder.textDetail.setText(sizeMilkText);

        Glide.with(context)
                .load(item.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(16))) // 24 → radius değeri (px)
                .into(holder.imageProduct);
        // ➕
        holder.btnPlus.setOnClickListener(v -> updateQuantity(item, item.getQuantity() + 1));

        // ➖
        holder.btnMinus.setOnClickListener(v -> {
            long q = item.getQuantity();
            if (q > 1) updateQuantity(item, q - 1);
            else deleteItem(item);
        });

        // 🗑️
        holder.btnDelete.setOnClickListener(v -> deleteItem(item));
    }

    private void updateQuantity(CartItem item, long newQty) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(userId)
                .child(item.getId());

        ref.child("quantity").setValue(newQty)
                .addOnSuccessListener(aVoid -> {
                    item.setQuantity(newQty);
                    notifyDataSetChanged();
                });
    }

    private void deleteItem(CartItem item) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(userId)
                .child(item.getId()); // 🔥 artık doğru node silinecek

        ref.removeValue();

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice, textQuantity;
        ImageView imageProduct;
        ImageButton btnPlus, btnMinus, btnDelete;
        TextView textDetail;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textViewCartName);
            textPrice = itemView.findViewById(R.id.textViewCartPrice);
            textQuantity = itemView.findViewById(R.id.textViewQuantity);
            imageProduct = itemView.findViewById(R.id.imageCartProduct);
            textDetail = itemView.findViewById(R.id.textViewCartNameDetail);
            btnPlus = itemView.findViewById(R.id.buttonPlus);
            btnMinus = itemView.findViewById(R.id.buttonMinus);
            btnDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
