package soa.work.scheduler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soa.work.scheduler.models.Category;
import soa.work.scheduler.retrofit.ApiService;
import soa.work.scheduler.retrofit.RetrofitClient;
import soa.work.scheduler.utilities.PrefManager;

import static soa.work.scheduler.data.Constants.PRICE_OFFERS;
import static soa.work.scheduler.data.Constants.WORK_PRICE;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private List<Category> categories;
    private ItemCLickListener itemCLickListener;
    private Context mContext;
    private StorageReference storageRef;
    private ApiService apiService;
    private static final String TAG = "CategoryRecyclerViewAda";
    private FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    private int imageVersion;
    private PrefManager prefManager;
    private String[] priceList;

    public CategoryRecyclerViewAdapter(List<Category> categories, Context mContext) {
        this.categories = categories;
        this.mContext = mContext;
        storageRef = FirebaseStorage.getInstance().getReference();
        apiService = RetrofitClient.getApiService();
        prefManager = new PrefManager(mContext);
        priceList = new String[categories.size()];

        Task<Void> fetch = remoteConfig.fetch(0);
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                remoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            imageVersion = Integer.parseInt(remoteConfig.getString("images_version"));
                        }
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryTextView.setText(categories.get(position).getCategoryTitle());
        setImage(categories.get(position).getCategoryImageFileName(), holder.categoryImageView);


        String price = priceList[position];

        if (price == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference user = database.getReference(PRICE_OFFERS).child(categories.get(position).getCategoryTitle()).child(WORK_PRICE);
            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String price = dataSnapshot.getValue(String.class);
                    if (price == null) {
                        holder.priceTextView.setVisibility(View.GONE);
                    } else {
                        priceList[position] = price;
                        holder.priceTextView.setVisibility(View.VISIBLE);
                        holder.priceTextView.setText("Price starts at : \n₹ " + price);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            holder.priceTextView.setText("Price starts at : \n₹ " + price);
            holder.priceTextView.setVisibility(View.VISIBLE);
        }


//        if (categories.get(position).getPrice() == 0) {
//            holder.priceTextView.setVisibility(View.GONE);
//        } else
//            holder.priceTextView.setText("Price starts at : ₹ " + categories.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setItemClickListener(ItemCLickListener itemClickListener) {
        this.itemCLickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.category_imageView)
        ImageView categoryImageView;
        @BindView(R.id.category_textView)
        TextView categoryTextView;
        @BindView(R.id.price_textView)
        TextView priceTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemCLickListener != null) {
                itemCLickListener.onItemClick(categories.get(getAdapterPosition()));
            }
        }
    }

    public interface ItemCLickListener {
        void onItemClick(Category category);
    }

    private void setImage(String fileName, ImageView imageView) {
        if (!fileName.isEmpty()) {
            File file = new File(mContext.getFilesDir() + File.separator + fileName);
            if (!file.exists()) {
                //This file doesn't exists. Need to download and cache it
                downloadImage(fileName, imageView);
            } else {
                //Cached image is available.
                if (imageVersion > prefManager.getImagesVersion()) {
                    //New images available
                    //Download images again
                    downloadImage(fileName, imageView);
                } else {
                    //New images not available. Use cached images
                    setImageBitmap(fileName, imageView);
                }
            }
        } else {
            //File name is empty. So falling back to placeholder empty image
            imageView.setImageDrawable(null);
        }

    }

    private void downloadImage(String fileName, ImageView imageView) {
        storageRef.child(fileName).getDownloadUrl().addOnSuccessListener(uri -> {
            Call<ResponseBody> downloadImageCall = apiService.getImage(uri.toString());
            downloadImageCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (response.body() != null) {
                        try {
                            InputStream in = response.body().byteStream();
                            FileOutputStream out = new FileOutputStream(mContext.getFilesDir() + File.separator + fileName);
                            int c;

                            while ((c = in.read()) != -1) {
                                out.write(c);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "FileOutputStream error", e);
                        }
                    }
                    setImageBitmap(fileName, imageView);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                }

            });
        });
    }

    private void setImageBitmap(String fileName, ImageView imageView) {
        int width, height;
        Bitmap bMap = BitmapFactory.decodeFile(mContext.getFilesDir() + File.separator + fileName);
        //width = 2*bMap.getWidth();
        //height = 6*bMap.getHeight();
        //Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);
        imageView.setImageBitmap(bMap);
    }
}
