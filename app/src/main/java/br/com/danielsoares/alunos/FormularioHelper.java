package br.com.danielsoares.alunos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import br.com.danielsoares.model.Aluno;

/**
 * Created by danie on 10/06/2017.
 */

public class FormularioHelper {
    private final EditText campoNome;
    private final  EditText campoEndereco;
    private final EditText campoTelefone;
    private final EditText campoSite;
    private final RatingBar campoNota;
    private Aluno aluno;
    private final ImageView campoFoto;

    public FormularioHelper(FormularioActivity activity) {
        campoNome = (EditText) activity.findViewById(R.id.nome);
        campoEndereco = (EditText) activity.findViewById(R.id.endereco);
        campoTelefone = (EditText) activity.findViewById(R.id.telefone);
        campoSite = (EditText) activity.findViewById(R.id.site);
        campoNota = (RatingBar) activity.findViewById(R.id.rating);
        campoFoto = (ImageView) activity.findViewById(R.id.formulario_foto);
        aluno = new Aluno();
    }


    public Aluno pegarAluno() {
        aluno.setNome(campoNome.getText().toString());
        aluno.setEndereco(campoEndereco.getText().toString());
        aluno.setTelefone(campoTelefone.getText().toString());
        aluno.setSite(campoSite.getText().toString());
        aluno.setNota(Double.valueOf(campoNota.getProgress()));
        aluno.setCaminhoFoto((String) campoFoto.getTag());
        return aluno;
    }

    public void preecheFormulario(Aluno aluno) {
        campoNome.setText(aluno.getNome());
        campoEndereco.setText(aluno.getEndereco());
        campoTelefone.setText(aluno.getTelefone());
        campoSite.setText(aluno.getSite());
        campoNota.setProgress(aluno.getNota().intValue());
        carregaImagem(aluno.getCaminhoFoto());
        this.aluno = aluno;
    }

    public void carregaImagem(String caminhoFoto) {
        if(caminhoFoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
            campoFoto.setImageBitmap(bitmap);
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFoto.setTag(caminhoFoto);
        }
    }
}