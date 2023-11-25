package com.example.calladoctor.Class;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calladoctor.DoctorHomePage;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.RecyclerViewHolder> {
    private Context context;
    private List<Appointment> appointmentList;
    private OnItemClickedListener<Appointment> itemClickListener;
    private String fullName;


    public HomePageAdapter(Context context, List<Appointment> appointmentList, OnItemClickedListener<Appointment> itemClickListener){
        this.context = context;
        this.appointmentList = appointmentList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public HomePageAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homepage_rv_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePageAdapter.RecyclerViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
//        String name = appointment.getPatient().getfName() + appointment.getPatient().getlName();
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        // Specify the document ID you want to retrieve
        String documentId = appointment.getPat();  // Replace with the actual document ID
        DocumentReference docRef = fb.collection("users").document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Patient p = document.toObject(Patient.class);
                    holder.patientName.setText(p.getfName() + " " + p.getlName());
                }
            }
        });
//        holder.patientName.setText(fullName);
        holder.requestDate.setText(formatDate(appointment.getDateRq()));
    }

    @NonNull
    private String formatDate(String date){
        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        LocalDate dt = LocalDate.parse(date, dateFormatter);
        // Format the LocalDate to a String
//        return date.format(dateFormatter);
        return dt.toString();
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private ImageView profilePic;
        private TextView patientName;
        private TextView requestDate;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.rv_profile);
            patientName = itemView.findViewById(R.id.rv_name);
            requestDate = itemView.findViewById(R.id.rv_date);
        }
    }
}
