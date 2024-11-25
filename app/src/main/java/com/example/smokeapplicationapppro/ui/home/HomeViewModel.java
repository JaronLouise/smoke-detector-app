package com.example.smokeapplicationapppro.ui.home;


import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.smokeapplicationapppro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeViewModel extends ViewModel {
    private final MutableLiveData<Integer> safetyLevel;
    private final MutableLiveData<String> safetyMessage;
    private final MutableLiveData<Integer> safetyColor;
    private final MutableLiveData<Integer> gifResource;
    private final MutableLiveData<String> safetyStatus;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    public HomeViewModel() {
        // Initialize LiveData with default values to prevent null issues
        safetyLevel = new MutableLiveData<>(0);  // Default value
        safetyMessage = new MutableLiveData<>("Initializing...");  // Default message
        safetyColor = new MutableLiveData<>(R.color.green);  // Default color
        gifResource = new MutableLiveData<>(R.drawable.error_icon);  // Default image
        safetyStatus = new MutableLiveData<>("Loading...");  // Default safety status
        initializeFirebase();
    }

    private void initializeFirebase() {
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://smokedetectiondatabase-default-rtdb.firebaseio.com/")
                .child("sensor");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer mq2Value = dataSnapshot.child("mq2Value").getValue(Integer.class);
                    if (mq2Value != null) {
                        updateSafetyState(mq2Value);
                    } else {
                        // Handle case when mq2Value is null
                        safetyMessage.setValue("Invalid sensor value.");
                        safetyColor.setValue(R.color.red);  // Fallback color
                        gifResource.setValue(R.drawable.error_icon);  // Fallback image
                    }
                } else {
                    // Handle case when sensor data is not available
                    safetyMessage.setValue("No data available.");
                    safetyColor.setValue(R.color.red);  // Fallback color
                    gifResource.setValue(R.drawable.error_icon);  // Fallback image
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("HomeViewModel", "Firebase read failed: " + error.getMessage());
                // Update LiveData to inform UI about the error
                safetyMessage.setValue("Error fetching data");
                safetyColor.setValue(R.color.red);  // Fallback color
                gifResource.setValue(R.drawable.error_icon);  // Fallback image
            }
        };

        // Start listening for Firebase database changes
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void updateSafetyState(int mq2Value) {
        safetyLevel.setValue(mq2Value);

        if (mq2Value <= 100) {
            safetyStatus.setValue("is Safe");
            safetyColor.setValue(R.color.green);  // Safe green color
            gifResource.setValue(R.drawable.error_icon);  // Safe icon or gif
            safetyMessage.setValue("Good job! Keep up the good work!");
        } else if (mq2Value <= 300) {
            safetyStatus.setValue("may be in Danger");
            safetyColor.setValue(R.color.yellow);  // Warning yellow color
            gifResource.setValue(R.drawable.error_icon);  // Warning icon or gif
            safetyMessage.setValue("Smoke Detected, stay Vigilant.");
        } else {
            safetyStatus.setValue("be in Danger");
            safetyColor.setValue(R.color.red);  // Danger red color
            gifResource.setValue(R.drawable.error_icon);  // Danger icon or gif
            safetyMessage.setValue("Danger! Help is on the way.");
        }
    }

    // Getters for LiveData
    public LiveData<String> getSafetyStatus() {
        return safetyStatus;
    }

    public LiveData<Integer> getSafetyLevel() {
        return safetyLevel;
    }

    public LiveData<String> getSafetyMessage() {
        return safetyMessage;
    }

    public LiveData<Integer> getSafetyColor() {
        return safetyColor;
    }

    public LiveData<Integer> getGifResource() {
        return gifResource;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove Firebase listener when ViewModel is cleared
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}
