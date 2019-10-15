package soa.work.scheduler.userAccount;

import android.content.Context;
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
import soa.work.scheduler.R;
import soa.work.scheduler.models.Category;
import soa.work.scheduler.models.IndividualWork;

public class WorksHistoryAdapter extends RecyclerView.Adapter<WorksHistoryAdapter.ViewHolder> {

    private ItemCLickListener itemCLickListener;
    private List<IndividualWork> list;
    private IndividualWork work;
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
        work = list.get(position);
        holder.workDescriptionTextView.setText("Description: " + work.getWorkDescription());
        holder.createdAtTextView.setText("Posted at: " + work.getCreatedDate());

        for (Category category : Category.getCategories()) {
            if (category.getCategoryTitle().equals(work.getWorkCategory())) {
                Picasso.Builder builder = new Picasso.Builder(mContext);
                builder.listener((picasso, uri, exception) -> Toast.makeText(mContext, "Failed to load profile pic", Toast.LENGTH_SHORT).show());
                Picasso pic = builder.build();
                pic.load(category.getCategoryTitle())
                        .into(holder.categoryImageView);
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
}
