package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class HisFolio  extends models.utils.PlantillaModelo{

		/**
	 * 
	 */

		@ManyToOne 
		public Folio folio;
		
		@ManyToOne
		public Estado estado;

		@ManyToOne
		public Personal usuario;

		@ManyToOne
		public Rol rol;

		public static Model.Finder<Long,HisFolio> find = new Model.Finder<Long,HisFolio>(Long.class, HisFolio.class);
		
}
