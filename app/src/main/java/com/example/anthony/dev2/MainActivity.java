package com.example.anthony.dev2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Déclaration des composants Graphiques
    private EditText et;
    private Button bt_load;
    private WebView webView;
    private TextView tv_resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //générées
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Je récupère mes composants graphisques
        et = (EditText) findViewById(R.id.et);
        bt_load = (Button) findViewById(R.id.bt_load);
        webView = (WebView) findViewById(R.id.webview_resultat);
        tv_resultat = (TextView) findViewById(R.id.tv_resultat);

        //Redirection des clic, provoque le implements View.OnClickListener qui provoque le onClick
        bt_load.setOnClickListener(this);

        //Réglages webView
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void onClick(View v) {
        //Gère le clic du bouton load
        if (v == bt_load) {
            //Je stocke l'url contenu dans l'editText
            String urlEditText = et.getText().toString();
            //Je donne l'url à la webView
            webView.loadUrl(urlEditText);
            //Je ne peux pas lancer directement la requête ici, je suis sur l'UIThread
            //Je passe donc par une AsyncTask
            //Je crée mon asyncTask
            MonAT monAT = new MonAT(urlEditText);
            //Je la lance
            monAT.execute();
        }
    }

    /**
     * Mon AsyncTAsk qui effectue une requete en arriere plan
     */
    public class MonAT extends AsyncTask {
        //J'ai besoin d'une url pour lancer ma requete
        private String url;
        private Exception exception;

        //Constructeur qui prend une url en param
        public MonAT(String url) {
            this.url = url;
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                //Faire ma requete
                //Penser à mettre la permission Internet dans l'AndroidManifest
                //Penser à ajouter OKhttp dans le build.gradle
                String resultatRequete = OkHttpUtils.sendGetOkHttpRequest(url);
                // je retourne le résultat
                return resultatRequete;
            }
            catch (Exception e) {
                //Si j'ai une exception je la stocke
                e.printStackTrace();
                exception = e;
                //Je retourne null si j'ai une exception
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //Quand ma requête est terminée, o contient le resultat.

            //Si j'ai obtenu une exception
            if (exception != null) {
                tv_resultat.setText(exception.getMessage());
            }
            else {
                //Sinon j'ai un resultat de requete
                //Je mets mon résultat dans le textView
                String monResultat = (String) o;
                tv_resultat.setText(monResultat);
            }
        }
    }
}
