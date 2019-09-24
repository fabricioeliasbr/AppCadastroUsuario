<?php
 
class DbOperation
{
    //Link de conexão com banco de dados
    private $con;
 
    //Construtor da classe
    function __construct()
    {
        // Obtendo o arquivo DbConnect.php
        require_once dirname(__FILE__) . '/DbConnect.php';
 
        // Criando um objeto DbConnect para se conectar ao banco de dados
        $db = new DbConnect();
 
        
		// Inicializando o link de conexão
        // chamando o método connect da classe DbConnect
        $this->con = $db->connect();
    }
	/*
	* Operação de criação
	* Quando esse método é chamado, um novo registro é criado no banco de dados
	*/
	function createUser($nome, $email){
		$stmt = $this->con->prepare("INSERT INTO user(nome, email) values(?, ?)");
		$stmt->bind_param("ssis", $nome, $email);
		if($stmt->execute())
			return true
		return false;
	}	
	/*
	* A operação de leitura
	* Quando este método é chamado, ele está retornando todo o registro existente do banco de dados
	*/
	function getUser(){
		$stmt = $this->con->prepare("SELECT nome, email FROM user");
		$stmt->execute();
		$stmt->bind_result($id, $nome, $email);
		
		$usuario = array(); 
		
		while($stmt->fetch()){
			$user  = array();
			$user['id'] = $id; 
			$user['nome'] = $nome; 
			$user['email'] = $email;
			array_push($user, $usuario); 
		}
		
		return $user; 
	}
	
	 /*
	* A operação de atualização
	* Quando esse método é chamado, o registro com o ID fornecido é atualizado com os novos valores fornecidos
	*/
	function updateUser($id, $nome, $email){
		$stmt = $this->con->prepare("UPDATE user SET nome = ?, email = ? WHERE id = ?");
		$stmt->bind_param("ssisi", $nome, $email, $id);
		if($stmt->execute())
			return true; 
		return false; 
	}		
	/*
	* A operação de exclusão
	* Quando este método é chamado, o registro é excluído para o ID fornecido
	*/
	function deleteUser($id){
		$stmt = $this->con->prepare("DELETE FROM user WHERE id = ? ");
		$stmt->bind_param("i", $id);
		if($stmt->execute())
			return true; 
		return false; 
	}
}