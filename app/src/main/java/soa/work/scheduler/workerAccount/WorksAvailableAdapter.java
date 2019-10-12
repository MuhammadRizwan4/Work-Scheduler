package soa.work.scheduler.workerAccount;

import android.annotation.SuppressLint;
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
import soa.work.scheduler.models.UniversalWork;

import static soa.work.scheduler.data.Constants.CARPENTER;
import static soa.work.scheduler.data.Constants.ELECTRICIAN;
import static soa.work.scheduler.data.Constants.MECHANIC;
import static soa.work.scheduler.data.Constants.PAINTER;
import static soa.work.scheduler.data.Constants.PLUMBER;

public class WorksAvailableAdapter extends RecyclerView.Adapter<WorksAvailableAdapter.ViewHolder> {

    private ItemCLickListener itemCLickListener;
    private List<UniversalWork> list;
    private Context mContext;

    public WorksAvailableAdapter(List<UniversalWork> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_available_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UniversalWork work = list.get(position);
        holder.user_name.setText(work.getWork_posted_by_name());
        holder.workDescriptionTextView.setText("Description: " + work.getWork_description());
        holder.work_deadline.setText("Deadline: " + work.getWork_deadline());
        holder.work_price_range.setText("Rs." + work.getPrice_range_from() + " - " + "Rs." + work.getPrice_range_to());
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
        @BindView(R.id.user_name_text_view)
        TextView user_name;
        @BindView(R.id.deadline)
        TextView work_deadline;
        @BindView(R.id.work_price)
        TextView work_price_range;

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
        void onItemClick(UniversalWork work);
    }
}
