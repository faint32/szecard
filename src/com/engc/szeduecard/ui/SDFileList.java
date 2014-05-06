package com.engc.szeduecard.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.engc.szeduecard.R;

/**
 * sd卡存储 列表
 * 
 * @ClassName: SDFileList
 * @Description: TODO
 * @author wutao
 * @date 2013-12-18 下午2:24:21
 * 
 */
public class SDFileList extends BaseActivity {
	ListView listFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist);
		initView();
		initData();
		listFile.setAdapter(new ArrayAdapter<Map<String, Object>>(this,
				android.R.layout.simple_expandable_list_item_1, initData()));

	}

	private void initView() {
		listFile = (ListView) findViewById(R.id.lvfile);

	}

	private List<Map<String, Object>> initData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sDStateString = Environment.getExternalStorageState();
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File SDFile = Environment.getExternalStorageDirectory();
				File sdPath = new File(SDFile.getAbsolutePath());
				if (sdPath.listFiles().length > 0) {
					for (File file : sdPath.listFiles()) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("text_content", file.getName()); // get filename
						list.add(map);
					}
				}
			} catch (Exception e) {
				// ...
			}

		

		}
		return list;

	}

}
