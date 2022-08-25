package com.example.kakihomeui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final List<MyItems> items; //items array list
    private final Context context; // context
    private RecyclerViewInterface recyclerViewInterface;

    // constructor
    public MyAdapter(List<MyItems> items, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.items = items;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }



    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate( R.layout.recyclerview_adapter_layout, parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        MyItems myItems = items.get(position);

        holder.email.setText(myItems.getEmail());
        holder.date.setText(myItems.getDate());
        holder.dateP.setText((myItems.getDateP()));
        holder.des.setText((myItems.getDes()));
        holder.title.setText((myItems.getTitle()));
        holder.loc.setText((myItems.getLoc()));
        holder.attendees.setText((myItems.getAttendees()));
        holder.time.setText((myItems.getTime()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // MyViewHolder class to hold reference for every item in the RV
     public static class MyViewHolder extends RecyclerView.ViewHolder {

        //declare TVs
        private final TextView email, date, dateP, des, title, loc, attendees, time;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            // getting TV from xml file
            email = itemView.findViewById(R.id.emailTV);
            date = itemView.findViewById(R.id.dateTV);
            dateP = itemView.findViewById(R.id.datePTV);
            des = itemView.findViewById(R.id.desTV);
            title = itemView.findViewById(R.id.titleTV);
            loc = itemView.findViewById(R.id.locTV);
            attendees = itemView.findViewById(R.id.attendeesTV);
            time = itemView.findViewById(R.id.timeTV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
