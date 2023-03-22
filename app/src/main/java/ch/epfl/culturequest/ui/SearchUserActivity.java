package ch.epfl.culturequest.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.ui.home.HomeFragment;

public class SearchUserActivity extends AppCompatActivity {
    Database db = new Database();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);


    }


    public void searchUser(View view) throws ExecutionException, InterruptedException {
        System.out.println("searching");
        List<String>usernames = db.getAllUsernames().get();
        System.out.println(usernames);
    }

    public void goBack(View view){
        super.onBackPressed();
    }
}
