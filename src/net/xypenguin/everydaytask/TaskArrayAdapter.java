package net.xypenguin.everydaytask;

import java.util.List;

import net.xypenguin.everydaytask.R;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
/*
 * ���X�g�r���[�ɐڑ�����A�_�v�^�i�Ǝ��N���X�j
 */
public class TaskArrayAdapter extends ArrayAdapter<ListItem> {
	private List<ListItem> list;
	LinearLayout layoutDate;
	
	public TaskArrayAdapter(Context context,List<ListItem> list) {
		super(context, -1, list);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		this.list = list;
	}
	
	//�v�f�r���[����������鎞�ɌĂ΂��
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		//�v�f�r���[�̏��null�̎��̓��C�A�E�g�𐶐�
		if(convertView == null){
			//�R���e�L�X�g���烌�C�A�E�g�C���t���[�^�[���擾
			LayoutInflater inflater = LayoutInflater.from(getContext());
			
			//�v�f�r���[��XML���C�A�E�g��`���Z�b�g
			convertView = inflater.inflate(R.layout.list, null);
			
			//���C�A�E�g���r���[���擾
			TextView task = (TextView)convertView.findViewById(R.id.text_task);
			TextView totalTime = (TextView)convertView.findViewById(R.id.text_totaltime);
			TextView count = (TextView)convertView.findViewById(R.id.text_count);
			TextView date = (TextView)convertView.findViewById(R.id.text_date);
			layoutDate = (LinearLayout)convertView.findViewById(R.id.layout_date);
			
			//�ė��p�̂��߁A�r���[�̃C���X�^���X���z���_�[�֊i�[
			holder = new ViewHolder();
			holder.task = task;
			holder.totalTime = totalTime;
			holder.count = count;
			holder.date = date;
			
			//�^�O�Ƃ��ēo�^
			convertView.setTag(holder);
		}else{
			//�r���[���ė��p
			holder = (ViewHolder)convertView.getTag();
		}
		//���X�g�|�W�V�����ɊY������f�[�^���擾
		ListItem item = list.get(position);
		
		//�f�[�^�ݒ�
		holder.task.setText(item.getTask());
		holder.totalTime.setText(long2Time(item.getTotalTime()));
		holder.count.setText(Integer.toString(item.getCount()));
		holder.date.setText(item.getDate());
		
		if (item.getDate().equals("") == false) {
			layoutDate.setVisibility(View.VISIBLE);
		}else{
			layoutDate.setVisibility(View.GONE);
		}
		
		//�v�f�r���[��Ԃ�
		return convertView;
	}
	
	public List<ListItem> getList(){
		return list;
	}
	
	public void setList(List<ListItem> list){
		this.list = list;
	}
	
	private static class ViewHolder{
		TextView task;
		TextView totalTime;
		TextView count;
		TextView date;
	}
	
	//�~���b�����ԕ\�L�ɂ��郁�\�b�h
	private static String long2Time(long time){
		int s = (int) ((time % (1000 * 60)) / 1000);
		int m = (int) (time % (1000 * 60 * 60) / (1000 * 60));
		int h = (int) (time / (1000 * 60 * 60));
		
		return String.format("%02d:%02d:%02d",h,m,s);
	}
}
