package com.zeyad.anew.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeyad.anew.Listners.onOptionClicked;
import com.zeyad.anew.R;
import com.zeyad.anew.databinding.OptionCustomItemBinding;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionsViewHolder> {
    List<String> options;

    Context context;

    onOptionClicked optionListner;

    public OptionsAdapter(List<String> options, Context context, onOptionClicked optionListner) {
        this.options = options;
        this.context = context;
        this.optionListner = optionListner;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OptionsViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.option_custom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, int position) {
        holder.binding.optionName.setText(options.get(position));

        // To check for option type
        if (options.get(position).equalsIgnoreCase("share")) {
            holder.binding.optionImage.setImageResource(R.drawable.share);
        } else if (options.get(position).equalsIgnoreCase("details")) {
            holder.binding.optionImage.setImageResource(R.drawable.baseline_info);
        } else if (options.get(position).equalsIgnoreCase("delete")) {
            holder.binding.optionImage.setImageResource(R.drawable.delete);
        } else if (options.get(position).equalsIgnoreCase("rename")) {
            holder.binding.optionImage.setImageResource(R.drawable.rename);
        }

        // Handle on click for options
        /**
         * TODO: Rename the file
         */
        holder.itemView.setOnClickListener(click -> {

            if (options.get(position).equalsIgnoreCase("share")) {
                optionListner.ShareFile();
            } else if (options.get(position).equalsIgnoreCase("details")) {
                optionListner.DetailsFile();
            } else if (options.get(position).equalsIgnoreCase("delete")) {
                optionListner.DeleteFile(position);
            } else if (options.get(position).equalsIgnoreCase("rename")) {
                Log.d("aly", "onBindViewHolder: " + "I am here");
                optionListner.RenameFILE(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return options.size();
    }




    class OptionsViewHolder extends RecyclerView.ViewHolder {
        OptionCustomItemBinding binding;

        public OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OptionCustomItemBinding.bind(itemView);
        }
    }
}
