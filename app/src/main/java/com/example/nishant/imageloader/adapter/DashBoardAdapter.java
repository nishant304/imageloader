package com.example.nishant.imageloader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nishant.imageloader.R;
import com.example.nishant.imageloader.models.MasterResponse;
import com.example.nishant.imageloader.models.User;
import com.example.nishant.imageloader.network.MVNetworkClient;
import com.example.nishant.imageloader.network.ResponseListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 12/4/2016.
 */

public class DashBoardAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    private final LayoutInflater inflater;
    private final RecyclerView recyclerView;
    private final WeakReference<ILoadUserDetailListener> iLoadUserDetailListener;
    ArrayList<MasterResponse> list = new ArrayList<>();

    public DashBoardAdapter(Context context, RecyclerView recyclerView, ILoadUserDetailListener iLoadUserDetailListener) {
        this.inflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        this.iLoadUserDetailListener = new WeakReference<ILoadUserDetailListener>(iLoadUserDetailListener);
    }

    public void addItems(List<MasterResponse> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        view.setOnClickListener(new ItemClickListener(this));
        ImageViewHolder imageViewholder = new ImageViewHolder(view);
        return imageViewholder;
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

        holder.textView.setText("loading...");
        MVNetworkClient.getInstance().addImageRequest(holder.imageView, new ResponseListener<Bitmap>
                (Bitmap.class, getUrl(position)) {
            @Override
            public void onResponse(List<Bitmap> t) {

            }

            @Override
            public void onResponse(final Bitmap bitmap) {
                holder.imageView.setImageBitmap(bitmap);
                holder.textView.setText(list.get(position).getUser().getName());
            }

            @Override
            public void onError(String message) {
                holder.textView.setText("");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getUrl(int pos) {
        DisplayMetrics metrics = inflater.getContext().getResources().getDisplayMetrics();
        if (metrics.densityDpi == 240) {
            return list.get(pos).getUrls().getRegular();
        } else if (metrics.densityDpi > 240) {
            return list.get(pos).getUrls().getFull();
        } else if (metrics.densityDpi > 160) {
            return list.get(pos).getUrls().getSmall();
        } else {
            return list.get(pos).getUrls().getThumb();
        }
    }

    private MasterResponse getItem(int pos) {
        return list.get(pos);
    }

    private static class ItemClickListener implements View.OnClickListener {

        private final WeakReference<DashBoardAdapter> weakDash;

        ItemClickListener(DashBoardAdapter dashBoardAdapter) {
            weakDash = new WeakReference<DashBoardAdapter>(dashBoardAdapter);
        }

        @Override
        public void onClick(View v) {
            DashBoardAdapter dashBoardAdapter = weakDash.get();
            if (dashBoardAdapter == null) {
                return;
            }
            int pos = dashBoardAdapter.recyclerView.getChildLayoutPosition(v);
            ILoadUserDetailListener listener = dashBoardAdapter.iLoadUserDetailListener.get();
            if (listener != null) {
                listener.loadUser(dashBoardAdapter.getItem(pos).getUser());
            }
        }
    }

    public interface ILoadUserDetailListener {
        void loadUser(User user);
    }

}

class ImageViewHolder extends RecyclerView.ViewHolder {

    final ImageView imageView;
    final TextView textView;

    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        textView = (TextView) itemView.findViewById(R.id.tvUserName);
    }
}



