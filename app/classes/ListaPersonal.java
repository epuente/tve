package classes;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import models.CuentaRol;
import models.Personal;
import models.Rol;
import models.TipoContrato;

public class ListaPersonal {
	public List<lista> laLista; 
	
	public class lista{
		public Long id;
		public String numEmpleado;
		public String getPaterno() {
			return paterno;
		}

		public void setPaterno(String paterno) {
			this.paterno = paterno;
		}

		public String getMaterno() {
			return materno;
		}

		public String getNombre() {
			return nombre;
		}

		public String getRoles() {
			return roles;
		}

		public String getTipocontrato() {
			return tipocontrato;
		}

		public String getTurno() {
			return turno;
		}

		public String getEstado() {
			return estado;
		}

		public String paterno;
		public String materno;
		public String nombre;
		public String roles;
		public String tipocontrato;
		public String turno;
		public String estado;
		
		public String getNumEmpleado() {
			return this.numEmpleado;
		}
	}
	
	//public static Model.Finder<Long,ListaPersonal> find = new Model.Finder<Long,ListaPersonal>(Long.class, ListaPersonal.class);
	
	public ListaPersonal() {
		laLista = new ArrayList<ListaPersonal.lista>();
		for ( Personal p: Personal.find.all()  ) {
			lista l = new lista();
			l.id = p.id;
			l.numEmpleado = p.numEmpleado;
			l.paterno = p.paterno;
			l.materno = p.materno;
			l.nombre = p.nombre;
			StringBuilder auxRolBuilder = new StringBuilder();
			for ( CuentaRol r : p.cuentas.get(0).roles) {
				auxRolBuilder.append(Rol.find.byId(r.rol.id).descripcion).append(",  ");
			}
			String auxRol = auxRolBuilder.toString();
			if (auxRol.length()>1)
				auxRol=auxRol.substring(0, auxRol.length()-3);
			
			l.roles = auxRol;
			l.tipocontrato = TipoContrato.find.byId(p.tipocontrato.id).descripcion;
			switch (p.turno) {
			case "M":
				l.turno = "Matutino";
				break;
			case "V":
				l.turno = "Vespertino";
				break;				
			case "A":
				l.turno = "Ambos";
				break;				
			default:
				l.turno = "No definido";
				break;
			}
			l.estado = p.activo.compareTo("S")==0?"Activo":"No activo";
			laLista.add(l);
		}
	}
	
	
	
	public static  List<lista>  Pagina(ListaPersonal lp, int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
		List<lista> sortedList = new ArrayList<ListaPersonal.lista>(); 
		//Filtrado
		List<lista> ret = lp.laLista.stream().filter(
						f->
						 comparaSinAcentos(f.paterno, filtro)
						 || comparaSinAcentos(f.materno, filtro)
						 || comparaSinAcentos(f.nombre, filtro)
						 || comparaSinAcentos(f.roles, filtro)
						 || comparaSinAcentos(f.tipocontrato, filtro)
						 || comparaSinAcentos(f.turno, filtro)
						 || comparaSinAcentos(f.estado, filtro)
					).collect(Collectors.toList());
		// Ordenamiento por columna
		System.out.println("ordenando por columna... "+columnaOrden);
		
		if (columnaOrden.compareTo("numEmpleado")==0) {
			System.out.println("ordenando por numEmpleado");
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getNumEmpleado))
					.collect(Collectors.toList());
		}
		if (columnaOrden.compareTo("paterno")==0) {
			System.out.println("ordenando por paterno");
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getPaterno))
					.collect(Collectors.toList());
		}
		if (columnaOrden.compareTo("materno")==0) {
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getMaterno))
					.collect(Collectors.toList());
		}	
		if (columnaOrden.compareTo("nombre")==0) {
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getNombre))
					.collect(Collectors.toList());
		}		
		if (columnaOrden.compareTo("roles")==0) {
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getRoles))
					.collect(Collectors.toList());
		}		
		if (columnaOrden.compareTo("contrato")==0) {
			System.out.println("\n\nSOY COLUMNA CONTRATO\n\n");
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getTipocontrato))
					.collect(Collectors.toList());
		}		
		if (columnaOrden.compareTo("turno")==0) {
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getTurno))
					.collect(Collectors.toList());
		}
		if (columnaOrden.compareTo("activo")==0) {
			sortedList = ret.stream()
					.sorted(Comparator.comparing(lista::getEstado))
					.collect(Collectors.toList());
		}		
		if (tipoOrden.compareTo("desc")==0) 
			Collections.reverse(sortedList);
		
		
		
		return  sortedList;
	}
	
	private static String normalize(String input) {
	    return input == null ? null : Normalizer.normalize(input, Normalizer.Form.NFKD);
	}
	
	static String removeAccents(String input) {
	    return normalize(input).replaceAll("\\p{M}", "");
	}	
	
	public static boolean comparaSinAcentos(String cadena1, String cadena2) {
		String c1=cadena1.toUpperCase();
		String c2=cadena2.toUpperCase();
		return removeAccents(c1).contains(removeAccents(c2) );
	}

}
