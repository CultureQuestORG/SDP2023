package ch.epfl.culturequest.utils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mockito.Mockito;

public class MockFirebaseDatabase {

    public static FirebaseDatabase getMockFirebaseDatabase() {
        FirebaseDatabase mockFirebaseDatabase =  Mockito.mock(FirebaseDatabase.class);
        DatabaseReference mockDatabaseReference = Mockito.mock(DatabaseReference.class);
        when(mockFirebaseDatabase.getReference(anyString())).thenReturn(mockDatabaseReference);
        return mockFirebaseDatabase;
    }
}
