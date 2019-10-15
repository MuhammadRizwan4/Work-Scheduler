package soa.work.scheduler;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.models.Category;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private List<Category> categories;
    private ItemCLickListener itemCLickListener;
    private Context mContext;

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
        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener((picasso, uri, exception) -> Toast.makeText(mContext, "Failed to load profile pic", Toast.LENGTH_SHORT).show());
        Picasso pic = builder.build();
        Uri uri = Uri.parse(categories.get(position).getCategoryImage());
        pic.load(uri)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.categoryImageView);

        if (categories.get(position).getPrice() == 0) {
            holder.priceTextView.setVisibility(View.GONE);
        } else
            holder.priceTextView.setText("Price starts at : ₹" + categories.get(position).getPrice());
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

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }
}
