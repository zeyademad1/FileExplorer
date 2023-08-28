package com.zeyad.anew.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zeyad.anew.R;
import com.zeyad.anew.databinding.FragmentAbout2Binding;

public class About extends Fragment {
    FragmentAbout2Binding binding;

    public About() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_about2, container, false);
        binding = FragmentAbout2Binding.inflate(inflater);
        return binding.getRoot();
    }

    @SuppressLint("IntentReset")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.github.setOnClickListener(click -> {
            String url = "https://github.com/zeyademad1";
            Intent openGitHub = new Intent(Intent.ACTION_VIEW);
            openGitHub.setData(Uri.parse(url));
            startActivity(openGitHub);
        });

        binding.facebook.setOnClickListener(click -> {
            String pageId = "https://www.facebook.com/zeyad.emad.568";
            Intent faceBook;
            try {
                faceBook = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + pageId));
            } catch (Exception e) {
                faceBook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + pageId));
            }
            startActivity(faceBook);
        });

        binding.linkedIn.setOnClickListener(click -> {
            Intent linkedIn = null;
            try {
                requireContext().getPackageManager().getPackageInfo("com.linkedin.android", 0);
                linkedIn = new Intent(Intent.ACTION_VIEW);
                linkedIn.setData(Uri.parse("https://www.linkedin.com/in/zeyad-emad-947542256/"));
                requireContext().startActivity(linkedIn);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                startActivity(linkedIn);
            }
        });


        binding.email.setOnClickListener(click -> {
            Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
            sendEmail.setType("text/html");
            sendEmail.setData(Uri.parse("mailto:"));
            sendEmail.putExtra(Intent.EXTRA_EMAIL,  new String[] {"zeyademad1z234@gmail.com"});
            sendEmail.putExtra(Intent.EXTRA_SUBJECT, "SomeThing to Me");
            requireContext().startActivity(Intent.createChooser(sendEmail, "Send Email "));

        });


    }
}