package com.gjcar.activity.user;



import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.gjcar.app.R;
import com.gjcar.data.data.Public_Api;
import com.gjcar.data.data.Public_SP;
import com.gjcar.utils.NetworkHelper;
import com.gjcar.utils.SharedPreferenceHelper;
import com.gjcar.utils.ToastHelper;
import com.gjcar.view.dialog.SubmitDialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Update_NickName_Activity extends Activity{

	/*��ʼ���ؼ�*/
	private EditText nickname;

	private Button update_submit;

	/*Handler*/
	private Handler handler;

	private final static int UpdateOk = 1;//���Ͷ��ųɹ�
	private final static int UpdateFail = 2;//���Ͷ���ʧ��
	private final static int UpdateDataFail = 3;//���Ͷ�������ʧ��

	/*����*/
	private String errorMsg = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatenickname);
		System.out.println("0");
		initTitleBar();System.out.println("1");

		initView();System.out.println("2");

		initListener();System.out.println("3");

		initHandler();System.out.println("4");
		
		
	}

	private void initTitleBar() {
		ImageView iv = (ImageView)findViewById(R.id.iv_back);System.out.println("hn");
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
	}

	private void initView() {

		nickname = (EditText) findViewById(R.id.et_updatenickname);System.out.println("a");

		update_submit = (Button) findViewById(R.id.update_submit);System.out.println("b");
		
		String phone = SharedPreferenceHelper.getString(this, Public_SP.Account, "phone");System.out.println("c");
		String nickName = SharedPreferenceHelper.getString(this, Public_SP.Account, "nickName");System.out.println("d");
				
		if(nickName.equals(phone)){

			nickname.setText("");
		}else{
			
			nickname.setText(nickName);		
		}
	}
	
	private void initListener() {
		
		update_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				if(!checkNickNameMessage().equals("")){
					ToastHelper.showToastShort(Update_NickName_Activity.this, checkNickNameMessage());
					return;
				}
				
				if(!NetworkHelper.isNetworkAvailable(Update_NickName_Activity.this)){
					return;
				}
				
				SubmitDialog.showSubmitDialog(Update_NickName_Activity.this);
				
				new Thread(){
						public void run() {
							Update_NickName();
						};
				}.start();
			}
		});
		
	}

	private void initHandler() {

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {

					case UpdateOk:
						SubmitDialog.closeSubmitDialog();
						finish();
						break;
	
					case UpdateFail:
						SubmitDialog.closeSubmitDialog();
						ToastHelper.showToastLong(Update_NickName_Activity.this, msg
								.getData().getString("errorMsg"));//��ʾ����ʧ�ܵ�ԭ��
						break;
	
					case UpdateDataFail:
						SubmitDialog.closeSubmitDialog();
						ToastHelper.showSendDataFailToast(Update_NickName_Activity.this);
						break;
	
					default:
						break;
				}

			}
		};

	}
	
	private String checkNickNameMessage(){

		errorMsg = "";
		
		if(nickname.getText().toString().trim().equals("") || nickname.getText().toString().trim() == null){
			errorMsg = errorMsg +"�ǳƲ���Ϊ��";				
			return errorMsg;
		}
		
		if(nickname.getText().toString().length() > 12){
			errorMsg = errorMsg +"�ǳƳ��Ȳ��ܳ���12���ַ�";				
			return errorMsg;
		}
		
		return errorMsg;
	}
	
	/** ���ͳ�����Ϣ  */
	private void Update_NickName() {
		
		// ����Ĭ�ϵĿͻ���ʵ��  
		HttpClient httpCLient = new DefaultHttpClient();

		JSONObject jsonObject = new JSONObject(); //**********************ע��json��������ʱ��Ҫ����

		//��������https://github.com/HP-Enterprise/Rental653/issues/731
		jsonObject.put("id", new Integer(SharedPreferenceHelper.getUid(this)));System.out.println("userId"+new Integer(SharedPreferenceHelper.getUid(this)));//�⳵�û�ID
		jsonObject.put("nickName", nickname.getText().toString());	

		StringEntity requestentity = null;
		try {System.out.println("3bbbbbbbbbbbb");
			requestentity = new StringEntity(jsonObject.toString(), "utf-8");System.out.println("4bbbbbbbbbbbb");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}//���������������    
		requestentity.setContentEncoding("UTF-8");
		requestentity.setContentType("application/json");

		System.out.println("5bbbbbbbbbbbb");
		// ����get����ʵ��  
//		HttpPost httppost = new HttpPost("http://121.40.157.200:7890/api/user/"+new Integer(SharedPreferenceHelper.getUid(this)).toString() +"/order");//**********************ע�����󷽷�  
		HttpPut httpput = new HttpPut(Public_Api.appWebSite+"api/me");//**********************ע�����󷽷�  
		
		httpput.setHeader("Content-Type", "application/json;charset=UTF-8");
		AddCookies(httpput);
		
		httpput.setEntity(requestentity);
		try {
			System.out.println("6bbbbbbbbbbbb");
			// �ͻ���ִ��get���� ������Ӧʵ��  
			HttpResponse response = httpCLient.execute(httpput);System.out.println("1aaaaaa");
			if (response.getStatusLine().getStatusCode() == 200) {//����ɹ�
				System.out.println("2aaaaaa");
				// ��ȡ��Ӧ��Ϣʵ��  
				HttpEntity responseentity = response.getEntity();
				String data = EntityUtils.toString(responseentity);
				System.out.println("3aaaaaa"+data);
				//�ж���Ӧ��Ϣ
				org.json.JSONObject datajobject = new org.json.JSONObject(data);
				boolean status = datajobject.getBoolean("status");
				String message = datajobject.getString("message");
				
				if (status) {
					System.out.println("33333");

					//�޸�����ɹ�	
					handler.sendEmptyMessage(UpdateOk);

					saveNickName();
				} else {
					System.out.println("4444");
					//�޸�����ʧ��

					Message msg = new Message();
					msg.what = UpdateFail;
					Bundle bundle = new Bundle();
					bundle.putString("errorMsg", message);
					msg.setData(bundle);
					handler.sendMessage(msg);

				}

			} else {//����ʧ��
				HttpEntity responseentity = response.getEntity();
				String data = EntityUtils.toString(responseentity);
				System.out.println("FOUND NOT"+data);
				handler.sendEmptyMessage(UpdateDataFail);
				System.out.println("1sssssssssss");
			}

		} catch (ClientProtocolException e) { //�����쳣  
			e.printStackTrace();
			handler.sendEmptyMessage(UpdateDataFail);
			System.out.println("2sssssssssss");
		} catch (IOException e) { //�����쳣    
			e.printStackTrace();
			handler.sendEmptyMessage(UpdateDataFail);
			System.out.println("3sssssssssss");
		} catch (org.json.JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(UpdateDataFail);
			System.out.println("4sssssssssss");
		} finally {
			httpCLient.getConnectionManager().shutdown();
			System.out.println("sssssssssss");
		}
		
	}
	
	private void saveNickName(){
		SharedPreferences preferences = this.getSharedPreferences(Public_SP.Account, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putString("nickName", nickname.getText().toString());
		
		editor.commit();
	}

	/**
     * ����Cookie
     * @param request
     */
    public void AddCookies(HttpPut request)
    {
          StringBuilder sb = new StringBuilder();

          String key = "token";
          String val = SharedPreferenceHelper.getString(this, Public_SP.Account, key);
          sb.append(key);
          sb.append("=");
          sb.append(val);
          sb.append(";");

          request.addHeader("cookie", sb.toString());

          System.out.println(""+sb);
    }
}