package soa.work.scheduler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.models.Category;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private List<Category> categories;
    private ItemCLickListener itemCLickListener;

    public CategoryRecyclerViewAdapter(List<Category> categories) {
        this.categories = categories;
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
        holder.categoryImageView.setImageResource(categories.get(position).getCategoryImage());
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

    public interface ItemCLickListener{
        void onItemClick(Category category);
    }
}
