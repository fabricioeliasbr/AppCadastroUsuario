package br.com.etecia.appcadastrousuario;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    TextView textViewCadastre, textViewNome, textViewEmail;
    EditText editTextNome, editTextEmail, editTextUserId;
    Button buttonCadastra;
    ListView listViewDados;

    List<User> userList;

    boolean isUpdating = false;
    boolean isCreating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUserId = this.findViewById(R.id.editTextUserId);
        editTextNome = this.findViewById(R.id.editTextNome);
        editTextEmail = this.findViewById(R.id.editTextEmail);
        textViewNome = this.findViewById(R.id.textViewNome);
        textViewEmail = this.findViewById(R.id.textViewEmail);
        textViewCadastre = this.findViewById(R.id.textViewCadastre);
        buttonCadastra = this.findViewById(R.id.buttonCadastra);

        userList = new ArrayList<>();

        buttonCadastra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isCreating) {
                    createUser();
                } else {
                    updateUser();
                }

            }
        });
        readUsers();
    }

    private void createUser() {
        String nome = editTextNome.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            editTextNome.setError("Por favor entre com o nome");
            editTextNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Por favor entre com um e-mail válido");
            editTextEmail.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("nome", nome);
        params.put("email", email);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_USER, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void readUsers() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_USERS, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void updateUser() {
        String id = editTextUserId.getText().toString();
        String nome = editTextNome.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            editTextNome.setError("Este campo não pode estar vazio!");
            editTextNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Este campo não pode estar vazio!");
            editTextEmail.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("nome", nome);
        params.put("email", email);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_USER, params, CODE_POST_REQUEST);
        request.execute();
        Toast.makeText(getApplicationContext(), "Cadastrado!", Toast.LENGTH_SHORT).show();
        buttonCadastra.setText("Cadastrar");

        editTextNome.setText("");
        editTextEmail.setText("");
        isUpdating = false;

    }

    private void deleteUser(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_USER + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshUserList(JSONArray users) throws JSONException {
        userList.clear();

        for (int i = 0; i < users.length(); i++) {
            JSONObject obj = users.getJSONObject(i);

            userList.add(new User(
                    obj.getInt("id"),
                    obj.getString("nome"),
                    obj.getString("email")
            ));
        }

        UserAdapter adapter = new UserAdapter(userList);
        listViewDados.setAdapter(adapter);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("erro")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshUserList(object.getJSONArray("users"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    class UserAdapter extends ArrayAdapter<User> {
        List<User> userList;

        public UserAdapter(List<User> userList) {
            super(MainActivity.this, R.layout.layout_user_list, userList);
            this.userList = userList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_user_list, null, true);

            TextView textViewNome = listViewItem.findViewById(R.id.textViewNome);
            TextView textViewEmail = listViewItem.findViewById(R.id.textViewEmail);
            TextView textViewDelete = listViewItem.findViewById(R.id.textViewApaga);

            final User user = userList.get(position);

            textViewNome.setText(user.getNome());
            textViewEmail.setText(user.getEmail());

            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Apagar " + user.getNome())
                            .setMessage("Tem certeza que deseja exluir?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteUser(user.getId());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            });

            return listViewItem;
        }
    }
}