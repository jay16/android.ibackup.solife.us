package android.backup.solife.us.entity;


/**
 * class name：SmsInfo<BR>
 * class description：获取短信各种信息的类
 */
public class SmsInfo {
	private Integer phoneId, smsId;
	/**
	 * 短信内容
	 */
	private String content;
	/**
	 * 发送短信的电话号码
	 */
	private String number;
	/**
	 * 发送短信的日期和时间
	 */
	private String date;
	/**
	 * 发送短信人的姓名
	 */
	private String name;
	/**
	 * 短信类型1是接收到的，2是已发出
	 */
	private String type;
	private Long idId,sync;
	private String state;

	public void setIdId(Long IdId) {
		this.idId = IdId;
	}
	public Long getIdId() {
		return this.idId;
	}

	public void setPhoneId(Integer phoneId) {
		this.phoneId = phoneId;
	}
	public Integer getPhoneId() {
		return this.phoneId;
	}
	
	public void setSmsId(Integer smsId) {
		this.smsId = smsId;
	}
	public Integer getSmsId() {
		return this.smsId;
	}
	public String getContent() {
		return this.content;
	}

	public void setContent(String smsbody) {
		this.content = smsbody;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String phoneNumber) {
		this.number = phoneNumber;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void setSync(Long sync) {
		this.sync = sync;
	}
	public Long getSync() {
		return this.sync;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return this.state;
	}
	
	public String toStr() {
		return  "id_id:" + this.idId +
				"name:" + this.name +
				"number:" + this.number +
				"date:" + this.date + 
				"type:" + this.type +
				"content:" + this.content;
	}
}