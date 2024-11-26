package com.example.smokeapplicationapppro.ui.profiles;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.smokeapplicationapppro.databinding.FragmentProfilesBinding;
import com.example.smokeapplicationapppro.databinding.ItemEmergencyContactBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfilesFragment extends Fragment {


    private FragmentProfilesBinding binding;
    private ProfilesViewModel profilesViewModel;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private EmergencyContactAdapter contactAdapter;
    private List<EmergencyContact> contactsList;


    private static class EmergencyContact {
        String id;
        String name;
        String number;


        EmergencyContact(String id, String name, String number) {
            this.id = id;
            this.name = name;
            this.number = number;
        }
    }


    private class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder> {
        private final List<EmergencyContact> contacts;


        EmergencyContactAdapter(List<EmergencyContact> contacts) {
            this.contacts = contacts;
        }


        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemEmergencyContactBinding binding = ItemEmergencyContactBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ContactViewHolder(binding);
        }


        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            EmergencyContact contact = contacts.get(position);
            holder.bind(contact);
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }


        class ContactViewHolder extends RecyclerView.ViewHolder {
            private final ItemEmergencyContactBinding binding;
            private EmergencyContact contact;


            ContactViewHolder(ItemEmergencyContactBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }


            void bind(EmergencyContact contact) {
                this.contact = contact;


                // Enable editing for contact fields
                binding.editTextContactName.setEnabled(true);
                binding.editTextContactNumber.setEnabled(true);


                // Set the contact details (name and number)
                binding.editTextContactName.setText(contact.name);


                // If contact number is not null or empty, display it
                binding.editTextContactNumber.setText(contact.number);


                // Delete button listener
                binding.deleteButton.setOnClickListener(v -> deleteContact(contact));


                // Save button listener (for saving the contact)
                binding.saveButton.setOnClickListener(v -> {
                    String name = binding.editTextContactName.getText().toString().trim();
                    String number = binding.editTextContactNumber.getText().toString().trim();


                    if (name.isEmpty()) {
                        // If name is empty, show a toast
                        Toast.makeText(requireContext(), "Contact name is required", Toast.LENGTH_SHORT).show();
                    } else if (number.isEmpty() || number.length() < 10) {
                        // If number is empty or not valid, show a toast
                        Toast.makeText(requireContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                    } else {
                        // If both are valid, proceed to save the contact
                        EmergencyContact contactToSave = new EmergencyContact(contact.id, name, number);
                        saveContactToDatabase(contactToSave);
                    }
                });


                // Name change listener
                binding.editTextContactName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}


                    @Override
                    public void afterTextChanged(Editable s) {
                        contact.name = s.toString();
                    }
                });


                // Number change listener
                binding.editTextContactNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}


                    @Override
                    public void afterTextChanged(Editable s) {
                        String updatedPhone = s.toString().replaceAll("[^0-9]", ""); // Remove non-numeric characters
                        if (!updatedPhone.equals(contact.number)) {
                            contact.number = updatedPhone; // Update the contact's phone number
                        }
                    }
                });


            }
        }
    }








    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profilesViewModel = new ViewModelProvider(this).get(ProfilesViewModel.class);
        binding = FragmentProfilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            userId = currentUser.getUid();
            setupRecyclerView();
            fetchUserData();
            setupViews();
        }


        return root;
    }


    private void setupRecyclerView() {
        contactsList = new ArrayList<>();
        contactAdapter = new EmergencyContactAdapter(contactsList);
        binding.emergencyContactsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.emergencyContactsRecyclerView.setAdapter(contactAdapter);
    }


    private void fetchUserData() {
        mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);


                    binding.editTextName.setText(name);
                    binding.editTextUsername.setText(username);
                    binding.editTextEmail.setText(email);


                    binding.editTextName.setEnabled(false);
                    binding.editTextUsername.setEnabled(false);
                    binding.editTextEmail.setEnabled(false);


                    contactsList.clear();
                    DataSnapshot contactsSnapshot = dataSnapshot.child("emergency_contacts");
                    if (contactsSnapshot.exists()) {
                        for (DataSnapshot contact : contactsSnapshot.getChildren()) {
                            String contactName = contact.child("name").getValue(String.class);
                            String contactNumber = contact.child("number").getValue(String.class);
                            String contactId = contact.getKey();
                            contactsList.add(new EmergencyContact(contactId, contactName, contactNumber));
                        }
                    } else {
                        contactsList.add(new EmergencyContact("", "", ""));
                    }


                    contactAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load profile: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupViews() {
        binding.infoButton.setOnClickListener(v -> showEmergencyContactInfo());
        binding.addContactButton.setOnClickListener(v -> addNewEmergencyContact());
    }


    private void showEmergencyContactInfo() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Emergency Contacts")
                .setMessage("Add multiple emergency contacts who will be contacted in case of emergencies. " +
                        "Please ensure these are reliable contacts who can be reached when needed.")
                .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void addNewEmergencyContact() {
        // Add a new empty contact to the RecyclerView
        EmergencyContact newContact = new EmergencyContact("", "", "");
        contactsList.add(newContact);
        contactAdapter.notifyItemInserted(contactsList.size() - 1);


        // Generate a unique ID for the new contact but do NOT save it yet
        String contactId = mDatabase.child("users").child(userId).child("emergency_contacts").push().getKey();
        if (contactId != null) {
            newContact.id = contactId;
        }


        // Let the user edit the contact in the RecyclerView before validation
    }




    private void deleteContact(EmergencyContact contact) {
        if (!contact.id.isEmpty()) {
            mDatabase.child("users").child(userId)
                    .child("emergency_contacts")
                    .child(contact.id)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        contactsList.remove(contact);
                        contactAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(),
                            "Failed to delete contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }


    private void saveContactToDatabase(EmergencyContact contact) {
        if (!validateContact(contact)) return;


        Map<String, Object> contactValues = new HashMap<>();
        contactValues.put("name", contact.name);
        contactValues.put("number", contact.number);


        mDatabase.child("users").child(userId)
                .child("emergency_contacts")
                .child(contact.id)
                .setValue(contactValues)
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(),
                        "Contact saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(),
                        "Failed to save contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private boolean validateContact(EmergencyContact contact) {
        if (contact.name.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Contact name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (contact.number.trim().isEmpty() || contact.number.length() < 10) {
            Toast.makeText(requireContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
