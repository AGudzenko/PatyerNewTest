package com.example.patyernewtest.Presenter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.patyernewtest.Model.PlaceMark;
import com.example.patyernewtest.View.UpdatePlaceMark;
import com.example.patyernewtest.View.AddPlaceMark;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class PlaceMarkPresenter implements IPlaceMarkPresenter{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    FirebaseAuth mAuth;
    AddPlaceMark addMarkView;
    UpdatePlaceMark updatePlaceMark;
    String email;


    public PlaceMarkPresenter(AddPlaceMark addMarkView) {
        this.addMarkView = addMarkView;
    }
    public PlaceMarkPresenter(UpdatePlaceMark updatePlaceMark) {
        this.updatePlaceMark = updatePlaceMark;
    }

    @Override
    public void writePlaceMarkToDB(String name, double latitude, double longitude, String emailUser, String description, String contact, String dataTime, String timeTysa, int removeInHours, int numberOfJoinUsers) {
        PlaceMark mark = new PlaceMark(name, latitude, longitude, emailUser, description, contact, dataTime, timeTysa, removeInHours, numberOfJoinUsers);
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference().child("PlaceMark");
        ref.push().setValue(mark);
        addMarkView.writePlaceMarkDone("Мероприятие добавлено!");
    }

    @Override
    public void readPlaceMark() {
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("PlaceMark").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    PlaceMark mark = ds.getValue(PlaceMark.class);
                    mark.setId(ds.getKey());

                    DateTime dataTime = new DateTime(mark.getDataTime());

                    if(dataTime.plusHours(mark.getRemoveInHours()).isBeforeNow()){
                        updatePlaceMark.showPlaceMark(mark, false);
                        ds.getRef().setValue(null);

                    } else {
                        updatePlaceMark.showPlaceMark(mark, true);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to read value.", error.toException());
                updatePlaceMark.errorUpdatePlaceMark("Failed to read value." + error.toException());
            }
        });
    }

    @Override
    public void showInfoPlaceMark(String id) {
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("PlaceMark").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if (ds.getKey().equals(id)){

                        PlaceMark mark = ds.getValue(PlaceMark.class);
                        mark.setId(ds.getKey());
                        updatePlaceMark.showInfoPlaceMarkView(mark);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to read value.", error.toException());
                updatePlaceMark.errorUpdatePlaceMark("Failed to read value." + error.toException());
            }
        });
    }

    @Override
    public void userJoinPlaceMark(String id, int numberOfJoin) {

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("PlaceMark").child(id).child("numberOfJoinUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().setValue(numberOfJoin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to read value.", error.toException());
                updatePlaceMark.errorUpdatePlaceMark("Failed to read value." + error.toException());
            }
        });

    }

    @Override
    public void userPlaceMarkIdListAdd(String id, String emailUser) {

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("email").getValue().toString().equals(emailUser)){
                        ds.child("userPlaceMarkIdList").child(id).getRef().setValue(id);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to read value.", error.toException());
                updatePlaceMark.errorUpdatePlaceMark("Failed to read value." + error.toException());
            }
        });
    }

    @Override
    public void userPlaceMarkIdListDelete(String id, String emailUser) {
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("email").getValue().toString().equals(emailUser)){
                        for (DataSnapshot ds2 : ds.child("userPlaceMarkIdList").getChildren()){
                            if (ds2.getValue().toString().equals(id)) {
                                ds2.getRef().setValue(null);
                            }
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to read value.", error.toException());
                updatePlaceMark.errorUpdatePlaceMark("Failed to read value." + error.toException());
            }
        });
    }

    @Override
    public void listUserPlaceMarkId(String emailUser) {
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("email").getValue().toString().equals(emailUser)){
                        for (DataSnapshot ds2 : ds.child("userPlaceMarkIdList").getChildren()){
                            updatePlaceMark.readListUserPlaceMark(ds2.getValue().toString());
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "Failed to read value.", error.toException());
                updatePlaceMark.errorUpdatePlaceMark("Failed to read value." + error.toException());
            }
        });
    }


}

