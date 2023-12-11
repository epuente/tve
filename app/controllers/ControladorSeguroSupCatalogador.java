package controllers;

import actions.Autenticar;
import actions.ConfirmarCatalogador;
import actions.ConfirmarSupCatalogador;
import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;

@With({Encabezados.class, Autenticar.class, ConfirmarSupCatalogador.class})
public abstract class ControladorSeguroSupCatalogador extends Controller {

}