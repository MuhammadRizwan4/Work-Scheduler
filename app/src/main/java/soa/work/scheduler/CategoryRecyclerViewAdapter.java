package soa.work.scheduler;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import static soa.work.scheduler.data.Constants.PRICE_OFFERS;
import static soa.work.scheduler.data.Constants.WORK_PRICE;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private List<Category> categories;
    private ItemCLickListener itemCLickListener;
    private Context mContext;
    private ProgressDialog progressDialog;

    public CategoryRecyclerViewAdapter(List<Category> categories, Context mContext) {
        this.categories = categories;
        this.mContext = mContext;
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
        fetchImage(categories.get(position).getCategoryImage(), holder.categoryImageView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference(PRICE_OFFERS).child(categories.get(position).getCategoryTitle()).child(WORK_PRICE);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String price = dataSnapshot.getValue(String.class);
                if (price == null){
                    holder.priceTextView.setVisibility(View.GONE);
                } else {
                    holder.priceTextView.setText("Price starts at : \n₹ " + price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private void fetchImage(String url, ImageView imageView) {
        ApiService apiService = RetrofitClient.getApiServiceForImageDownload();
        Call<ResponseBody> downloadImageCall = apiService.getImage(url);
        progressDialog = new ProgressDialog(mContext);

        downloadImageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.body() != null) {
                    try (InputStream in = response.body().byteStream(); FileOutputStream out = new FileOutputStream(mContext.getFilesDir() + File.separator + "test.jpg")) {
                        int c;

                        while ((c = in.read()) != -1) {
                            out.write(c);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                int width, height;
                Bitmap bMap = BitmapFactory.decodeFile(mContext.getFilesDir() + File.separator + "test.jpg");
                //width = 2*bMap.getWidth();
                //height = 6*bMap.getHeight();
                //Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);
                imageView.setImageBitmap(bMap);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }

        });
    }

}
