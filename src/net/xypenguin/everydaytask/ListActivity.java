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
	private EditText editText; // �V�K�쐬�_�C�A���O�p
	private int dialogNumber; // �_�C�A���O�̋�ʂ̂��߂̐���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		// �R���|�[�l���g�̊֘A�t��
		layout = (RelativeLayout) this.findViewById(R.id.main_layout);
		listView = (ListView) this.findViewById(R.id.listView1);

		// ���X�g�r���[�̃X�N���[���L���b�V�������i�F�����������Ȃ�̂Łj
		listView.setScrollingCacheEnabled(false);

		// ���X�i�[�̐ݒ�
		listView.setOnItemClickListener(this);

		// �A�_�v�^�[�쐬
		adapter = new TaskArrayAdapter(this, setItem());

		// ���X�g�r���[�ɍ��ڃf�[�^�i�A�_�v�^�j���Z�b�g
		listView.setAdapter(adapter);

		// �����^�X�N�������̂Ȃ�V�K�쐬�̃_�C�A���O���J��
		int i = 0;
		editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		for (int j = 0; j < 10; j++) {
			i += adapter.getList().get(j).getTask().length();
			if (j == 9 && i == 0) {
				showYesDialog(ListActivity.this, "�����^�X�N������Ă݂悤",
						"���Ȃ����悭�s���K����o�^���Ď��Ԃ��v��܂��傤", editText, this, 1);
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onResume();
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		num2bg(pref.getInt("bgNum", 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		getMenuInflater().inflate(R.menu.menu_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		switch (item.getItemId()) {
		// �V�K�쐬
		case R.id.item_edit:
			editText = new EditText(this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			for (int i = 0; i < 10; i++) {
				// �J���Ă郊�X�g�A�C�e�������邩�`�F�b�N
				if (adapter.getList().get(i).getTask().length() == 0) {
					showYesDialog(ListActivity.this, "�V�K�쐬",
							"�V�����^�X�N������͂��Ă�������", editText, this, 0);
					break;
				} else if (adapter.getList().get(i).getTask().length() != 0
						&& i == 9) {
					showDialog(this, "�^�X�N��10�܂ł����o�^�ł��Ȃ���", "�g��Ȃ��^�X�N���폜���Ă�");
					break;
				}
			}
			break;
		case R.id.item_credit:
			// �����I�ȃC���e���g�̐���
			Intent intent = new Intent(this,
					net.xypenguin.everydaytask.CreditActivity.class);
			// �A�N�e�B�r�e�B�̌Ăяo��
			startActivity(intent);
			break;
		case R.id.item_main:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.item_sort:
			showListDialog(this, "���בւ��̕��@", this, 2);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// ���X�g�A�C�e�����^�b�`�����Ƃ�
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		ListView listView = (ListView) parent;
		ListItem item = (ListItem) listView.getItemAtPosition(position);
		// �^�X�N���������A�C�e�����^�b�`����ƐV�K�쐬
		if (item.getTask().length() < 1) {
			editText = new EditText(this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			showYesDialog(ListActivity.this, "�V�K�쐬", "�V�����^�X�N������͂��Ă�������",
					editText, this, 0);
			// �^�X�N��������A�C�e�����^�b�`����Ƃ��̏���main�n���Ď��g�̃A�N�e�B�r�e�B�͕���
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

	// �쐬�_�C�A���O
	// ���򂠂�@�쐬�{�^�������X�i�[�A��߂�{�^���͉������Ȃ�
	private void showYesDialog(Context context, String title, String message,
			EditText editText, OnClickListener listener, int dialogNumber) {
		this.dialogNumber = dialogNumber;
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setView(editText);
		ad.setPositiveButton("�쐬", listener);
		ad.setNegativeButton("��߂�", null);
		ad.show();
	}

	// �m�F�_�C�A���O ���򖳂� ���X�i�[����
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

	// �m�F�_�C�A���O ����Ȃ� ���X�i�[����
	private static void showDialog(Context context, String title, String message) {
		AlertDialog.Builder ad = new Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("OK", null);
		ad.show();
	}

	// ���X�g�_�C�A���O ���򂠂�
	private void showListDialog(Context context, String title,
			OnClickListener listener, int dialogNumber) {
		this.dialogNumber = dialogNumber;
		AlertDialog.Builder ad = new Builder(context);
		ad.setCancelable(true);
		ad.setTitle(title);
		String[] sortItems = { "��֋l�߂�", "�񐔂�������", "���Ԃ�������" };
		ad.setItems(sortItems, listener);
		ad.show();
	}

	// �V�K�쐬�_�C�A���O�̍쐬�{�^���N���b�N��
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		switch (dialogNumber) {
		case 0:
		case 1:
			for (int i = 0; i < 10; i++) {
				// ���O�������Ă��Ȃ��A�C�e�����ォ��T���Ēǉ�����
				if (adapter.getList().get(i).getTask().length() == 0) {
					// �����^�X�N�������Ƃ��A���O����͂����ɍ쐬�{�^���������Ə���ɖ��O������
					if (dialogNumber == 1
							&& editText.getText().toString().length() == 0) {
						editText.setText("�V�����^�X�N");
					}
					adapter.getList().get(i)
							.setTask(editText.getText().toString());
					String justTime = justNow();
					adapter.getList().get(i).setDate(justTime);

					// �ǉ��������_�Ń^�X�N�����v���t�@�����X�ɒǉ�����
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

	// ���O�̖������X�g�A�C�e������ɋl�߂郁�\�b�h
	private void sortTopAlignment() {
		for (int i = 0; i < 10; i++) {
			// ���O�������Ă��Ȃ��A�C�e�����ォ��T����
			if (adapter.getList().get(i).getTask().length() == 0) {
				for (int j = i; j < 10; j++) {
					// ���O�������Ă���A�C�e���������Ė��O�̖����A�C�e���ɑ������
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
						// ������͏�����
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

	// ���X�g�A�C�e�����񐔂��������ɕ��בւ���
	private void sortCountOrder() {
		sortTopAlignment();
		for (int i = 0; i < 10; i++) {
			for (int j = i + 1; j < 10; j++) {
				// �O����񐔂��m�F���ď��Ԃ����̒l�̕����񐔂��傫���Ȃ�
				if (adapter.getList().get(i).getCount() < adapter.getList()
						.get(j).getCount()) {
					// ��U�A�ێ�����ϐ�
					String task = adapter.getList().get(i).getTask();
					long totalTime = adapter.getList().get(i).getTotalTime();
					int count = adapter.getList().get(i).getCount();
					String date = adapter.getList().get(i).getDate();
					// ���Ԃ���̒l��O�Ɉڂ�
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
					// ��U�A�ێ����Ă����l�����Ɉڂ�
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

	// ���X�g�A�C�e����݌v���Ԃ��������ɕ��ׂ�
	private void sortTimeOrder() {
		sortTopAlignment();
		for (int i = 0; i < 10; i++) {
			for (int j = i + 1; j < 10; j++) {
				// �O����݌v���Ԃ��m�F���ď��Ԃ����̒l�̕����傫���Ȃ�
				if (adapter.getList().get(i).getTotalTime() < adapter.getList()
						.get(j).getTotalTime()) {
					// ��U�A�ێ�����ϐ�
					String task = adapter.getList().get(i).getTask();
					long totalTime = adapter.getList().get(i).getTotalTime();
					int count = adapter.getList().get(i).getCount();
					String date = adapter.getList().get(i).getDate();
					// ���Ԃ���̒l��O�Ɉڂ�
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
					// ��U�A�ێ����Ă����l�����Ɉڂ�
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

	// �v���t�@�����X����e���ڂ̒l���擾���ăZ�b�g
	private List<ListItem> setItem() {
		// ���X�g���ڂ̍쐬�i10���j
		list = new ArrayList<ListItem>();
		for (int i = 0; i < 10; i++) {
			String fileName = "task" + i;
			// �v���t�@�����X����e���ڂ̒l���擾���ăZ�b�g
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

	// ���X�g�̑S�A�C�e�����v���t�@�����X�ɕۑ�
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

	// ���݂̓������擾����
	private String justNow() {
		Time time = new Time("Asia/Tokyo");
		time.setToNow();
		String date = time.year + "�N" + (time.month + 1) + "��" + time.monthDay
				+ "��" + time.hour + ":" + time.minute + ":" + time.second;
		return date;
	}

	// bgNum���󂯎���Ĕw�i��ύX���郁�\�b�h
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
