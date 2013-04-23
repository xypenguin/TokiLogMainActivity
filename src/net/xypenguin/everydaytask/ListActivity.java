package net.xypenguin.everydaytask;

import java.util.ArrayList;
import java.util.List;

import net.xypenguin.everydaytask.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ListActivity extends Activity implements OnItemClickListener,
		OnClickListener {
	private RelativeLayout layout;
	private List<ListItem> list;
	private TaskArrayAdapter adapter;
	private ListView listView;
	private EditText editText; // 新規作成ダイアログ用
	private int dialogNumber; // ダイアログの区別のための数字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		// コンポーネントの関連付け
		layout = (RelativeLayout) this.findViewById(R.id.main_layout);
		listView = (ListView) this.findViewById(R.id.listView1);

		// リストビューのスクロールキャッシュ無効（色がおかしくなるので）
		listView.setScrollingCacheEnabled(false);

		// リスナーの設定
		listView.setOnItemClickListener(this);

		// アダプター作成
		adapter = new TaskArrayAdapter(this, setItem());

		// リストビューに項目データ（アダプタ）をセット
		listView.setAdapter(adapter);

		// 何もタスクが無いのなら新規作成のダイアログが開く
		int i = 0;
		editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		for (int j = 0; j < 10; j++) {
			i += adapter.getList().get(j).getTask().length();
			if (j == 9 && i == 0) {
				showYesDialog(ListActivity.this, "何かタスクを作ってみよう",
						"あなたがよく行う習慣を登録して時間を計りましょう", editText, this, 1);
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		num2bg(pref.getInt("bgNum", 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO 自動生成されたメソッド・スタブ
		getMenuInflater().inflate(R.menu.menu_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ
		switch (item.getItemId()) {
		// 新規作成
		case R.id.item_edit:
			editText = new EditText(this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			for (int i = 0; i < 10; i++) {
				// 開いてるリストアイテムがあるかチェック
				if (adapter.getList().get(i).getTask().length() == 0) {
					showYesDialog(ListActivity.this, "新規作成",
							"新しいタスク名を入力してください", editText, this, 0);
					break;
				} else if (adapter.getList().get(i).getTask().length() != 0
						&& i == 9) {
					showDialog(this, "タスクは10個までしか登録できないよ", "使わないタスクを削除してね");
					break;
				}
			}
			break;
		case R.id.item_credit:
			// 明示的なインテントの生成
			Intent intent = new Intent(this,
					net.xypenguin.everydaytask.CreditActivity.class);
			// アクティビティの呼び出し
			startActivity(intent);
			break;
		case R.id.item_main:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.item_sort:
			showListDialog(this, "並べ替えの方法", this, 2);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// リストアイテムをタッチしたとき
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO 自動生成されたメソッド・スタブ
		ListView listView = (ListView) parent;
		ListItem item = (ListItem) listView.getItemAtPosition(position);
		// タスク名が無いアイテムをタッチすると新規作成
		if (item.getTask().length() < 1) {
			editText = new EditText(this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			showYesDialog(ListActivity.this, "新規作成", "新しいタスク名を入力してください",
					editText, this, 0);
			// タスク名があるアイテムをタッチするとその情報をmain渡して自身のアクティビティは閉じる
		} else {
			Intent intent = new Intent(this,
					net.xypenguin.everydaytask.MainActivity.class);
			intent.putExtra("task", item.getTask());
			intent.putExtra("totalTime", item.getTotalTime());
			intent.putExtra("count", item.getCount());
			intent.putExtra("number", position);
			SharedPreferences pref = getSharedPreferences("task" + position,
					MODE_PRIVATE);
			intent.putExtra("date", pref.getString("date", ""));
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}

	// 作成ダイアログ
	// 分岐あり　作成ボタンがリスナー、やめるボタンは何もしない
	private void showYesDialog(Context context, String title, String message,
			EditText editText, OnClickListener listener, int dialogNumber) {
		this.dialogNumber = dialogNumber;
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setView(editText);
		ad.setPositiveButton("作成", listener);
		ad.setNegativeButton("やめる", null);
		ad.show();
	}

	// 確認ダイアログ 分岐無し リスナーあり
	private void showOkDialog(Context context, String title, String message,
			OnClickListener listener, int dialogNumber) {
		this.dialogNumber = dialogNumber;
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("OK", listener);
		ad.show();
	}

	// 確認ダイアログ 分岐なし リスナー無し
	private static void showDialog(Context context, String title, String message) {
		AlertDialog.Builder ad = new Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("OK", null);
		ad.show();
	}

	// リストダイアログ 分岐あり
	private void showListDialog(Context context, String title,
			OnClickListener listener, int dialogNumber) {
		this.dialogNumber = dialogNumber;
		AlertDialog.Builder ad = new Builder(context);
		ad.setCancelable(true);
		ad.setTitle(title);
		String[] sortItems = { "上へ詰める", "回数が多い順", "時間が長い順" };
		ad.setItems(sortItems, listener);
		ad.show();
	}

	// 新規作成ダイアログの作成ボタンクリック時
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO 自動生成されたメソッド・スタブ
		switch (dialogNumber) {
		case 0:
		case 1:
			for (int i = 0; i < 10; i++) {
				// 名前が入っていないアイテムを上から探して追加する
				if (adapter.getList().get(i).getTask().length() == 0) {
					// 何もタスクが無いとき、名前を入力せずに作成ボタンを押すと勝手に名前をつける
					if (dialogNumber == 1
							&& editText.getText().toString().length() == 0) {
						editText.setText("新しいタスク");
					}
					adapter.getList().get(i)
							.setTask(editText.getText().toString());
					String justTime = justNow();
					adapter.getList().get(i).setDate(justTime);

					// 追加した時点でタスク名をプリファレンスに追加する
					SharedPreferences pref = getSharedPreferences("task" + i,
							MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					editor.putString("task", editText.getText().toString());
					editor.putInt("number", i);
					editor.putString("date", justTime);
					editor.commit();
					break;
				}
			}
			adapter = new TaskArrayAdapter(this, setItem());
			listView.setAdapter(adapter);

			break;
		case 2:
			if (which == 0) {
				sortTopAlignment();
			} else if (which == 1) {
				sortCountOrder();
			} else if (which == 2) {
				sortTimeOrder();
			}
			break;
		default:
			break;
		}
	}

	// 名前の無いリストアイテムを上に詰めるメソッド
	private void sortTopAlignment() {
		for (int i = 0; i < 10; i++) {
			// 名前が入っていないアイテムを上から探して
			if (adapter.getList().get(i).getTask().length() == 0) {
				for (int j = i; j < 10; j++) {
					// 名前が入っているアイテムを見つけて名前の無いアイテムに代入して
					if (1 <= adapter.getList().get(j).getTask().length()) {
						adapter.getList().get(i)
								.setTask(adapter.getList().get(j).getTask());
						adapter.getList()
								.get(i)
								.setTotalTime(
										adapter.getList().get(j).getTotalTime());
						adapter.getList().get(i)
								.setCount(adapter.getList().get(j).getCount());
						adapter.getList().get(i)
								.setDate(adapter.getList().get(j).getDate());
						// 代入元は初期化
						adapter.getList().get(j).setTask("");
						adapter.getList().get(j).setTotalTime(0);
						adapter.getList().get(j).setCount(0);
						adapter.getList().get(j).setDate("");
						break;
					}
				}
			}
		}
		saveList2Preferences();
		adapter = new TaskArrayAdapter(this, setItem());
		listView.setAdapter(adapter);
	}

	// リストアイテムを回数が多い順に並べ替える
	private void sortCountOrder() {
		sortTopAlignment();
		for (int i = 0; i < 10; i++) {
			for (int j = i + 1; j < 10; j++) {
				// 前から回数を確認して順番が後ろの値の方が回数が大きいなら
				if (adapter.getList().get(i).getCount() < adapter.getList()
						.get(j).getCount()) {
					// 一旦、保持する変数
					String task = adapter.getList().get(i).getTask();
					long totalTime = adapter.getList().get(i).getTotalTime();
					int count = adapter.getList().get(i).getCount();
					String date = adapter.getList().get(i).getDate();
					// 順番が後の値を前に移す
					adapter.getList().get(i)
							.setTask(adapter.getList().get(j).getTask());
					adapter.getList()
							.get(i)
							.setTotalTime(
									adapter.getList().get(j).getTotalTime());
					adapter.getList().get(i)
							.setCount(adapter.getList().get(j).getCount());
					adapter.getList().get(i)
							.setDate(adapter.getList().get(j).getDate());
					// 一旦、保持していた値を後ろに移す
					adapter.getList().get(j).setTask(task);
					adapter.getList().get(j).setTotalTime(totalTime);
					adapter.getList().get(j).setCount(count);
					adapter.getList().get(j).setDate(date);
				}
			}
		}
		saveList2Preferences();
		adapter = new TaskArrayAdapter(this, setItem());
		listView.setAdapter(adapter);
	}

	// リストアイテムを累計時間が多い順に並べる
	private void sortTimeOrder() {
		sortTopAlignment();
		for (int i = 0; i < 10; i++) {
			for (int j = i + 1; j < 10; j++) {
				// 前から累計時間を確認して順番が後ろの値の方が大きいなら
				if (adapter.getList().get(i).getTotalTime() < adapter.getList()
						.get(j).getTotalTime()) {
					// 一旦、保持する変数
					String task = adapter.getList().get(i).getTask();
					long totalTime = adapter.getList().get(i).getTotalTime();
					int count = adapter.getList().get(i).getCount();
					String date = adapter.getList().get(i).getDate();
					// 順番が後の値を前に移す
					adapter.getList().get(i)
							.setTask(adapter.getList().get(j).getTask());
					adapter.getList()
							.get(i)
							.setTotalTime(
									adapter.getList().get(j).getTotalTime());
					adapter.getList().get(i)
							.setCount(adapter.getList().get(j).getCount());
					adapter.getList().get(i)
							.setDate(adapter.getList().get(j).getDate());
					// 一旦、保持していた値を後ろに移す
					adapter.getList().get(j).setTask(task);
					adapter.getList().get(j).setTotalTime(totalTime);
					adapter.getList().get(j).setCount(count);
					adapter.getList().get(j).setDate(date);
				}
			}
		}
		saveList2Preferences();
		adapter = new TaskArrayAdapter(this, setItem());
		listView.setAdapter(adapter);
	}

	// プリファレンスから各項目の値を取得してセット
	private List<ListItem> setItem() {
		// リスト項目の作成（10件）
		list = new ArrayList<ListItem>();
		for (int i = 0; i < 10; i++) {
			String fileName = "task" + i;
			// プリファレンスから各項目の値を取得してセット
			SharedPreferences pref = getSharedPreferences(fileName,
					MODE_PRIVATE);
			String task = pref.getString("task", "");
			long totalTime = pref.getLong("totalTime", 0);
			int count = pref.getInt("count", 0);
			String date = pref.getString("date", "");
			ListItem item = new ListItem(task, totalTime, count, date);
			list.add(item);
		}
		return list;
	}

	// リストの全アイテムをプリファレンスに保存
	private void saveList2Preferences() {
		for (int i = 0; i < 10; i++) {
			SharedPreferences pref = getSharedPreferences("task" + i,
					MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("task", adapter.getList().get(i).getTask());
			editor.putLong("totalTime", adapter.getList().get(i).getTotalTime());
			editor.putInt("count", adapter.getList().get(i).getCount());
			editor.putInt("number", i);
			editor.putString("date", adapter.getList().get(i).getDate());
			editor.commit();
		}
	}

	// 現在の日時を取得する
	private String justNow() {
		Time time = new Time("Asia/Tokyo");
		time.setToNow();
		String date = time.year + "年" + (time.month + 1) + "月" + time.monthDay
				+ "日" + time.hour + ":" + time.minute + ":" + time.second;
		return date;
	}

	// bgNumを受け取って背景を変更するメソッド
	private void num2bg(int bgNum) {
		switch (bgNum) {
		case 0:
			layout.setBackgroundResource(R.drawable.bg_01);
			break;
		case 1:
			layout.setBackgroundResource(R.drawable.bg_02);
			break;
		case 2:
			layout.setBackgroundResource(R.drawable.bg_03);
			break;
		case 3:
			layout.setBackgroundResource(R.drawable.bg_04);
			break;
		default:
			break;
		}
	}
}
