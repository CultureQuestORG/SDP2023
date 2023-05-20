package ch.epfl.culturequest.backend.tournament.apis;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

public class AppConcurrencyApi {


    // Indicate other users that the tournament is currently being generated
    public static CompletableFuture<Boolean> lockTournamentGeneration() {

        DatabaseReference pathToGenerationLock = getDeviceSynchronizationRef().child("generationLocked");
        return setBoolAsync(pathToGenerationLock, true);
    }


    // To unlock when the tournament is over or if one of the device fails to generate the tournament
    public static CompletableFuture<Boolean> unlockTournamentGeneration() {

        DatabaseReference pathToGenerationLock = getDeviceSynchronizationRef().child("generationLocked");
        return setBoolAsync(pathToGenerationLock, false);
    }


    // Allow of form of synchronization to prevent other devices from generating the tournament if one of the device has already been charged to do so
    public static CompletableFuture<Boolean> isTournamentGenerationLocked() {

        DatabaseReference pathToGenerationLock = getDeviceSynchronizationRef().child("generationLocked");
        return isEqualAsync(pathToGenerationLock, true);
    }

    // Indicate other devices that the tournament can now be fetched from Firebase
    public static CompletableFuture<Boolean> indicateTournamentGenerated() {

        DatabaseReference pathToGenerated = getDeviceSynchronizationRef().child("generated");
        return setBoolAsync(pathToGenerated, true);
    }

    // Reset the generation state of the tournament to allow upcoming generation in the next week

    public static CompletableFuture<Boolean> indicateTournamentNotGenerated() {

        DatabaseReference pathToGenerated = getDeviceSynchronizationRef().child("generated");
        return setBoolAsync(pathToGenerated, false);
    }

    // Slot where the variables used to handle the android apps synchronization are stored
    public static DatabaseReference getDeviceSynchronizationRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference("tournaments").child("device-synchronization");
    }



    // Put boolean in database reference and returns a future boolean indicating whether the operation was successful or not
    // true -> setValue succeeded; null -> setValue failed
    @SuppressLint("NewApi")
    public static CompletableFuture<Boolean> setBoolAsync(DatabaseReference databaseReference, Boolean bool) {


        CompletableFuture<Boolean> future = new CompletableFuture<>();
        databaseReference.setValue(bool, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                if (error != null) {
                    future.complete(null);
                } else {
                    future.complete(true);
                }
            }
        });

        future.orTimeout(120, java.util.concurrent.TimeUnit.SECONDS);

        return future;
    }


    // Returns true if the value in the database reference is equal to the expected boolean
    // Returns false if the value in the database reference is not equal to the expected boolean
    // Returns null if the value in the database reference is null or if the operation failed

    @SuppressLint("NewApi")
    public static CompletableFuture<Boolean> isEqualAsync(DatabaseReference databaseReference, Boolean expectedBool) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean value = snapshot.getValue(Boolean.class);

                if (value == null) {
                    future.complete(null);
                } else {
                    future.complete(value.equals(expectedBool));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.complete(null);
            }
        });

        future.orTimeout(120, java.util.concurrent.TimeUnit.SECONDS);

        return future;

    }
}
