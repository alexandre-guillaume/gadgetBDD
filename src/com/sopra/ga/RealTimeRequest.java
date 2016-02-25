package com.sopra.ga;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.Analytics.Data.Realtime.Get;
import com.google.api.services.analytics.model.RealtimeData;

import java.io.File;
import java.io.IOException;
import java.util.List;

/***
 * Classe permettant d'effectuer des requêtes sur Google Analytics via l'API RealTime
 * @author Brandon Gommard
 *
 */
public class RealTimeRequest {

	private static final String APPLICATION_NAME = "Request Real Time";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String SERVICE_ACCOUNT_EMAIL = "244928579941-kftdvta8vcfb652a19okeminim514gnj@developer.gserviceaccount.com";


	public static List<List<String>> ExecuteRequest(String metriques, String dimensions, String sort, String keyP12, String view_id) {

		Analytics analytics = null;
		try {
			analytics = initializeAnalytics(keyP12);
		} catch (Exception e) {
			System.out.println("Impossible d'initialiser la connexion avec Google Analitycs, vérifiez votre connexion ou votre proxy");
		}

		Get realtimeRequest	= null;
		RealtimeData realtimeData = null;
		try {
			System.out.println(view_id);
			System.out.println(metriques);
			System.out.println(dimensions);
			realtimeRequest = analytics.data().realtime().get(view_id, metriques)
														.setDimensions(dimensions)
														.setSort(sort);
			realtimeData = realtimeRequest.execute();
			//System.out.println(realtimeData);
			/*for ( List<String>  mega : realtimeData.getRows()){
				for ( String  mystring : mega){
					System.out.print(mystring + " ");
				}
				System.out.println();
			}*/
		} catch (IOException e1) {
			System.out.println("Requête impossible");
		}
		
		return realtimeData.getRows();
	}


	/**
	 * Initialise un object Analytics avec la clef fournie en paramètre
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Analytics initializeAnalytics(String key) throws Exception {
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		// On déclare ici un GoogleCredential avec la clef .p12 récupérée dans la console developer
		GoogleCredential credential = new GoogleCredential.Builder()
		.setTransport(httpTransport)
		.setJsonFactory(JSON_FACTORY)
		.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
		.setServiceAccountPrivateKeyFromP12File(new File(key))
		.setServiceAccountScopes(AnalyticsScopes.all())
		.build();

		// Construit l'object Analytics
		return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
		.setApplicationName(APPLICATION_NAME).build();
	}


	
}