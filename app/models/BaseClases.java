package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class BaseClases extends Model{
	private static final long serialVersionUID = 1L;

	@Id	
	@Column(updatable=false, nullable=false)
	public Long id;
	
	@Formats.DateTime(pattern="dd-MM-yyyy")
	@CreatedTimestamp
	public Date auditInsert = new Date();
	
	@Formats.DateTime(pattern="dd-MM-yyyy")
	@UpdatedTimestamp
	public Date auditUpdate = new Date();	
	
}
