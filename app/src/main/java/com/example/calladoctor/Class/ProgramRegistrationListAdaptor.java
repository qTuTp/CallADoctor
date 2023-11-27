package com.example.calladoctor.Class;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.R;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProgramRegistrationListAdaptor extends RecyclerView.Adapter<ProgramRegistrationListAdaptor.ProgramRegistrationListViewHolder> {
    private Context context;
    private List<ProgramRegistration> programRegistrationList;
    private OnItemClickedListener<ProgramRegistration> itemClickListener;

    public ProgramRegistrationListAdaptor(Context context, List<ProgramRegistration> programRegistrationList, OnItemClickedListener<ProgramRegistration> itemClickListener) {
        this.context = context;
        this.programRegistrationList = programRegistrationList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ProgramRegistrationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.program_registration_item, parent, false);
        return new ProgramRegistrationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramRegistrationListViewHolder holder, int position) {
        ProgramRegistration request = programRegistrationList.get(position);

        switch (request.getStatus()){
            case "Denied":
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.red));
                break;
            case "Completed":
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.green));
                break;
            case "Pending":
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.yellow));
                break;

        }

        holder.clinicName.setText(request.getClinicName());
        holder.status.setText(request.getStatus());

        holder.item.setOnClickListener(v -> {
            itemClickListener.onItemClicked(request);
        });

    }

    private String formatTime(LocalTime time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        // Format the LocalTime to a String and return
        return time.format(timeFormatter);
    }

    private String formatDate(LocalDate date){
        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        // Format the LocalDate to a String
        return date.format(dateFormatter);
    }

    @Override
    public int getItemCount() {
        return programRegistrationList.size();
    }

    public class ProgramRegistrationListViewHolder extends RecyclerView.ViewHolder{

        private ImageView statusIndicator;
        private TextView clinicName;
        private TextView status;
        private MaterialCardView item;
        public ProgramRegistrationListViewHolder(@NonNull View itemView) {
            super(itemView);

            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            clinicName = itemView.findViewById(R.id.clinicName);
            status = itemView.findViewById(R.id.status);
            item = itemView.findViewById(R.id.itemLayout);



        }
    }
}
