package soa.work.scheduler.userAccount;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.R;
import soa.work.scheduler.models.IndividualWork;

import static soa.work.scheduler.data.Constants.AC_REPAIRING;
import static soa.work.scheduler.data.Constants.BIKE_MECHANIC;
import static soa.work.scheduler.data.Constants.CARPENTER;
import static soa.work.scheduler.data.Constants.CAR_MECHANIC;
import static soa.work.scheduler.data.Constants.CATERING;
import static soa.work.scheduler.data.Constants.ELECTRICIAN;
import static soa.work.scheduler.data.Constants.HOME_TUTOR;
import static soa.work.scheduler.data.Constants.LAPTOP_OR_PC_REPAIRING;
import static soa.work.scheduler.data.Constants.MARVEL;
import static soa.work.scheduler.data.Constants.MECHANIC;
import static soa.work.scheduler.data.Constants.MOBILE_REPAIRING;
import static soa.work.scheduler.data.Constants.PAINTER;
import static soa.work.scheduler.data.Constants.PHOTOGRAPHY;
import static soa.work.scheduler.data.Constants.PLUMBER;
import static soa.work.scheduler.data.Constants.REFRIGERATOR_REPAIRING;
import static soa.work.scheduler.data.Constants.RENOVATION;
import static soa.work.scheduler.data.Constants.STUDENT_PROJECT;
import static soa.work.scheduler.data.Constants.T_SHIRT;
import static soa.work.scheduler.data.Constants.VOLUNTEER;
import static soa.work.scheduler.data.Constants.WASHING_MACHINE_REPAIRING;
import static soa.work.scheduler.data.Constants.WEDDING;

public class WorksHistoryAdapter extends RecyclerView.Adapter<WorksHistoryAdapter.ViewHolder> {

    private ItemCLickListener itemCLickListener;
    private List<IndividualWork> list;
    private Context mContext;
    private Bitmap[] images;

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
        if (list.size() != 0) {
            images = new Bitmap[list.size()];
        }
        
        IndividualWork work = list.get(position);
        holder.workDescriptionTextView.setText("Description: " + work.getWorkDescription());
        holder.createdAtTextView.setText("Posted at: " + work.getCreatedDate());
        holder.categoryImageView.setBackgroundResource(R.drawable.ic_image);

        switch (list.get(position).getWorkCategory()){
            case CARPENTER : setImageBitmap("carpenter.jpg", holder.categoryImageView, position);
                            break;
//            case MECHANIC : setImageBitmap("", holder.categoryImageView, position);
//                            break;
            case AC_REPAIRING : setImageBitmap("ac_repair.jpg", holder.categoryImageView, position);
                            break;
            case BIKE_MECHANIC  : setImageBitmap("bike_mechanic.jpg", holder.categoryImageView, position);
                            break;
            case CAR_MECHANIC : setImageBitmap("car_mechanic.jpg", holder.categoryImageView, position);
                            break;
            case ELECTRICIAN : setImageBitmap("commercial_electrician.jpg", holder.categoryImageView, position);
                            break;
            case LAPTOP_OR_PC_REPAIRING : setImageBitmap("laptop_pc_repairing.jpg", holder.categoryImageView, position);
                            break;
            case MOBILE_REPAIRING : setImageBitmap("mobile_repairing.jpg", holder.categoryImageView, position);
                            break;
            case PLUMBER : setImageBitmap("plumber.jpg", holder.categoryImageView, position);
                            break;
            case REFRIGERATOR_REPAIRING : setImageBitmap("refrigerator_repairing.jpg", holder.categoryImageView, position);
                            break;
            case WASHING_MACHINE_REPAIRING : setImageBitmap("washing_machine.jpg", holder.categoryImageView, position);
                            break;
            case PAINTER : setImageBitmap("", holder.categoryImageView, position);
                            break;
//            case MARVEL  :setImageBitmap("", holder.categoryImageView, position);
//                            break;
            case PHOTOGRAPHY : setImageBitmap("photography_videography.jpg", holder.categoryImageView, position);
                            break;
            case CATERING : setImageBitmap("catering.jpg", holder.categoryImageView, position);
                            break;
            case T_SHIRT  :setImageBitmap("t_shirt.jpg", holder.categoryImageView, position);
                            break;
            case WEDDING : setImageBitmap("wedding.jpg", holder.categoryImageView, position);
                            break;
            case STUDENT_PROJECT : setImageBitmap("project-Copy.jpg", holder.categoryImageView, position);
                            break;
//            case VOLUNTEER : setImageBitmap("", holder.categoryImageView, position);
//                            break;
            case HOME_TUTOR : setImageBitmap("home_tutor.jpg", holder.categoryImageView, position);
                            break;
            case RENOVATION  : setImageBitmap("renovation_service.jpg", holder.categoryImageView, position);
                            break;
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

    private void setImageBitmap(String fileName, ImageView imageView, int position) {
        imageView.setImageDrawable(null);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap appBitmap = images[position];
                if (appBitmap == null) {
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            int width, height;
                            Bitmap bMap = BitmapFactory.decodeFile(mContext.getFilesDir() + File.separator + fileName);
                            width = imageView.getWidth();
                            height = imageView.getHeight();
                            Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);
                            images[position] = bMap2;
                            imageView.setImageBitmap(bMap2);
                        }
                    });

                } else {
                    imageView.setImageBitmap(appBitmap);
                }
            }
        });
    }
}
