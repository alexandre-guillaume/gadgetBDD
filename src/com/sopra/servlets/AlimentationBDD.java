package com.sopra.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sopra.dao.*;
import com.sopra.ga.RealTimeRequest;

/**
 * Servlet appelée par le Gadget lors de son chargement, lors de son premier appel elle initialise le thread qui permettra
 * de récupérer les données de Google Analytics à intervalle régulier, puis même si la servlet est de nouveau appelée,
 * elle ne relancera pas de nouveau thread
 * @author Brandon Gommard
 *
 */
public class AlimentationBDD extends HttpServlet implements Runnable {

	private static final long serialVersionUID 		= 1L;
	String[] objectifsAsuivre = null;
	// On initialise les paramètres à des valeurs par défault
	String siteAsuivre = "ga:50359954";
	String dbTableName = "mma";
	String dbUser = "mmawrite";
	String dbPass = "coveamma";
	Thread extracteur;
	String path;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("Initialisation du thread d'alimentation de la base de données");
		extracteur = new Thread(this);
		extracteur.setPriority(Thread.MIN_PRIORITY);  // priorité minimale
		extracteur.start();
		path = getServletConfig().getServletContext().getRealPath("/") + "/WEB-INF/client_secrets.p12";  
	}

	public void run() {
		while (true) {
			if(objectifsAsuivre != null){
				for(int i=0 ; i < objectifsAsuivre.length ; i++){
					//information de google sur les os , leur version , appareille et navigateur.
					List<List<String>> datas = RealTimeRequest.ExecuteRequest("rt:" + objectifsAsuivre[i], 
							"rt:operatingSystem,rt:operatingSystemVersion,rt:deviceCategory,rt:browser,rt:browserVersion",
							"-rt:" + objectifsAsuivre[i],
							path,
							siteAsuivre);
					
					ObjectifsManagement.ajoutContenu(datas, objectifsAsuivre[i], dbTableName, dbUser, dbPass);
				}
				try {
					// Attendre 30 minutes avant de refaire une requête 1000 * 60 * 30
					System.out.println("Alimentation terminée, nouveau remplissage de la BDD dans 30 minutes");
					extracteur.sleep(1000 * 60 * 30);
				} 
				catch (InterruptedException ignored) { System.out.println("Thread d'alimentation interrompu"); }
			}
		}
	}

	/**
	 * Lorsque le gadget sera mis à jour, la fonction doGet sera appelée, on mettra donc à jour la liste des objectifs à suivre,
	 * afin que le thread récupère les bonnes métriques.
	 * On affiche également un rappel à l'utilisateur des métriques qu'il suit actuellement.
	 */
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		/*String paramObjectifs = request.getParameter("up_listeObjectifs");
		objectifsAsuivre = paramObjectifs.split(";");
		
		siteAsuivre = request.getParameter("up_siteAsuivre");
		dbTableName = request.getParameter("up_dbTable");
		dbUser = request.getParameter("up_dbUser");
		dbPass = request.getParameter("up_dbPass");
		

		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		out.write("Editez les préférences du gadget pour spécifier les données Google Analytics que vous souhaitez suivre "
				+ "et stocker dans la base de données. Ajoutez des métriques séparées par des point-virgules. La liste complète "
				+ "des métriques est accessible à l'adresse: https://developers.google.com/analytics/devguides/reporting/realtime/dimsmets/ \n");
		out.write("\nVous suivez actuellement: \n");
		for(int i = 0; i<objectifsAsuivre.length ; i++){
			out.write(objectifsAsuivre[i] + "\n");
		}
		out.close();*/
		response.setContentType("text/html");
	    response.getOutputStream().println("Hello World !");
	}

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		doGet(request, response);
	}
}