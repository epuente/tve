package controllers;

import actions.Autenticar;
import actions.ConfirmarCatalogador;
import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;

@With({Encabezados.class, Autenticar.class, ConfirmarCatalogador.class})
public abstract class ControladorSeguroCatalogador extends Controller {

}