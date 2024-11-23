package com.example.smokeapplicationapppro.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smokeapplicationapppro.R;
import com.example.smokeapplicationapppro.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private GestureDetector gestureDetector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), binding.textDashboard::setText);

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

        return root;
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