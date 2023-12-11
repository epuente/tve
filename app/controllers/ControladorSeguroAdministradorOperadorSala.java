package controllers;

import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar; 
import actions.ConfirmarAdministradorOperadorSala;

@With({Encabezados.class, Autenticar.class, ConfirmarAdministradorOperadorSala.class})
public abstract class ControladorSeguroAdministradorOperadorSala extends Controller {}