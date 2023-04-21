package ch.epfl.culturequest.backend.artprocessing.utils;

import android.graphics.Bitmap;

import com.google.firebase.FirebaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ArtImageUpload {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public CompletableFuture<String> uploadAndGetUrlFromImage(Bitmap bitmapImage){

        StorageReference imageRef = storageRef.child("images/"+ UUID.randomUUID().toString());

        CompletableFuture<String> f = new CompletableFuture<>();

        ByteArrayInputStream baos = new ByteArrayInputStream(bitmapImage.toString().getBytes());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.WEBP, 80, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        imageRef.putStream(bs).addOnSuccessListener(taskSnapshot -> {
            // Get a URL to the uploaded content
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Do something with the URL
                String url = uri.toString();
                f.complete(url);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                f.completeExceptionally(new FirebaseException("Could not get download url"));
            });
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            f.completeExceptionally(new FirebaseException("Could not upload image"));
        });

        return f;

    }



}
