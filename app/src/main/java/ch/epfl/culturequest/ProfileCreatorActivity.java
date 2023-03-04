package ch.epfl.culturequest;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileCreatorActivity extends ComponentActivity {
    private final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        assert USER != null;
        ((TextView)findViewById(R.id.email_text)).setText(USER.getEmail());
        String fullName = USER.getDisplayName();
        String[] names = fullName.split(" ");
        ((TextView)findViewById(R.id.first_name_text)).setText(names[0]);
        ((TextView)findViewById(R.id.last_name_text)).setText(names[1]);
    }


    public void createProfile(View view){
        System.out.println("created!!");
    }


}
