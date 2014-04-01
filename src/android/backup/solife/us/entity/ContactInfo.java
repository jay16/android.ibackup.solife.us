package android.backup.solife.us.entity;

import android.graphics.Bitmap;

public class ContactInfo {
	  private Integer phoneId,contactId;
	  //手机号
	  private String number;
	  //联系人名称
	  private String name;
	  //联系人头像
	  private byte[] photo;
	  //信息来源: sim,phone
	  private String type;
	private long id,sync,idId;
	private String state;

	public void setId(long Id) {
		this.id = Id;
	}
	public long getId() {
		return this.id;
	}
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
	
	  
	  public void setNumber(String phoneNumber) {
		  this.number = phoneNumber;
	  }
	  public String getNumber() {
		  return this.number;
	  }
	  
	  public void setName(String contactName) {
		  this.name = contactName;
	  }
	  public String getName() {
		  return this.name;
	  }
	  
	  public void setContactId(Integer contactId) {
		  this.contactId = contactId;
	  }
	  public Integer getContactId() {
		  return this.contactId;
	  }
	  
	  public void setPhoto(byte[] contactPhoto) {
		  this.photo = contactPhoto;
	  }
	  public byte[] getPhoto() {
		  return this.photo;
	  }
	  
	  public void setType(String type) {
		  this.type = type;
	  }
	  public String getType() {
		  return this.type;
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
		return "{ id_id: " + this.idId +
				",name: " + this.name +
				",number: " + this.number +
				",type: " + this.type +
				",sync: " + this.sync +
				",state: " + this.state +
				",photoId: " + this.phoneId +
				",contactId:" + this.contactId;
	}
}
