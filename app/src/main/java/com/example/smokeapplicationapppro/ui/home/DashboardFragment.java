package com.example.smokeapplicationapppro.ui.home;


import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.smokeapplicationapppro.R;
import com.example.smokeapplicationapppro.databinding.FragmentDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.telephony.SmsManager;
import android.widget.Switch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


// Inside your DashboardFragment


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private GestureDetector gestureDetector;
    private TextView tvSafetyStatus, tvSafetyMessage, tvSafetyLevel, tvIs;
    private ImageView ivHouseGif;
    private ConstraintLayout rootLayout;
    private DatabaseReference databaseReference;
    private TextView lastUpdateTextView;
    private DatabaseReference contactsReference;
    private Switch sosSwitch;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), binding.textDashboard::setText);


        // Initialize GestureDetector
        gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            private static final float SWIPE_THRESHOLD = 100;
            private static final float SWIPE_VELOCITY_THRESHOLD = 100;


            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }


            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    float diffX = e1.getX() - e2.getX();
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX < 0) { // Right swipe
                            navigateToHome();
                            return true;
                        }
                    }
                } catch (Exception e) {
                    Log.e("DashboardFragment", "Error processing gesture", e);
                }
                return false;
            }
        });


        // Set touch listener
        root.setOnTouchListener((v, event) -> {
            v.performClick();
            return gestureDetector.onTouchEvent(event);
        });


        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("lastUpdate");


        // Initialize TextView
        lastUpdateTextView = binding.textLastUpdate;


        // Set up Firebase listener
        setupFirebaseListener();
        contactsReference = FirebaseDatabase.getInstance().getReference("contacts");


        // Initialize the Switch
        sosSwitch = binding.getRoot().findViewById(R.id.sosSwitchId);


        // Set up the Switch listener to send SOS when toggled
        sosSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sendSosMessage();
            }
        });


        return root;
    }


    private void sendSosMessage() {
        contactsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    String phoneNumber = contactSnapshot.child("phone").getValue(String.class);
                    if (phoneNumber != null) {
                        sendMessage(phoneNumber, "SOS! Emergency! Please help!");
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DashboardFragment", "Failed to read contacts: " + databaseError.getMessage());
            }
        });
    }


    private void sendMessage(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("DashboardFragment", "Message sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e("DashboardFragment", "Failed to send message: " + e.getMessage());
        }
    }
    private void setupFirebaseListener() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long timestamp = dataSnapshot.getValue(Long.class);
                    if (timestamp != null) {
                        updateLastUpdateText(timestamp);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardFragment", "Firebase Database error: " + error.getMessage());
            }
        });
    }


    private void updateLastUpdateText(long timestamp) {
        try {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(date);
            lastUpdateTextView.setText(formattedDate);
        } catch (Exception e) {
            Log.e("DashboardFragment", "Error formatting date: " + e.getMessage());
            lastUpdateTextView.setText("Date not available");
        }
    }


    private void updateUI(Integer mq2Value, String buzzerStatus, String ledStatus) {
        // Existing MQ2 value update
        tvSafetyLevel.setText(String.valueOf(mq2Value));


        // Get reference to the buzzer card view and its status text
        CardView cardBuzzer = requireView().findViewById(R.id.card_buzzer);
        TextView buzzerStatusText = (TextView) ((LinearLayout) cardBuzzer.getChildAt(0)).getChildAt(1);


        // Update buzzer status UI
        if ("online".equalsIgnoreCase(buzzerStatus)) {
            // Set online state
            cardBuzzer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
            buzzerStatusText.setText("ONLINE");
        } else {
            // Set offline state
            cardBuzzer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey));
            buzzerStatusText.setText("OFFLINE");
        }


        // Existing LED status logic
        if ("green".equals(ledStatus)) {
            tvIs.setText("is");
            tvSafetyStatus.setText("Safe");
            tvSafetyStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            ivHouseGif.setImageResource(R.drawable.error_icon);
            tvSafetyMessage.setText("Everything is okay. No smoke detected.");
        } else if ("yellow".equals(ledStatus)) {
            tvIs.setText("may be in");
            tvSafetyStatus.setText("Danger");
            tvSafetyStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow));
            ivHouseGif.setImageResource(R.drawable.error_icon);
            tvSafetyMessage.setText("Smoke detected! Stay alert.");
        } else if ("red".equals(ledStatus)) {
            tvIs.setText("is in");
            tvSafetyStatus.setText("Danger");
            tvSafetyStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            ivHouseGif.setImageResource(R.drawable.error_icon);
            tvSafetyMessage.setText("Danger! Immediate action required!");
        }


        // Log buzzer status
        Log.d("HomeFragment", "Buzzer Status: " + buzzerStatus);
    }


    private void navigateToHome() {
        try {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_dashboard_to_navigation_home);
        } catch (Exception e) {
            Log.e("DashboardFragment", "Navigation error", e);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

