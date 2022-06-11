package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.firebase.Model.User;
import com.example.firebase.UserAdapter.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * mendefinisikan variable yang akan di pakai
     */

    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;

    /**inisialisasi objek firebase firestore
     * untuk menghubungkan dengan firestore
     */

    private FirebaseFirestore db =  FirebaseFirestore.getInstance();

    private List<User> list = new ArrayList<>();
    private UserAdapter userAdapter;
    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        btnAdd = findViewById(R.id.btn_add);


        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setTitle("Mengambil data");
        userAdapter = new UserAdapter(getApplicationContext(),list);

        userAdapter.setDialog(new UserAdapter.Dialog() {
            @Override
            public void onClick(int post) {
                final CharSequence[] dialogItem = {"Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            /**
                             * melemparkan data ke kelas berikutnya
                             */

                            case 0:
                                Intent intent = new Intent(getApplicationContext(),EditorActivity.class);
                                intent.putExtra("id" ,list.get(post).getId());
                                intent.putExtra("name" ,list.get(post).getName());
                                intent.putExtra("email" ,list.get(post).getEmail());
                                startActivity(intent);
                                break;
                            case 1:

                                /**
                                 * memanggil kelas delete data
                                 */
                                deleteData(list.get(post).getId());
                                break;
                        }
                    }
                });
                dialog.show();

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(userAdapter);

        btnAdd.setOnClickListener(V ->{
            startActivity(new Intent(getApplicationContext(), EditorActivity.class));
        });

    }

    /**
     * method untuk menampilkan data agar di tampilkan
     * pada saat aplikasi pertamakali di running
     */


    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void getData(){
        progressDialog.show();

        /**
         * mengambil darta dari firestore
         */

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            /**
                             * code ini mengambil data dari collection
                             */
                            for (QueryDocumentSnapshot document : task.getResult()){

                                /**
                                 * Data apa saja yang akan di ambil dari collection
                                 */

                                User user = new User(document.getString("name"), document.getString("email"));
                                user.setId(document.getId());
                                list.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getApplicationContext(),"Data Gagal di Ambil!!!!!....",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    /**
     * method untuk menghapus data
     */

    private void deleteData(String id){
        progressDialog.show();
        db.collection("users").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Data Gagal di Hapuss!!!!!....",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        getData();
                    }
                });

    }
}