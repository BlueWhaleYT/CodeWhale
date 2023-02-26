package com.bluewhaleyt.codewhale.components;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bluewhaleyt.codewhale.R;
import com.bluewhaleyt.codewhale.activities.MainActivity;
import com.bluewhaleyt.codewhale.databinding.ActivityMainBinding;
import com.bluewhaleyt.codewhale.utils.PreferencesManager;
import com.bluewhaleyt.filemanagement.FileIconUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class TreeView {

    public static boolean isPath = false;
    //        public static int textColor = 0xFF000000;
//        public static int backgroundColor = 0xFFFFFFFF;
    public static boolean darkMode = false;

    public static class TreeNode<T extends LayoutItemType> implements Cloneable {
        private T content;
        private TreeNode parent;
        private List<TreeNode> childList;
        private boolean isExpand;
        private boolean isLocked;
        //the tree high
        private int height = UNDEFINE;

        private static final int UNDEFINE = -1;

        public TreeNode(@NonNull T content) {
            this.content = content;
            this.childList = new ArrayList<>();
        }

        public int getHeight() {
            if (isRoot())
                height = 0;
            else if (height == UNDEFINE)
                height = parent.getHeight() + 1;
            return height;
        }

        public boolean isRoot() {
            return parent == null;
        }

        public boolean isLeaf() {
            return childList == null || childList.isEmpty();
        }

        public void setContent(T content) {
            this.content = content;
        }

        public T getContent() {
            return content;
        }

        public List<TreeNode> getChildList() {
            return childList;
        }

        public void setChildList(List<TreeNode> childList) {
            this.childList.clear();
            for (TreeNode treeNode : childList) {
                addChild(treeNode);
            }
        }

        public TreeNode addChild(TreeNode node) {
            if (childList == null)
                childList = new ArrayList<>();
            childList.add(node);
            node.parent = this;
            return this;
        }

        public boolean toggle() {
            isExpand = !isExpand;
            return isExpand;
        }

        public void collapse() {
            if (isExpand) {
                isExpand = false;
            }
        }

        public void collapseAll() {
            if (childList == null || childList.isEmpty()) {
                return;
            }
            for (TreeNode child : this.childList) {
                child.collapseAll();
            }
        }

        public void expand() {
            if (!isExpand) {
                isExpand = true;
            }
        }

        public void expandAll() {
            expand();
            if (childList == null || childList.isEmpty()) {
                return;
            }
            for (TreeNode child : this.childList) {
                child.expandAll();
            }
        }

        public boolean isExpand() {
            return isExpand;
        }

        public void setParent(TreeNode parent) {
            this.parent = parent;
        }

        public TreeNode getParent() {
            return parent;
        }

        public TreeNode<T> lock() {
            isLocked = true;
            return this;
        }

        public TreeNode<T> unlock() {
            isLocked = false;
            return this;
        }

        public boolean isLocked() {
            return isLocked;
        }

        @Override
        public String toString() {
            return "TreeNode{" +
                    "content=" + this.content +
                    ", parent=" + (parent == null ? "null" : parent.getContent().toString()) +
                    ", childList=" + (childList == null ? "null" : childList.toString()) +
                    ", isExpand=" + isExpand +
                    '}';
        }

        @Override
        protected TreeNode<T> clone() throws CloneNotSupportedException {
            TreeNode<T> clone = new TreeNode<>(this.content);
            clone.isExpand = this.isExpand;
            return clone;
        }
    }


    //interface
    public interface LayoutItemType {
        int getLayoutId();
    }


    // Tree View Adapter

    public static class TreeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final String KEY_IS_EXPAND = "IS_EXPAND";
        private final List<? extends TreeViewBinder> viewBinders;
        private List<TreeNode> displayNodes;
        private int padding = 30;
        private OnTreeNodeListener onTreeNodeListener;
        private boolean toCollapseChild;

        public TreeViewAdapter(List<? extends TreeViewBinder> viewBinders) {
            this(null, viewBinders);
        }

        public TreeViewAdapter(List<TreeNode> nodes, List<? extends TreeViewBinder> viewBinders) {
            displayNodes = new ArrayList<>();
            if (nodes != null)
                findDisplayNodes(nodes);
            this.viewBinders = viewBinders;
        }

        private void findDisplayNodes(List<TreeNode> nodes) {
            for (TreeNode node : nodes) {
                displayNodes.add(node);
                if (!node.isLeaf() && node.isExpand())
                    findDisplayNodes(node.getChildList());
            }
        }

        @Override
        public int getItemViewType(int position) {
            return displayNodes.get(position).getContent().getLayoutId();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(viewType, parent, false);
            if (viewBinders.size() == 1)
                return viewBinders.get(0).provideViewHolder(v);
            for (TreeViewBinder viewBinder : viewBinders) {
                if (viewBinder.getLayoutId() == viewType)
                    return viewBinder.provideViewHolder(v);
            }
            return viewBinders.get(0).provideViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            if (payloads != null && !payloads.isEmpty()) {
                Bundle b = (Bundle) payloads.get(0);
                for (String key : b.keySet()) {
                    switch (key) {
                        case KEY_IS_EXPAND:
                            if (onTreeNodeListener != null)
                                onTreeNodeListener.onToggle(b.getBoolean(key), holder);
                            break;
                    }
                }
            }
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setPaddingRelative(displayNodes.get(position).getHeight() * (padding + 20), 3, 3, 3);

            final TextView txt = holder.itemView.findViewById(R.id.tv_name);

//                txt.setTextColor(textColor);
//                holder.itemView.setBackgroundColor(backgroundColor);

            final String clickedPath[] = {""};

            holder.itemView.setOnClickListener(v -> {
                TreeNode selectedNode = displayNodes.get(holder.getLayoutPosition());
                // Prevent multi-click during the short interval.
                try {
                    long lastClickTime = (long) holder.itemView.getTag();
                    if (System.currentTimeMillis() - lastClickTime < 500)
                        return;
                } catch (Exception e) {
                    holder.itemView.setTag(System.currentTimeMillis());
                }
                holder.itemView.setTag(System.currentTimeMillis());

                try {
                    Dir dirNode = (Dir) selectedNode.getContent();
                    clickedPath[0] = dirNode.dirName;
                } catch (Exception e) {
                    File fileNode = (File) selectedNode.getContent();
                    clickedPath[0] = fileNode.fileName;
                }

                if (onTreeNodeListener != null && onTreeNodeListener.onClick(clickedPath[0],
                        selectedNode, holder))
                    return;
                if (selectedNode.isLeaf())
                    return;
                // This TreeNode was locked to click.
                if (selectedNode.isLocked()) return;
                boolean isExpand = selectedNode.isExpand();
                int positionStart = displayNodes.indexOf(selectedNode) + 1;
                if (!isExpand) {
                    notifyItemRangeInserted(positionStart, addChildNodes(selectedNode, positionStart));
                } else {
                    notifyItemRangeRemoved(positionStart, removeChildNodes(selectedNode, true));
                }

            });


            holder.itemView.setOnLongClickListener(v -> {
                TreeNode selectedNode = displayNodes.get(holder.getLayoutPosition());

                try {
                    Dir dirNode = (Dir) selectedNode.getContent();
                    clickedPath[0] = dirNode.dirName;
                } catch (Exception e) {
                    File fileNode = (File) selectedNode.getContent();
                    clickedPath[0] = fileNode.fileName;
                }

                onTreeNodeListener.onLongClick(clickedPath[0]);


                return true;
            });


            for (TreeViewBinder viewBinder : viewBinders) {
                if (viewBinder.getLayoutId() == displayNodes.get(position).getContent().getLayoutId())
                    viewBinder.bindView(holder, position, displayNodes.get(position));
            }
        }

        private int addChildNodes(TreeNode pNode, int startIndex) {
            List<TreeNode> childList = pNode.getChildList();
            int addChildCount = 0;
            for (TreeNode treeNode : childList) {
                displayNodes.add(startIndex + addChildCount++, treeNode);
                if (treeNode.isExpand()) {
                    addChildCount += addChildNodes(treeNode, startIndex + addChildCount);
                }
            }
            if (!pNode.isExpand())
                pNode.toggle();
            return addChildCount;
        }

        private int removeChildNodes(TreeNode pNode) {
            return removeChildNodes(pNode, true);
        }

        private int removeChildNodes(TreeNode pNode, boolean shouldToggle) {
            if (pNode.isLeaf())
                return 0;
            List<TreeNode> childList = pNode.getChildList();
            int removeChildCount = childList.size();
            displayNodes.removeAll(childList);
            for (TreeNode child : childList) {
                if (child.isExpand()) {
                    if (toCollapseChild)
                        child.toggle();
                    removeChildCount += removeChildNodes(child, false);
                }
            }
            if (shouldToggle)
                pNode.toggle();
            return removeChildCount;
        }

        @Override
        public int getItemCount() {
            return displayNodes == null ? 0 : displayNodes.size();
        }

        public void setPadding(int padding) {
            this.padding = padding;
        }

        public void ifCollapseChildWhileCollapseParent(boolean toCollapseChild) {
            this.toCollapseChild = toCollapseChild;
        }

        public void setOnTreeNodeListener(OnTreeNodeListener onTreeNodeListener) {
            this.onTreeNodeListener = onTreeNodeListener;
        }

        public interface OnTreeNodeListener {
            /**
             * called when TreeNodes were clicked.
             *
             * @return weather consume the click event.
             */
            boolean onClick(String clickedPath, TreeNode node, RecyclerView.ViewHolder holder);

            /**
             * called when TreeNodes were toggle.
             *
             * @param isExpand the status of TreeNodes after being toggled.
             */
            void onToggle(boolean isExpand, RecyclerView.ViewHolder holder);


            //long clickedPath
            void onLongClick(String clickedPath);
        }

        public void refresh(List<TreeNode> treeNodes) {
            displayNodes.clear();
            findDisplayNodes(treeNodes);
            notifyDataSetChanged();
        }

        public Iterator<TreeNode> getDisplayNodesIterator() {
            return displayNodes.iterator();
        }

        private void notifyDiff(final List<TreeNode> temp) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return temp.size();
                }

                @Override
                public int getNewListSize() {
                    return displayNodes.size();
                }

                // judge if the same items
                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return TreeViewAdapter.this.areItemsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
                }

                // if they are the same items, whether the contents has bean changed.
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return TreeViewAdapter.this.areContentsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
                }

                @Nullable
                @Override
                public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                    return TreeViewAdapter.this.getChangePayload(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
                }
            });
            diffResult.dispatchUpdatesTo(this);
        }

        private Object getChangePayload(TreeNode oldNode, TreeNode newNode) {
            Bundle diffBundle = new Bundle();
            if (newNode.isExpand() != oldNode.isExpand()) {
                diffBundle.putBoolean(KEY_IS_EXPAND, newNode.isExpand());
            }
            if (diffBundle.size() == 0)
                return null;
            return diffBundle;
        }

        // For DiffUtil, if they are the same items, whether the contents has bean changed.
        private boolean areContentsTheSame(TreeNode oldNode, TreeNode newNode) {
            return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent())
                    && oldNode.isExpand() == newNode.isExpand();
        }

        // judge if the same item for DiffUtil
        private boolean areItemsTheSame(TreeNode oldNode, TreeNode newNode) {
            return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent());
        }

        /**
         * collapse all root nodes.
         */
        public void collapseAll() {
            // Back up the nodes are displaying.
            List<TreeNode> temp = backupDisplayNodes();
            //find all root nodes.
            List<TreeNode> roots = new ArrayList<>();
            for (TreeNode displayNode : displayNodes) {
                if (displayNode.isRoot())
                    roots.add(displayNode);
            }
            //Close all root nodes.
            for (TreeNode root : roots) {
                if (root.isExpand())
                    removeChildNodes(root);
            }
            notifyDiff(temp);
        }

        @NonNull
        private List<TreeNode> backupDisplayNodes() {
            List<TreeNode> temp = new ArrayList<>();
            for (TreeNode displayNode : displayNodes) {
                try {
                    temp.add(displayNode.clone());
                } catch (CloneNotSupportedException e) {
                    temp.add(displayNode);
                }
            }
            return temp;
        }

        public void collapseNode(TreeNode pNode) {
            List<TreeNode> temp = backupDisplayNodes();
            removeChildNodes(pNode);
            notifyDiff(temp);
        }

        public void collapseBrotherNode(TreeNode pNode) {
            List<TreeNode> temp = backupDisplayNodes();
            if (pNode.isRoot()) {
                List<TreeNode> roots = new ArrayList<>();
                for (TreeNode displayNode : displayNodes) {
                    if (displayNode.isRoot())
                        roots.add(displayNode);
                }
                //Close all root nodes.
                for (TreeNode root : roots) {
                    if (root.isExpand() && !root.equals(pNode))
                        removeChildNodes(root);
                }
            } else {
                TreeNode parent = pNode.getParent();
                if (parent == null)
                    return;
                List<TreeNode> childList = parent.getChildList();
                for (TreeNode node : childList) {
                    if (node.equals(pNode) || !node.isExpand())
                        continue;
                    removeChildNodes(node);
                }
            }
            notifyDiff(temp);
        }

    }


    // Tree View Binder

    public static abstract class TreeViewBinder<VH extends RecyclerView.ViewHolder> implements LayoutItemType {
        public abstract VH provideViewHolder(View itemView);

        public abstract void bindView(VH holder, int position, TreeNode node);

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View rootView) {
                super(rootView);
            }

            protected <T extends View> T findViewById(@IdRes int id) {
                return (T) itemView.findViewById(id);
            }
        }

    }


    public static class FileNodeBinder extends TreeViewBinder<FileNodeBinder.ViewHolder> {
        @Override
        public ViewHolder provideViewHolder(View itemView) {
            return new ViewHolder(itemView);
        }

        @Override
        public void bindView(ViewHolder holder, int position, TreeNode node) {

            File fileNode = (File) node.getContent();
            if (isPath) {
                holder.tvName.setText(Uri.parse(fileNode.fileName).getLastPathSegment());
            } else {
                holder.tvName.setText(fileNode.fileName);
            }

            ImageView imageView = holder.itemView.findViewById(R.id.imageview1);
            var fileIconUtil = new FileIconUtil(holder.tvName.getText().toString(), "");
            fileIconUtil.bindFileIcon(imageView);

            setColor(holder.tvName, null);
        }

        @Override
        public int getLayoutId() {
            return R.layout.layout_file_list_item;
        }

        public class ViewHolder extends TreeViewBinder.ViewHolder {
            public TextView tvName;

            public ViewHolder(View rootView) {
                super(rootView);
                this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
            }

        }
    }


    public static class DirectoryNodeBinder extends TreeViewBinder<DirectoryNodeBinder.ViewHolder> {
        @Override
        public ViewHolder provideViewHolder(View itemView) {
            return new ViewHolder(itemView);
        }

        @Override
        public void bindView(ViewHolder holder, int position, TreeNode node) {

//            holder.ivArrow.setRotation(0);
            if (darkMode) {
                holder.ivArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
            } else {
                holder.ivArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
            }
//            int rotateDegree = node.isExpand() ? 90 : 0;
//            holder.ivArrow.setRotation(rotateDegree);
            int img = node.isExpand() ? R.drawable.ic_baseline_keyboard_arrow_down_24 : R.drawable.ic_baseline_keyboard_arrow_right_24;
            holder.ivArrow.setImageResource(img);
            Dir dirNode = (Dir) node.getContent();

            if (isPath) {
                holder.tvName.setText(Uri.parse(dirNode.dirName).getLastPathSegment());
            } else {
                holder.tvName.setText(dirNode.dirName);
            }

            if (node.isLeaf())
                holder.ivArrow.setVisibility(View.INVISIBLE);
            else holder.ivArrow.setVisibility(View.VISIBLE);

            setColor(holder.tvName, holder.ivArrow);
        }

        @Override
        public int getLayoutId() {
            return R.layout.layout_directory_list_item;
        }

        public static class ViewHolder extends TreeViewBinder.ViewHolder {
            private ImageView ivArrow;
            private TextView tvName;
            private ImageView imageView;

            public ViewHolder(View rootView) {
                super(rootView);
                this.ivArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
                this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
                this.imageView = (ImageView) rootView.findViewById(R.id.imageview2);
            }

            public ImageView getIvArrow() {
                return ivArrow;
            }

            public TextView getTvName() {
                return tvName;
            }
        }
    }


    public static class Dir implements TreeView.LayoutItemType {
        public String dirName;

        public Dir(String dirName) {
            this.dirName = dirName;
        }

        @Override
        public int getLayoutId() {
            return R.layout.layout_directory_list_item;
        }
    }


    public static class File implements TreeView.LayoutItemType {
        public String fileName;

        public File(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public int getLayoutId() {
            return R.layout.layout_file_list_item;
        }
    }

    private static void setColor(TextView textview, ImageView imageView) {
        if (PreferencesManager.isFollowEditorThemeEnabled()) {
            var color = getEditorColorScheme().getColor(EditorColorScheme.TEXT_NORMAL);
            textview.setTextColor(color);
            if (imageView != null) imageView.setColorFilter(color);
        }
    }

    private static EditorColorScheme getEditorColorScheme() {
        return MainActivity.getEditorColorScheme();
    }
}
