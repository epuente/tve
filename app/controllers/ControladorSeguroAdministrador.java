package controllers;

import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar; 
import actions.ConfirmarAdministrador;

@With({Encabezados.class, Autenticar.class, ConfirmarAdministrador.class})
public abstract class ControladorSeguroAdministrador extends Controller {}