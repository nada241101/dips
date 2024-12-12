package com.android.foodorderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.foodorderapp.adapters.PlaceYourOrderAdapter;
import com.android.foodorderapp.model.Menu;
import com.android.foodorderapp.model.RestaurantModel;
import com.google.android.material.textfield.TextInputLayout;

public class PlaceYourOrderActivity extends AppCompatActivity {

    private EditText inputName, inputAddress, inputCardNumber, inputCardExpiry, inputCardPin;
    private TextInputLayout Addresslayout;
    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount, tvDeliveryChargeAmount, tvDeliveryCharge, tvTotalAmount, buttonPlaceYourOrder;
    private Button btnPickup, btnDelivery;
    private boolean isDeliveryOn;
    private PlaceYourOrderAdapter placeYourOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_your_order);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);

        inputName = findViewById(R.id.inputName);
        inputAddress = findViewById(R.id.inputAddressxml);
        Addresslayout = findViewById(R.id.Addresslayout);
        inputCardNumber = findViewById(R.id.inputCardNumber);
        inputCardExpiry = findViewById(R.id.inputCardExpiry);
        inputCardPin = findViewById(R.id.inputCardPin);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        buttonPlaceYourOrder = findViewById(R.id.buttonPlaceYourOrder);
        btnPickup = findViewById(R.id.btnPickup);
        btnDelivery = findViewById(R.id.btnDelivery);

        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        buttonPlaceYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaceOrderButtonClick(restaurantModel);
            }
        });

        btnPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeliveryOption(false);
                calculateTotalAmount(restaurantModel);
            }
        });

        btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeliveryOption(true);
                calculateTotalAmount(restaurantModel);
            }
        });

        // Default selection
        setDeliveryOption(false);

        initRecyclerView(restaurantModel);
        calculateTotalAmount(restaurantModel);
    }

    private void setDeliveryOption(boolean delivery) {
        isDeliveryOn = delivery;
        if (delivery) {
            btnDelivery.setBackgroundColor(getResources().getColor(R.color.blue));
            btnPickup.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            Addresslayout.setVisibility(View.VISIBLE);
            tvDeliveryChargeAmount.setVisibility(View.VISIBLE);
            tvDeliveryCharge.setVisibility(View.VISIBLE);
        } else {
            btnPickup.setBackgroundColor(getResources().getColor(R.color.blue));
            btnDelivery.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            Addresslayout.setVisibility(View.GONE);
            tvDeliveryChargeAmount.setVisibility(View.GONE);
            tvDeliveryCharge.setVisibility(View.GONE);
        }
    }

    private void calculateTotalAmount(RestaurantModel restaurantModel) {
        float subTotalAmount = 0f;

        for(Menu m : restaurantModel.getMenus()) {
            subTotalAmount += m.getPrice() * m.getTotalInCart();
        }

        tvSubtotalAmount.setText("L.E " + String.format("%.2f", subTotalAmount));
        if (isDeliveryOn) {
            tvDeliveryChargeAmount.setText("L.E " + String.format("%.2f", restaurantModel.getDelivery_charge()));
            subTotalAmount += restaurantModel.getDelivery_charge();
        }
        tvTotalAmount.setText("L.E " + String.format("%.2f", subTotalAmount));
    }

    private void onPlaceOrderButtonClick(RestaurantModel restaurantModel) {
        if (TextUtils.isEmpty(inputName.getText().toString())) {
            inputName.setError("Please enter name ");
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        } else if (isDeliveryOn && TextUtils.isEmpty(inputAddress.getText().toString())) {
            inputAddress.setError("Please enter address ");
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(inputCardNumber.getText().toString())) {
            inputCardNumber.setError("Please enter card number ");
            Toast.makeText(this, "Please enter card number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(inputCardExpiry.getText().toString())) {
            inputCardExpiry.setError("Please enter card expiry ");
            Toast.makeText(this, "Please enter card expiry", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(inputCardPin.getText().toString())) {
            inputCardPin.setError("Please enter card pin/cvv ");
            Toast.makeText(this, "Please enter card pin/cvv", Toast.LENGTH_SHORT).show();
            return;
        }


        String name = inputName.getText().toString();
        String address = inputAddress.getText().toString();
        String cardNumber = inputCardNumber.getText().toString();
        String cardExpiry = inputCardExpiry.getText().toString();
        String cardPin = inputCardPin.getText().toString();
        float orderTotal = Float.parseFloat(tvTotalAmount.getText().toString().substring(4)); // Remove "Pkr " from start
        String deliveryMethod = isDeliveryOn ? "Delivery" : "Pickup";


        // Start success activity..
        Intent i = new Intent(PlaceYourOrderActivity.this, OrderSucceessActivity.class);
        i.putExtra("RestaurantModel", restaurantModel);
        startActivityForResult(i, 1000);
    }

    private void initRecyclerView(RestaurantModel restaurantModel) {
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1000) {
            setResult(Activity.RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                // do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
