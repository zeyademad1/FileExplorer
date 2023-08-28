package com.zeyad.anew.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zeyad.anew.Listners.onFileClickListner;
import com.zeyad.anew.R;
import com.zeyad.anew.databinding.FolderItemBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.FilesListViewHolder> {
    List<File> fileList;
    Context context;

    onFileClickListner listner;

    public FilesListAdapter(List<File> fileList, Context context, onFileClickListner listner) {
        this.fileList = fileList;
        this.context = context;
        this.listner =  listner;
    }


    @NonNull
    @Override
    public FilesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilesListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_item, parent, false));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FilesListViewHolder holder, int position) {
        holder.binding.fileName.setText(fileList.get(position).getName());

        if (fileList.get(position).isDirectory()) {

            int subFiles = Objects.requireNonNull(fileList.get(position).listFiles()).length;
            holder.binding.enter.setVisibility(View.VISIBLE);
            holder.binding.fileSize.setText(subFiles + " Files");

        }
        // to show size of the file
        holder.binding.fileSize.setText(Formatter.formatFileSize(context, fileList.get(position).length()));

        // To Format date
        Date date = new Date(fileList.get(position).lastModified());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.binding.fileDate.setText(simpleDateFormat.format(date));

        //To Change file icon
        if (fileList.get(position).getName().toLowerCase().endsWith("jpeg") ||
                fileList.get(position).getName().toLowerCase().endsWith("jpg")
                || fileList.get(position).getName().toLowerCase().endsWith("png")) {
            Uri image_uri = Uri.fromFile(fileList.get(position));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), image_uri);
                holder.binding.fileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (fileList.get(position).getName().toLowerCase().endsWith("mp3") ||
                fileList.get(position).getName().toLowerCase().endsWith("wav") ||
                fileList.get(position).getName().toLowerCase().endsWith("m4a") ||
                fileList.get(position).getName().toLowerCase().endsWith("amr")) {
            holder.binding.fileImage.setImageResource(R.drawable.next_audio);

        } else if (fileList.get(position).getName().toLowerCase().endsWith("mp4")) {

            Glide.with(context)
                    .load(fileList.get(position).getAbsoluteFile()).into(holder.binding.fileImage);

        } else if (fileList.get(position).getName().toLowerCase().endsWith("apk")) {

            Drawable drawable = null;
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(String.valueOf(fileList.get(position)), 0);
                if (packageInfo != null) {
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    appInfo.sourceDir = fileList.get(position).getAbsolutePath();
                    appInfo.publicSourceDir = fileList.get(position).getAbsolutePath();
                    drawable = appInfo.loadIcon(context.getPackageManager());
                    holder.binding.fileImage.setImageDrawable(drawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            holder.binding.fileImage.setImageResource(R.drawable.next_application);
        } else if (fileList.get(position).getName().toLowerCase().endsWith("pdf")) {
            holder.binding.fileImage.setImageResource(R.drawable.final_pdf);
        } else if (fileList.get(position).getName().toLowerCase().endsWith("csv")) {
            holder.binding.fileImage.setImageResource(R.drawable.xls);

        } else if (fileList.get(position).getName().toLowerCase().endsWith("docx")) {
            holder.binding.fileImage.setImageResource(R.drawable.word);

        } else if (fileList.get(position).getName().toLowerCase().endsWith("txt")) {
            holder.binding.fileImage.setImageResource(R.drawable.txt);

        }else if (fileList.get(position).getName().toLowerCase().endsWith("ppt")) {
            holder.binding.fileImage.setImageResource(R.drawable.ppt);

        } else if (fileList.get(position).getName().toLowerCase().endsWith("zip")) {
            holder.binding.fileImage.setImageResource(R.drawable.next_zip);
        } else {
            holder.binding.fileImage.setImageResource(R.drawable.folder1);
        }

        holder.itemView.setOnClickListener(c -> {

            listner.onFileClick(fileList.get(position));

        });

        holder.itemView.setOnLongClickListener(c -> {
            listner.onFileLongClick(fileList.get(position));
            return true;
        });


    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    class FilesListViewHolder extends RecyclerView.ViewHolder {
        FolderItemBinding binding;

        public FilesListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FolderItemBinding.bind(itemView);
        }
    }
}


