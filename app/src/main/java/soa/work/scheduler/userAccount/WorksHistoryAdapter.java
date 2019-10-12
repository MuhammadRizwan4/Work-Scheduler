package soa.work.scheduler.userAccount;

import android.content.Context;
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
import soa.work.scheduler.R;
import soa.work.scheduler.models.IndividualWork;

import static soa.work.scheduler.data.Constants.CARPENTER;
import static soa.work.scheduler.data.Constants.ELECTRICIAN;
import static soa.work.scheduler.data.Constants.MECHANIC;
import static soa.work.scheduler.data.Constants.PAINTER;
import static soa.work.scheduler.data.Constants.PLUMBER;

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
        holder.workDescriptionTextView.setText("Description: " + work.getWork_description());
        holder.createdAtTextView.setText("Posted at: " + work.getCreated_date());

        switch (work.getWork_category()) {
            case PAINTER:
                holder.categoryImageView.setImageResource(R.drawable.ic_painter);
                break;
            case CARPENTER:
                holder.categoryImageView.setImageResource(R.drawable.ic_carpenter);
                break;
            case PLUMBER:
                holder.categoryImageView.setImageResource(R.drawable.ic_plumber);
                break;
            case MECHANIC:
                holder.categoryImageView.setImageResource(R.drawable.ic_mechanic);
                break;
            case ELECTRICIAN:
                holder.categoryImageView.setImageResource(R.drawable.ic_electrician);
        }

        if (work.getIs_work_available()){
            if (work.getWork_completed()) {
                holder.statusOfWOrk.setText("Completed");
                holder.statusIcon.setBackgroundResource(R.drawable.circle_green);
            } else {
                holder.statusIcon.setBackgroundResource(R.drawable.circle_yellow);
                if (work.getAssigned_to() != null && !work.getAssigned_to().isEmpty()) {
                    holder.statusOfWOrk.setText("Assigned to: " + work.getAssigned_to());
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
