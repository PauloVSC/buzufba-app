package com.example.vinicius.buzufbaapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

    public static boolean validaPermissions (Activity activity, String[] permissoes,  int requestCode){

        if(Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissions = new ArrayList<String>();

            for (String permissao: permissoes){

                // Verificação se as permissões já existem
                Boolean validaPermisson = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!validaPermisson) listaPermissions.add(permissao);

                // Verifica se a lista está vazia, caso sim retorna true
                if ( listaPermissions.isEmpty()) return true;

                String[] novasPermissoes = new String[listaPermissions.size()];
                listaPermissions.toArray(novasPermissoes);

                //Solicitação de Permissões
                ActivityCompat.requestPermissions(activity, novasPermissoes, 1);


            }

        }
        return true;
    }
}
