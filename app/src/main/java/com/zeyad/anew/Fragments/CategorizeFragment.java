package com.zeyad.anew.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zeyad.anew.Adapters.FilesListAdapter;
import com.zeyad.anew.Adapters.OptionsAdapter;
import com.zeyad.anew.Listners.onFileClickListner;
import com.zeyad.anew.Listners.onOptionClicked;
import com.zeyad.anew.R;
import com.zeyad.anew.Utils.FileOpener;
import com.zeyad.anew.Utils.FileTypes;
import com.zeyad.anew.databinding.DetailsLayoutBinding;
import com.zeyad.anew.databinding.FragmentCategorizeBinding;
import com.zeyad.anew.databinding.OptionDialogueBinding;
import com.zeyad.anew.databinding.RenameFileActionBinding;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CategorizeFragment extends Fragment implements onFileClickListner, onOptionClicked {
    FragmentCategorizeBinding binding;
    File storage;
    String internal_storage;
    List<File> files;

    FilesListAdapter filesListAdapter;

    File subFile;
    String returnedType;

    PopupWindow optionsPopUp;

    OptionsAdapter optionsAdapter;

    List<String> options;

    File temp_file;

    DetailsLayoutBinding details_binding;

    String information;

    public CategorizeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflater.inflate(R.layout.fragment_categorize, container, false);
        binding = FragmentCategorizeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //As requireArguments is always != null
        returnedType = CategorizeFragmentArgs.fromBundle(requireArguments()).getFileType();

        if (returnedType.equals(FileTypes.type_download)) {
            storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        } else {
            storage = Environment.getExternalStorageDirectory();
        }

        runTimePermission();

        binding.backImage.setOnClickListener(click ->
                requireActivity().onBackPressed()
        );


    }

    void runTimePermission() {
        Dexter.withContext(requireContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }

    @NonNull
    private ArrayList<File> getFiles(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] internal_storage_all_files = file.listFiles();
        if (internal_storage_all_files != null) {
            for (File temp : internal_storage_all_files) {
                if (temp.isDirectory() && !temp.isHidden()) {
                    fileArrayList.addAll(getFiles(temp));
                } else {
                    switch (returnedType) {
                        case FileTypes.type_app:
                            if (temp.getName().toLowerCase().endsWith(".apk"))
                                fileArrayList.add(temp);
                            break;
                        case FileTypes.type_document:
                            if (temp.getName().toLowerCase().endsWith(".csv") ||
                                    temp.getName().toLowerCase().endsWith(".txt") || temp.getName().toLowerCase().endsWith(".pdf")
                                    || temp.getName().toLowerCase().endsWith(".docx") || temp.getName().toLowerCase().endsWith(".ppt")) {
                                fileArrayList.add(temp);
                            }
                            break;
                        case FileTypes.type_archive:
                            if (temp.getName().toLowerCase().endsWith(".zip")) {
                                fileArrayList.add(temp);
                            }
                            break;
                        case FileTypes.type_download:
                            if (temp.getName().toLowerCase().endsWith(".jpg") || temp.getName().toLowerCase().endsWith(".jpeg")
                                    || temp.getName().toLowerCase().endsWith(".png") || temp.getName().toLowerCase().endsWith(".mp3")
                                    || temp.getName().toLowerCase().endsWith(".wav") || temp.getName().toLowerCase().endsWith(".mp4")
                                    || temp.getName().toLowerCase().endsWith(".apk") || temp.getName().toLowerCase().endsWith("pdf")
                                    || temp.getName().toLowerCase().endsWith(".zip")) {
                                fileArrayList.add(temp);
                            }
                            break;
                        case FileTypes.type_audio:
                            if (temp.getName().toLowerCase().endsWith(".mp3") ||
                                    temp.getName().toLowerCase().endsWith(".wav") || temp.getName().toLowerCase().endsWith(".amr") ||
                                    temp.getName().toLowerCase().endsWith(".m4a")) {
                                fileArrayList.add(temp);
                            }
                            break;
                        case FileTypes.type_video:
                            if (temp.getName().toLowerCase().endsWith(".mp4")) {
                                fileArrayList.add(temp);
                            }
                            break;
                        case FileTypes.type_image:
                            if (temp.getName().toLowerCase().endsWith(".jpg") ||
                                    temp.getName().toLowerCase().endsWith(".jpeg") || temp.getName().toLowerCase().endsWith("png")) {
                                fileArrayList.add(temp);
                            }
                            break;
                    }
                }

            }

        }
        return fileArrayList;

    }


    @SuppressLint("CheckResult")
    private void displayFiles() {

        files = new ArrayList<>();
        Single.fromCallable(() ->
                        files.addAll(getFiles(storage))
                ).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Toast.makeText(requireContext(), "Done ", Toast.LENGTH_SHORT).show();
                    }
                }).subscribe(fileList -> {
                    filesListAdapter = new FilesListAdapter(files, requireContext(), this);
                    binding.filesList.setItemAnimator(new DefaultItemAnimator());
                    binding.filesList.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.filesList.smoothScrollToPosition(0);
                    binding.filesList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
                    binding.filesList.setAdapter(filesListAdapter);
                    information = files.size() + " items , storage used: " + Formatter.formatFileSize(requireContext(), storage.getUsableSpace());
                    binding.fastInformation.setText(information);
                    Log.d("aly", "displayFiles: " + files.size());
                });


    }

    @Override
    public void onFileClick(File file) {
        if (file.isDirectory()) {
            // open the same fragment but change the parent folder
            NavHostFragment.findNavController(this).navigate(InternalStorageDirections
                    .actionNavigationInternalStorageSelf().setPath(file.getAbsolutePath()));
        } else {
            try {
                FileOpener.openFile(file, requireContext());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


    @Override
    public void onFileLongClick(File file) {
        temp_file = file;
        //We will create dialogue to show options
        OptionDialogueBinding dialogueBinding = OptionDialogueBinding.inflate(LayoutInflater.from(requireContext()));
        optionsPopUp = new PopupWindow(dialogueBinding.getRoot(),
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        initOptionsList(dialogueBinding);
        optionsPopUp.setAnimationStyle(R.style.Animation);
        optionsPopUp.showAtLocation(optionsPopUp.getContentView().getRootView(), Gravity.CENTER, 0, 0);
        optionsPopUp.showAsDropDown(binding.getRoot());
        optionsPopUp.setElevation(30);
        optionsPopUp.setOutsideTouchable(true);

        optionsPopUp.setBackgroundDrawable(new BitmapDrawable(getResources(),
                ""));
    }

    private void initOptionsList(OptionDialogueBinding dialogueBinding) {
        dialogueBinding.optionsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        options = new ArrayList<>();
        setOptions();
        optionsAdapter = new OptionsAdapter(options, requireContext(), this);
        dialogueBinding.optionsList.setAdapter(optionsAdapter);


    }

    private void setOptions() {
        options.add("Share");
        options.add("Rename");
        options.add("Delete");
        options.add("Details");
    }


    @Override
    public void RenameFILE(int position) {
        optionsPopUp.dismiss();
        RenameFileActionBinding renameFileActionBinding = RenameFileActionBinding.inflate(LayoutInflater.from(requireContext()));
        Dialog rename_dialogue = new Dialog(requireContext());
        rename_dialogue.setContentView(renameFileActionBinding.getRoot());
        renameFileActionBinding.newName.setText(temp_file.getName());

        renameFileActionBinding.btnCancle.setOnClickListener(cancel -> rename_dialogue.cancel());

        Log.d("aly", "RenameFILE: " + temp_file);

        renameFileActionBinding.btnOk.setOnClickListener(ok -> {
            String new_name = renameFileActionBinding.newName.getText().toString();
            String extension = FilenameUtils.getExtension(temp_file.getAbsolutePath());
            if (new_name.equals(temp_file.getName())) {
                renameFileActionBinding.newName.setError("The Name isn't changed");
            }
            File current = new File(temp_file.getAbsolutePath());
            File destination = new File(temp_file.getAbsolutePath().replace(temp_file.getName(), new_name) + extension);

            if (current.renameTo(destination)) {
                files.add(position, destination);
                filesListAdapter.notifyItemChanged(position);
            }
            rename_dialogue.cancel();
        });
        rename_dialogue.show();
    }

    @SuppressLint({"InflateParams", "ResourceType"})
    @Override
    public void DetailsFile() {
        // We Will Show a popup Window
        optionsPopUp.dismiss();
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        details_binding = DetailsLayoutBinding.inflate(inflater);
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        setUpDetails();
        PopupWindow popupWindow = new PopupWindow(details_binding.getRoot(), width, height, focusable);
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.showAtLocation(popupWindow.getContentView().getRootView(), Gravity.BOTTOM, 0, 0);
        details_binding.ok.setOnClickListener(c -> {
            popupWindow.dismiss();
        });


    }

    @SuppressLint("SetTextI18n")
    void setUpDetails() {
        // For File Name
        details_binding.detailsFileName.setText(temp_file.getName() + FilenameUtils.getExtension(temp_file.getName()));

        // For File MIME Type
        try {
            details_binding.detailsFileType.setText(temp_file.toURL().openConnection().getContentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //For File Size
        String fileSize = Formatter.formatFileSize(requireContext(), temp_file.length());
        details_binding.detailsFileSize.setText(fileSize);

        //For FIle last Modified
        Date date = new Date(temp_file.lastModified());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        String lastModifiedDate = dateFormat.format(date);
        String lastModifiedTime = timeFormat.format(date);
        details_binding.detailsFileModifiedDate.setText(String.format("%s %s", lastModifiedDate, lastModifiedTime));

        //For Path
        details_binding.detailsFilePath.setText(temp_file.getAbsolutePath());
    }

    @Override
    public void DeleteFile(int position) {
        optionsPopUp.dismiss();
        boolean delete = temp_file.delete();
        if (delete) {
            files.remove(position);
            filesListAdapter.notifyItemRemoved(position);
        } else {
            Toast.makeText(requireContext(), "Sorry, Can't Delete this file", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void ShareFile() {
        Uri data = FileProvider.getUriForFile(requireContext(), requireContext()
                .getApplicationContext().getPackageName() + ".provider", temp_file);

        String fileName = temp_file.getName();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("*/*");
        Log.d("TAG", "aly ShareFile: " + fileName);
        share.putExtra(Intent.EXTRA_STREAM, data);
        startActivity(Intent.createChooser(share, "Share " + fileName + " Using: "));

 }


}