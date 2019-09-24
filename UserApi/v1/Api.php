<?php 

	//Recebendo a classe dboperation
	require_once '../includes/DbOperation.php';
	
	// Método que irá validar todos os parâmetros que estão disponíveis
	// vamos passar os parâmetros necessários para este método

	function isTheseParametersAvailable($params){
		//assumindo que todos os parâmetros estão disponíveis
		$available = true; 
		$missingparams = ""; 
		
		foreach($params as $param){
			if(!isset($_POST[$param]) || strlen($_POST[$param])<=0){
				$available = false; 
				$missingparams = $missingparams . ", " . $param; 
			}
		}
		
		
		//se os parâmetros estiverem faltando
		if(!$available){
			$response = array(); 
			$response['error'] = true; 
			$response['message'] = 'Parameters ' . substr($missingparams, 1, strlen($missingparams)) . ' missing';
			
			//exibindo os erros
			echo json_encode($response);
			
			//parando a execução adicional
			die();
		}
	}
	
	//matriz para exibir a resposta
	$response = array();
	
	
	// se for uma chamada de API
	// isso significa que um parâmetro get chamado api call é definido na URL
	// e com este parâmetro estamos concluindo que é uma chamada de API

	if(isset($_GET['apicall'])){
		
		switch($_GET['apicall']){
			
			// a operação CREATE
			// se o valor da chamada da API for 'criar'
			// vamos criar um registro no banco de dados
			case 'createUser':
				//primeiro verifique os parâmetros necessários para este pedido estão disponíveis ou não
				isTheseParametersAvailable(array('nome','email'));
				
				//criando um novo objeto dboperation
				$db = new DbOperation();
				
				
				//criando um novo registro no banco de dados
				$result = $db->createHero(
					$_POST['nome'],
					$_POST['email']
				);
				

				// se o registro for criado com sucesso
				if($result){
					//registro é criado significa que não há erro
					$response['error'] = false; 

					//na mensagem temos uma mensagem de sucesso
					$response['message'] = 'Usuario criado com successo';

					//e estamos recebendo todos os heróis do banco de dados
					$response['User'] = $db->getHeroes();
				}else{

					// se o registro for não, significa que há um erro
					$response['error'] = true; 

					// e nós temos a mensagem de erro
					$response['message'] = 'Algum erro ocorreu. Por favor tente novamente';
				}
				
			break; 
			
			// a operação escrita
			// se a chamada for getheroes
			case 'getUser':
				$db = new DbOperation();
				$response['error'] = false; 
				$response['message'] = 'Pedido concluído com sucesso';
				$response['User'] = $db->getUser();
			break; 
			
			//operação de alteração
			case 'updateUser':
				isTheseParametersAvailable(array('id','nome','email'));
				$db = new DbOperation();
				$result = $db->updateHero(
					$_POST['id'],
					$_POST['name'],
					$_POST['email']
				);
				
				if($result){
					$response['error'] = false; 
					$response['message'] = 'Heroe alterado com sucesso';
					$response['User'] = $db->getUser();
				}else{
					$response['error'] = true; 
					$response['message'] = 'Algum erro ocorreu. Por favor tente novamente';
				}
			break; 
			
			//operação de delete
			case 'deleteUser':

			// para a operação delete estamos obtendo um parâmetro GET da url com o id do registro a ser deletado
				if(isset($_GET['id'])){
					$db = new DbOperation();
					if($db->deleteUser($_GET['id'])){
						$response['error'] = false; 
						$response['message'] = 'Heroe apagado com sucesso';
						$response['User'] = $db->getUser();
					}else{
						$response['error'] = true; 
						$response['message'] = 'Algum erro ocorreu. Por favor tente novamente';
					}
				}else{
					$response['error'] = true; 
					$response['message'] = 'Nada a excluir. Por favor forneça o id';
				}
			break; 
		}
		
	}else{
		//se não for uma chamada api
		//respondendo com os valores apropriados para array
		$response['error'] = true; 
		$response['message'] = 'Chamda de API inválida.';
	}
	
	//exibindo a resposta na estrutura do JSON
	echo json_encode($response);
	
	
