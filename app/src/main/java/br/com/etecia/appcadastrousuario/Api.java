package br.com.etecia.appcadastrousuario;

public class Api {
    //URL para acesso ao banco de dados WEB Service
    private static final String ROOT_URL = "http://10.67.96.174/UserApi/v1/Api.php?apicall=";

    //Variáveis estáticas e globais para acesso os métodos no WEB Service

    public static final String URL_CREATE_USER = ROOT_URL + "createUser";
    public static final String URL_READ_USERS = ROOT_URL + "getUser";
    public static final String URL_UPDATE_USER = ROOT_URL + "updateUser";
    public static final String URL_DELETE_USER = ROOT_URL + "deleteUser&id=";
}
