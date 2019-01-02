package com.example.michalcop.rememberall.activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.michalcop.rememberall.R;
import com.example.michalcop.rememberall.database.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter is required to make RecyclerView work. That is the real backend object that
 * creates, inflates, fills and updates whole data-revolving elements in the MainActivity.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    //Inner interface implemented in MainActivity. We use interface here so we can have different
    //implementations in different places.
    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(Post post);
    }

    //Initialise required objects/interfaces.
    private List<Post> data;
    private Context context; //Context is only used to define inflater.
    private LayoutInflater layoutInflater; //"Instantiates a layout XML file into its corresponding view"
    private OnDeleteButtonClickListener onDeleteButtonClickListener; //Listener is a design pattern that creates method to be done "on call".

    //Constructor class required by Adapter. We implement OnDeleteListener here so need to construct it too.
    PostsAdapter(Context context, OnDeleteButtonClickListener listener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onDeleteButtonClickListener = listener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //ViewHolder defines what is beeing held in the view. Overriden method defines what view we are
    //actually using, what elements are to be taken into the consideration.
    //Defines functionality and/or content of xml elements in one of RecyclerViews elements.
    //To make it short, framework magic. Following overrides are self-explainatory and also required by Adapter.
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_post_item, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.bind(data.get(position)); //Bind data to right RV element.
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //Method that sets new data comparing it to the old one. Using DiffUtil reduces memory required.
    public void setData(List<Post> newData) {
        if (data != null) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    //ViewHolders were explained earlier upon overriding. We init each sub-element of one of RV elements.
    class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvContent, tvDateCreated, tvDateFor;
        private Button btnDelete;
        //constructor passing elements to main view.
        PostViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvDateFor = itemView.findViewById(R.id.tvDateFor);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
        //Bind method binds attributes to xml elements for a single RV element (post).
        void bind(final Post post) {
            if (post != null) {
                tvTitle.setText(post.getTitle());
                tvContent.setText(post.getContent());
                tvDateCreated.setText(post.getDateCreated());       //Use setter methods for xml and getter for data.
                tvDateFor.setText(post.getDateFor());
                btnDelete.setOnClickListener(v -> {                 //Make button interactive by providing listener to it.
                    if (onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClicked(post);
                    btnDelete.getBackground().setColorFilter(0x800000, PorterDuff.Mode.SRC_ATOP); // Listener would change bg forever. Need to set it back to given color here.

                });
            }
        }
    }

    //Callback methods repository used by DiffUtil used earlier on.
    class PostDiffCallback extends DiffUtil.Callback {

        private final List<Post> oldPosts, newPosts;
        //Constructor with new/old posts.
        PostDiffCallback(List<Post> oldPosts, List<Post> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getId() == newPosts.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}
