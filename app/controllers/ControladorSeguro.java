package controllers;

import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar;
import actions.Encabezados;

@With({ Encabezados.class,   Autenticar.class})
public abstract class ControladorSeguro extends Controller {}