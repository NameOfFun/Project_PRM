package com.example.prm392_labbooking.presentation.billing;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_labbooking.R;
import com.example.prm392_labbooking.data.db.DatabaseHelper;
import com.example.prm392_labbooking.data.local.BookingHistoryStorage;
import com.example.prm392_labbooking.domain.model.BillingAdapter;
import com.example.prm392_labbooking.domain.model.CartItem;
import com.example.prm392_labbooking.domain.model.Facility;
import com.example.prm392_labbooking.domain.model.Product;
import com.example.prm392_labbooking.domain.model.Slot;
import com.example.prm392_labbooking.domain.usecase.booking.SaveBookingUseCase;
import com.example.prm392_labbooking.navigation.NavigationManager;
import com.example.prm392_labbooking.presentation.MainActivity;
import com.example.prm392_labbooking.utils.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillingFragment extends Fragment {

    private RecyclerView rvBillingItems;
    private TextView tvSubtotalPrice, tvTax, tvTotalPrice;
    private EditText etCardholderName, etCardNumber, etExpiryDate, etCvv;
    private Button btnConfirmBooking;
    private ImageButton btnBack;

    private List<CartItem> cartItems = new ArrayList<>();
    private double totalPrice;

    private SaveBookingUseCase saveBookingUseCase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_billing, container, false);

        ((MainActivity) requireActivity()).hideBottomNavigation();

        saveBookingUseCase = new SaveBookingUseCase(new DatabaseHelper(requireContext()));

        initViews(view);
        loadCartItems();
        setupRecyclerView();
        calculateTotalPrice();

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
        btnBack.setOnClickListener(v -> navigateToCart());

        return view;
    }

    private void initViews(View view) {
        rvBillingItems = view.findViewById(R.id.rv_billing_items);
        tvSubtotalPrice = view.findViewById(R.id.tv_subtotal_price);
        tvTax = view.findViewById(R.id.tv_tax);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        etCardholderName = view.findViewById(R.id.et_cardholder_name);
        etCardNumber = view.findViewById(R.id.et_card_number);
        etExpiryDate = view.findViewById(R.id.et_expiry_date);
        etCvv = view.findViewById(R.id.et_cvv);
        btnConfirmBooking = view.findViewById(R.id.btn_confirm_booking);
        btnBack = view.findViewById(R.id.btn_back);
    }

    private void navigateToCart() {
        NavigationManager.showCart(requireActivity().getSupportFragmentManager());
    }

    private void loadCartItems() {
        Bundle args = getArguments();
        if (args != null) {
            String cartItemsJson = args.getString("cartItemsJson");
            if (cartItemsJson != null) {
                Type type = new TypeToken<List<CartItem>>(){}.getType();
                cartItems = new Gson().fromJson(cartItemsJson, type);
            }
        }
        if (cartItems == null) cartItems = new ArrayList<>();
    }

    private void setupRecyclerView() {
        rvBillingItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBillingItems.setAdapter(new BillingAdapter(cartItems));
    }

    @SuppressLint("DefaultLocale")
    private void calculateTotalPrice() {
        double subtotal = 0;
        for (CartItem item : cartItems) subtotal += item.getPrice();

        double tax = subtotal * 0.08;
        totalPrice = subtotal + tax;

        tvSubtotalPrice.setText(getString(R.string.subtotal_label, subtotal));
        tvTax.setText(getString(R.string.tax_label, tax));
        tvTotalPrice.setText(getString(R.string.total_label, totalPrice));
    }

    private void confirmBooking() {
        String name = etCardholderName.getText().toString().trim();
        String cardNumber = etCardNumber.getText().toString().trim();
        String expiry = etExpiryDate.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (!ValidationUtils.isValidCardholderName(name)) {
            etCardholderName.setError(getString(R.string.error_cardholder_name));
            return;
        }
        if (!ValidationUtils.isValidCardNumber(cardNumber)) {
            etCardNumber.setError(getString(R.string.error_card_number));
            return;
        }
        if (!ValidationUtils.isValidExpiryDate(expiry)) {
            etExpiryDate.setError(getString(R.string.error_expiry_date));
            return;
        }
        if (!ValidationUtils.isValidCvv(cvv)) {
            etCvv.setError(getString(R.string.error_cvv));
            return;
        }

        boolean success = saveBookingUseCase.execute(cartItems, totalPrice);

        if (success) {
            persistBookingsToHistory(cartItems);

            com.example.prm392_labbooking.presentation.cart.CartManager.getInstance(requireContext())
                    .clearCart();

            Toast.makeText(requireContext(), getString(R.string.booking_confirmed), Toast.LENGTH_SHORT).show();

            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.booking_failed), Toast.LENGTH_SHORT).show();
        }
    }


    // -----------------------------
    // SAVE TO HISTORY (phuocpm nhánh)
    // -----------------------------
    private void persistBookingsToHistory(List<CartItem> items) {
        if (items == null || items.isEmpty()) return;

        BookingHistoryStorage storage = BookingHistoryStorage.getInstance(requireContext());

        List<BookingHistoryStorage.BookingHistoryRecord> records = new ArrayList<>();

        for (CartItem item : items) {
            BookingHistoryStorage.BookingHistoryRecord record = mapCartItemToHistory(item);
            if (record != null) records.add(record);
        }

        if (!records.isEmpty()) storage.addRecords(records);
    }

    private BookingHistoryStorage.BookingHistoryRecord mapCartItemToHistory(CartItem item) {
        if (item == null) return null;

        String roomName = resolveProductName(item.getProduct());
        String dateText = formatBookingDate(item.getDate());
        String timeRange = buildTimeRange(item.getSlots());
        double price = item.getPrice();
        List<String> facilities = mapFacilities(item.getFacilities());
        int participants = item.getProduct() != null ? item.getProduct().getNumber() : 0;
        int durationHours = item.getSlots() != null ? item.getSlots().size() : 0;

        return new BookingHistoryStorage.BookingHistoryRecord(
                roomName,
                dateText,
                timeRange,
                price,
                facilities,
                participants,
                durationHours
        );
    }

    private String resolveProductName(Product product) {
        if (product == null || product.getName() == null)
            return getString(R.string.feedback_room_placeholder);

        String key = product.getName();
        int resId = getResources().getIdentifier(key, "string", requireContext().getPackageName());

        return resId != 0 ? getString(resId) : key;
    }

    private String formatBookingDate(Date date) {
        if (date == null) date = new Date();
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    }

    private String buildTimeRange(List<Slot> slots) {
        List<String> ranges = ValidationUtils.getMergedSlotDisplayList(slots);
        if (ranges == null || ranges.isEmpty()) return getString(R.string.feedback_time_placeholder);
        return android.text.TextUtils.join(", ", ranges);
    }

    private List<String> mapFacilities(List<Facility> facilities) {
        List<String> result = new ArrayList<>();
        if (facilities != null) {
            for (Facility f : facilities) {
                int resId = getFacilityLabelResId(f);
                result.add(resId != 0 ? getString(resId) : f.getCode());
            }
        }
        return result;
    }

    private int getFacilityLabelResId(Facility facility) {
        if (facility == null) return 0;
        switch (facility) {
            case WHITE_BOARD: return R.string.facility_white_board;
            case TV: return R.string.facility_tv;
            case MICROPHONE: return R.string.facility_microphone;
            case NETWORK: return R.string.facility_network;
            default: return 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).showBottomNavigation();
    }
}
