package com.example.market.ui.fragments.main;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentProfileDetailsBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Image;
import com.example.market.ui.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ProfileDetailsFragment extends Fragment implements View.OnClickListener, HTTTPCallback, LocationListener {


    private FragmentProfileDetailsBinding binding;
    private MarketViewModel viewModel;

    private static final int PICK_IMAGE_REQUEST = 345345;
    ActivityResultLauncher<String> editPhotoRequestPermissionLauncher;

    private int fragId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentProfileDetailsBinding.inflate(inflater, container, false);

        binding.logoutButton.setOnClickListener(this);
        binding.deleteAccountButton.setOnClickListener(this);
        binding.updateLocationButton.setOnClickListener(this);
        binding.profilePhoto.setOnClickListener(this);
        binding.img.setOnClickListener(this);

        binding.loadingLocation.setVisibility(View.GONE);
        binding.closeLoadingLocation.setOnClickListener(this);
        binding.locationError.setVisibility(View.GONE);
        binding.closeErrorLocation.setOnClickListener(this);

        editPhotoRequestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Open Gallery"), PICK_IMAGE_REQUEST);
                    }
                });

        viewModel.sendRequest("/profile/personalinfo", "GET", null, null, false, false, true, this);

        if ((Boolean) viewModel.getStoredCredentials().get("is_admin")) fragId = R.id.nav_host_fragment_activity_admin;
        else fragId = R.id.nav_host_fragment_activity_main;

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.logoutButton) {
            viewModel.clearCookies();
            viewModel.removeStoredCredentials();
            Intent myIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(myIntent);
        }
        if (view == binding.deleteAccountButton) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), 0);
            alert.setTitle(R.string.confirm_deletion_title);
            alert.setMessage(R.string.confirm_deletion_message);
            alert.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    viewModel.sendRequest("/profile/delete", "GET", null, null, false, false, true, ProfileDetailsFragment.this::afterDelete);
                }
            });

            alert.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.show();
        }
        if (view == binding.updateLocationButton) {
            requestLocation();
        }
        if (view == binding.img || view == binding.profilePhoto) {
            verifyStoragePermissions(getActivity());
        }
        if(view==binding.closeLoadingLocation){
            binding.loadingLocation.setVisibility(View.GONE);
        }
        if(view==binding.closeErrorLocation){
            binding.locationError.setVisibility(View.GONE);
        }
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/profile/personalinfo";
        String url2 = "/profile/setlocation";
        String url3 = "/profile/setphoto";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    populateProfileInfo(data);
                } else {
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
                    NavController navController = navHostFragment.getNavController();
                    navController.navigateUp();
                }
            }
            if (endpoint.equals(url2) || endpoint.equals(url3)) {
                if (code == 200) {
                    viewModel.sendRequest("/profile/personalinfo", "GET", null, null, false, false, true, this);
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void populateProfileInfo(JSONObject data) throws JSONException, NullPointerException {
        String username = data.getString("username");
        String email = data.getString("email");
        String photoURL = data.getString("photo");
        String city = data.getString("location");

        if (city.equals("null")) {
            city = getActivity().getResources().getString(R.string.location_undefined);
        }

        binding.usernameInput.setText(username);
        binding.emailInput.setText(email);
        binding.locationInput.setText(city);

        if (photoURL.equals("null")) {
            binding.profilePhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
        } else {
            String fullURL = "https://" + BuildConfig.API_ADDRESS + photoURL;
            Glide.with(getActivity().getApplicationContext())
                    .load(fullURL)
                    .override(1000, 1000) //give resize dimension, you could calculate those
                    .centerCrop() // scale to fill the ImageView
                    .error(R.drawable.placeholder_avatar)
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(binding.profilePhoto);
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);
        CancellationSignal cancellationSignal = new CancellationSignal();
        locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, cancellationSignal, getActivity().getApplication().getMainExecutor(), new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                if (location != null) {
                    System.out.println(location.getLatitude());
                    System.out.println(location.getLongitude());
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("cityX", location.getLongitude());
                    params.put("cityY", location.getLatitude());
                    viewModel.sendRequest("/profile/setlocation", "POST", null, params, true, false, true, ProfileDetailsFragment.this::onComplete);
                } else {
                    System.out.println("Location unavailable");
                    binding.locationError.setVisibility(View.VISIBLE);
                }
                binding.loadingLocation.setVisibility(View.GONE);
            }
        });
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissionLauncher.launch(permissions);
        } else {
            binding.loadingLocation.setVisibility(View.VISIBLE);
            updateLocation();
        }
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                boolean hasPermission = true;
                for (boolean value : isGranted.values()) {
                    if (!value) {
                        hasPermission = false;
                    }
                }
                if (hasPermission) {
                    updateLocation();
                    binding.loadingLocation.setVisibility(View.VISIBLE);
                }
            });

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    public void afterDelete(JSONObject data) {
        try {
            String url1 = "/profile/delete";
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    viewModel.clearCookies();
                    viewModel.removeStoredCredentials();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.successfull_deletion_message, Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(myIntent);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void verifyStoragePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            editPhotoRequestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Open Gallery"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                Uri mImageUri = data.getData();
                String[] imageProjection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(mImageUri, imageProjection, null, null, null);
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageProjection[0]);
                String part_image = cursor.getString(indexImage);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), mImageUri);
                    System.out.println(part_image);
                    System.out.println(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cursor.close();
                try {
                    viewModel.updateProfilePic(new Image(part_image, bitmap), this);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}