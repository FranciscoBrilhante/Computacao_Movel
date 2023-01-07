package com.example.market.ui.fragments.main;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentCreateProductBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.interfaces.ProductImageInterface;
import com.example.market.marketDatabase.Category;
import com.example.market.marketDatabase.Image;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.adapter.CreateSpinnerAdapter;
import com.example.market.ui.components.adapter.ImageListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CreateProductFragment extends Fragment implements View.OnClickListener, ProductImageInterface, HTTTPCallback {
    private FragmentCreateProductBinding binding;
    private MarketViewModel viewModel;
    private ImageListAdapter imageListAdapter;
    private RecyclerView imageRecyclerView;
    private ArrayList<Image> productImages;
    private CreateSpinnerAdapter categorySpinnerAdapter;

    private static final int PICK_IMAGE_REQUEST = 345345;
    ActivityResultLauncher<String> requestPermissionLauncher;

    private int fragId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentCreateProductBinding.inflate(inflater, container, false);


        categorySpinnerAdapter = new CreateSpinnerAdapter(getContext(), new ArrayList<>());
        binding.categorySpinner.setAdapter(categorySpinnerAdapter);

        imageListAdapter = new ImageListAdapter(new ImageListAdapter.ImageDiff(), this);
        imageRecyclerView = binding.imageRecyclerView;
        imageRecyclerView.setAdapter(imageListAdapter);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        productImages = new ArrayList<>();
        imageListAdapter.submitList(productImages);

        viewModel.getAllCategories().observe(requireActivity(), categories -> {
            categorySpinnerAdapter.clear();
            categorySpinnerAdapter.addAll(categories);
            categorySpinnerAdapter.notifyDataSetChanged();
        });

        binding.uploadPhotoButton.setOnClickListener(this);
        binding.backButton.setOnClickListener(this);
        binding.publishButton.setOnClickListener(this);
        binding.uploadButton.setOnClickListener(this);

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if(isGranted){
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Open Gallery"), PICK_IMAGE_REQUEST);
                    }
                });

        if ((Boolean) viewModel.getStoredCredentials().get("is_admin")) fragId = R.id.nav_host_fragment_activity_admin;
        else fragId = R.id.nav_host_fragment_activity_main;

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backButton) {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
            NavController navController = navHostFragment.getNavController();
            navController.navigateUp();
        }
        if (view == binding.uploadPhotoButton) {
            if(productImages.size()>=5){
                Toast.makeText(getActivity().getApplicationContext(), R.string.cannot_upload_more_photos, Toast.LENGTH_SHORT).show();
                return;
            }
            verifyStoragePermissions(getActivity());
        }
        if (view == binding.uploadButton || view == binding.publishButton) {
            uploadProduct();
        }
    }

    @Override
    public void onComplete(JSONObject data) {
        try {
            String url1 = "/product/add";
            String url2 = "/product/myproducts";
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    viewModel.sendRequest("/product/myproducts", "GET", null, null, false, false, true, this);
                    if (productImages.size() > 0) {
                        int productID = data.getInt("id");
                        viewModel.sendProductPhotos(productID, productImages);
                    }
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(fragId);
                    NavController navController = navHostFragment.getNavController();
                    navController.navigateUp();
                }
            }else if (endpoint.equals(url2)) {
                if (code == 200) {
                    ArrayList<Product> products = viewModel.productsFromJSONObject(data);
                    viewModel.addProducts(products);
                }
            }
        } catch (JSONException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickToRemove(Image image) {
        int index = productImages.indexOf(image);
        productImages.remove(image);
        imageListAdapter.notifyItemRemoved(index);
    }

    private void verifyStoragePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        else{
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
                productImages.add(new Image(part_image, bitmap));
                imageListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void uploadProduct(){
        String title = binding.titleInput.getText().toString();
        String description = binding.descriptionInput.getText().toString();
        String price = binding.priceInput.getText().toString();

        if (title.length() <= 0 || description.length() <= 0 || price.length() <= 0) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.empty_product_info_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        Double priceDouble = 0.0;
        try {
            priceDouble = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.price_invalid_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("title", title);
        params.put("description", description);
        params.put("price", priceDouble);
        Category cat = (Category) binding.categorySpinner.getSelectedItem();
        params.put("category", cat.getId());

        viewModel.sendRequest("/product/add", "POST", null, params, true, false, true, this);
    }

}
