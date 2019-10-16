package soa.work.scheduler.userAccount;

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
import soa.work.scheduler.R;
import soa.work.scheduler.models.Category;
import soa.work.scheduler.models.IndividualWork;
import soa.work.scheduler.retrofit.ApiService;
import soa.work.scheduler.retrofit.RetrofitClient;

public class WorksHistoryAdapter extends RecyclerView.Adapter<WorksHistoryAdapter.ViewHolder> {

    private ItemCLickListener itemCLickListener;
    private List<IndividualWork> list;
    private Context mContext;
    public WorksHistoryAdapter(List<IndividualWork> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IndividualWork work = list.get(position);
        holder.workDescriptionTextView.setText("Description: " + work.getWorkDescription());
        holder.createdAtTextView.setText("Posted at: " + work.getCreatedDate());

        for (Category category : Category.getCategories()) {
            if (category.getCategoryTitle().equals(work.getWorkCategory())) {
                fetchImage(category.getCategoryImageFileName(), holder.categoryImageView);
            }
        }

        if (work.getWorkAvailable()){
            if (work.getWorkCompleted()) {
                holder.statusOfWOrk.setText("Completed");
                holder.statusIcon.setBackgroundResource(R.drawable.circle_green);
            } else {
                holder.statusIcon.setBackgroundResource(R.drawable.circle_yellow);
                if (work.getAssignedTo() != null && !work.getAssignedTo().isEmpty()) {
                    holder.statusOfWOrk.setText("Assigned to: " + work.getAssignedTo());
                } else {
                    holder.statusOfWOrk.setText("Pending");
                }
            }
        } else{
            holder.statusOfWOrk.setText("Unpublished");
            holder.statusIcon.setBackgroundResource(R.drawable.circle_red);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener(ItemCLickListener itemClickListener) {
        this.itemCLickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.category_imageView)
        ImageView categoryImageView;
        @BindView(R.id.work_description_text_view)
        TextView workDescriptionTextView;
        @BindView(R.id.created_at_text_view)
        TextView createdAtTextView;
        @BindView(R.id.status_of_work)
        TextView statusOfWOrk;
        @BindView(R.id.status_icon)
        ImageView statusIcon;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemCLickListener != null) {
                itemCLickListener.onItemClick(list.get(getAdapterPosition()));
            }
        }
    }

    public interface ItemCLickListener {
        void onItemClick(IndividualWork work);
    }

    private void fetchImage(String url, ImageView imageView) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ResponseBody> downloadImageCall = apiService.getImage(url);
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
