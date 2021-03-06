package com.prpr894.cplayer.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.recycleradapters.CollectionBackupListRecyclerAdapter;
import com.prpr894.cplayer.base.BaseActivity;
import com.prpr894.cplayer.bean.CollectionBackupItemDataBean;
import com.prpr894.cplayer.bean.CollectionBackupListBean;
import com.prpr894.cplayer.greendao.gen.LiveRoomItemDataBeanDao;
import com.prpr894.cplayer.utils.DateUtils;
import com.prpr894.cplayer.utils.JsonStringUtil;
import com.prpr894.cplayer.utils.SPUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.MyToast;

import static com.prpr894.cplayer.utils.AppConfig.EXIT_NOTIFICATION_DIALOG;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE_BAI_DU;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE_SAO_ZI;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private RadioButton mRadioButtonBaiduPlayer, mRadioButtonJiaoZiPlayer;
    private CheckBox mCheckBoxExitDialog;
    private TextView mTextViewBackRead;
    private TextView mTextViewBackSave;

    String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "CPlayer/Collection/collections.json";
    File file;
    private CollectionBackupListBean mCollectionBackupBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        initData();
    }

    private void initData() {
        file = new File(filePath);
        if (file.exists()) {
            String str = JsonStringUtil.getJsonString(this, filePath);
            Log.d("flag", "本地获取的数据" + str);
            Gson gson = new Gson();
            mCollectionBackupBean = gson.fromJson(str, CollectionBackupListBean.class);
        } else {
            mCollectionBackupBean = new CollectionBackupListBean();
        }
    }

    private void initView() {
        getToolbarTitle().setText("设置");
        mRadioButtonBaiduPlayer = findViewById(R.id.rb_baidu_player);
        mRadioButtonJiaoZiPlayer = findViewById(R.id.rb_jiaozi_player);
        mCheckBoxExitDialog = findViewById(R.id.cb_show_exit_dialog);
        mRadioButtonBaiduPlayer.setOnCheckedChangeListener(this);
        mRadioButtonJiaoZiPlayer.setOnCheckedChangeListener(this);
        mCheckBoxExitDialog.setOnCheckedChangeListener(this);
        mTextViewBackRead=findViewById(R.id.tv_collection_backup_read);
        mTextViewBackRead.setOnClickListener(this);
        mTextViewBackSave=findViewById(R.id.tv_collection_backup_save);
        mTextViewBackSave.setOnClickListener(this);
        initSettings();
    }

    private void initSettings() {
        if (SPUtil.getBoolen(MyApp.getInstance(), EXIT_NOTIFICATION_DIALOG, true)) {
            mCheckBoxExitDialog.setChecked(true);
        } else {
            mCheckBoxExitDialog.setChecked(false);
        }
        switch (SPUtil.getString(MyApp.getInstance(), PLAY_TYPE, PLAY_TYPE_BAI_DU)) {
            case PLAY_TYPE_BAI_DU:
                mRadioButtonBaiduPlayer.setChecked(true);
                break;
            case PLAY_TYPE_SAO_ZI:
                mRadioButtonJiaoZiPlayer.setChecked(true);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_show_exit_dialog:
                mCheckBoxExitDialog.setChecked(isChecked);
                SPUtil.putBoolen(MyApp.getInstance(), EXIT_NOTIFICATION_DIALOG, isChecked);
                break;
            case R.id.rb_baidu_player:
                if (isChecked) {
                    SPUtil.putString(MyApp.getInstance(), PLAY_TYPE, PLAY_TYPE_BAI_DU);
                }
                break;
            case R.id.rb_jiaozi_player:
                if (isChecked) {
                    SPUtil.putString(MyApp.getInstance(), PLAY_TYPE, PLAY_TYPE_SAO_ZI);
                }
                break;

        }
    }

    /**
     * 将数据源以json格式保存到本地文件
     *
     * @param str
     */
    private void saveCollectionFile(String str) {
        try {
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
            MyToast.successBig("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCollectionDialog() {

        AlertDialog.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setCancelable(false);
        builder.setTitle("备份收藏");
        builder.setMessage("确定备份当前收藏夹的全部内容到内置SD吗？");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LiveRoomItemDataBeanDao dao = MyApp.getInstance().getDaoSession().getLiveRoomItemDataBeanDao();
                CollectionBackupItemDataBean bean = new CollectionBackupItemDataBean();
                bean.setBackupDate(DateUtils.getStrDate());
                bean.setData(dao.loadAll());
                List<CollectionBackupItemDataBean> data;
                if (mCollectionBackupBean.getData() == null) {
                    data = new ArrayList<>();
                } else {
                    data = mCollectionBackupBean.getData();
                }
                data.add(bean);
                mCollectionBackupBean.setData(data);
                Gson gson = new Gson();
                saveCollectionFile(gson.toJson(mCollectionBackupBean));
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    private void readCollectionDialog() {

        AlertDialog.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setCancelable(false);
        builder.setTitle("恢复备份");
        builder.setIcon(R.drawable.ic_backup);
        if (mCollectionBackupBean.getData() == null || mCollectionBackupBean.getData().size() <= 0) {
            builder.setMessage("提示：暂无备份。");
        } else {
            builder.setMessage("提示：点击条目然后选择恢复或删除。");
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialogl_collection_read, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_read_collection);
        final List<CollectionBackupItemDataBean> list = mCollectionBackupBean.getData();
        final CollectionBackupListRecyclerAdapter adapter = new CollectionBackupListRecyclerAdapter(list, SettingsActivity.this);
        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position != list.size() - 1) {
                    outRect.set(0, 0, 0, 10);
                }
            }
        };
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));
        builder.setView(view);
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.setNeutralButton("清空备份", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogPa, int which) {
                if (mCollectionBackupBean.getData() == null || mCollectionBackupBean.getData().size() <= 0) {
                    MyToast.errorBig("当前没有备份！");
                    return;
                }
                AlertDialog.Builder builder;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(SettingsActivity.this);
                }
                builder.setCancelable(false);
                builder.setTitle("警告");
                builder.setMessage("清空数据不可恢复，确定清空收藏备份吗？");
                builder.setPositiveButton("清空", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (file.delete()) {
                            mCollectionBackupBean.getData().clear();
                            MyToast.successBig("清除成功");
                        } else {
                            MyToast.errorBig("清除失败");
                        }
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });
        final AlertDialog dialog = builder.create();
        adapter.setOnRecyclerItemClickListener(new CollectionBackupListRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(final int position, CollectionBackupItemDataBean data, View view) {
                dialog.dismiss();
                AlertDialog.Builder builder;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(SettingsActivity.this);
                }
                builder.setCancelable(false);
                builder.setTitle("提示");
//                builder.setMessage("对于备份 " + data.getBackupDate() + "\n请选择你想执行的操作：");
                builder.setMessage(Html.fromHtml("对于备份 <font color='#47C4FC'>"+data.getBackupDate() + "</font><br>请选择你想执行的操作："));
                builder.setPositiveButton("恢复备份", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LiveRoomItemDataBeanDao dao = MyApp.getInstance().getDaoSession().getLiveRoomItemDataBeanDao();
                        dao.deleteAll();
                        dao.insertInTx(mCollectionBackupBean.getData().get(position).getData());
                        dialog.dismiss();
                        MyToast.successBig("恢复成功");
                    }
                });


                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        adapter.remove(position);
                        Gson gson = new Gson();
                        saveCollectionFile(gson.toJson(mCollectionBackupBean));
                        dialog.dismiss();
                        MyToast.successBig("删除成功");
                    }
                });

                builder.create().show();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_collection_backup_read:
                readCollectionDialog();
                break;
            case R.id.tv_collection_backup_save:
                saveCollectionDialog();
                break;
        }
    }
}
