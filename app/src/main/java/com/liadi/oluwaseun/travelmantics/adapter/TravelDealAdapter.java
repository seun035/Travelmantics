package com.liadi.oluwaseun.travelmantics.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liadi.oluwaseun.travelmantics.AdminActivity;
import com.liadi.oluwaseun.travelmantics.R;
import com.liadi.oluwaseun.travelmantics.UserActivity;
import com.liadi.oluwaseun.travelmantics.models.TravelDeal;
import com.liadi.oluwaseun.travelmantics.repository.TravelDealRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TravelDealAdapter extends RecyclerView.Adapter<TravelDealAdapter.TravelDealViewHolder> {

    private List<TravelDeal> mTravelDeals;

    private Context context;

    public TravelDealAdapter(Context context) {

        this.context = context;
        mTravelDeals = new ArrayList<>();

        TravelDealRepository.getReference((UserActivity)context).readTravelDeals(mTravelDeals, this);
    }

    @NonNull
    @Override
    public TravelDealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TravelDealViewHolder(LayoutInflater.from(context).inflate(R.layout.travel_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TravelDealViewHolder holder, int position) {
        holder.bindTravelDeal(mTravelDeals.get(position));
    }

    @Override
    public int getItemCount() {
        return mTravelDeals.size();
    }


    class TravelDealViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTv;

        private TextView mDescriptionTv;

        private TextView mPriceTv;

        private ImageView mImageImgv;

        private TravelDeal mTravelDeal;

        public TravelDealViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.list_item_title);
            mDescriptionTv = itemView.findViewById(R.id.list_item_description);
            mPriceTv = itemView.findViewById(R.id.list_item_price);
            mImageImgv = itemView.findViewById(R.id.list_item_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(AdminActivity.createAdminIntent(context,mTravelDeal));
                }
            });
        }

        public void bindTravelDeal(TravelDeal travelDeal) {
            mTravelDeal = travelDeal;
            mTitleTv.setText(travelDeal.getTitle());
            mPriceTv.setText(travelDeal.getPrice());
            mDescriptionTv.setText(travelDeal.getDescription());
            Picasso.get().load(travelDeal.getImageUrl()).into(mImageImgv);
        }
    }
}
