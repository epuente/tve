package models;

import java.util.Date;

import javax.persistence.Entity;

import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
public class Eventos {
	public int tipo;
	//public FolioProductorAsignado folioproductorasignado;
	public Long referencia;
	public Long folioproductorasignado;
	public Long fase;
	public Date inicio;
	public Date fin;
	public Long estado;
	
}
