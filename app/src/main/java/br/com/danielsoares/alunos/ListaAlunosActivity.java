package br.com.danielsoares.alunos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Browser;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.danielsoares.EnvioAlunosTask;
import br.com.danielsoares.WebClient;
import br.com.danielsoares.adapter.AlunosAdapter;
import br.com.danielsoares.converter.AlunoConverter;
import br.com.danielsoares.dao.AlunoDAO;
import br.com.danielsoares.model.Aluno;

import static br.com.danielsoares.alunos.R.id.site;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);

        if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 456);
        }

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                Intent intentParaFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intentParaFormulario.putExtra("aluno", aluno);
                startActivity(intentParaFormulario);
            }
        });

        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentParaFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentParaFormulario);
            }
        });

        registerForContextMenu(listaAlunos);
    }


    public void carregaLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        AlunosAdapter adapter = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos,  menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_enviar_notas:
                new EnvioAlunosTask(this).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);
        MenuItem deletar = menu.add("Deletar");
        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);

        MenuItem itemLigar = menu.add("Ligar");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);

                    return false;
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                    return false;
                }
            }
        });

        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
        itemSMS.setIntent(intentSMS);


        MenuItem itemSite = menu.add("Visitar site!");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);
        String site = aluno.getSite();
        if (!site.startsWith("https://")) {
            site = "https://" + site;
        }
        intentSite.setData(Uri.parse(site));
        itemSite.setIntent(intentSite);

        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.deleta(aluno);
                dao.close();

                carregaLista();
                return false;
            }
        });



    }

}
