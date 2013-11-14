# AIAD - Relatório Intercalar

## Considerações gerais

O relatório tem como objectivo especificar o tema do trabalho, nomeadamente:

* Cenário e objectivos do trabalho
* Estudo da plataforma/ferramenta (de sistemas multi-agente) para desenvolvimento
* Agentes, suas estratégias e interacções
* Resultados esperados e como avaliá-los

Deve incluir as secções referidas no seguinte arquétipo.

## Arquétipo:

### Folha de rosto

* Nome da disciplina e ano

		Agentes e Inteligência Artificial Distribuída 
		2013/2014
	
* Título do trabalho

		Cluedo - O Jogo do Detective

* Nome dos elementos do grupo (incluir código de aluno e email)

		Miguel Oliveira - ei10076
		Afonso Caldas - ei10xxx
		Rui Monteiro - ei10xxx


### Índice

### Enunciado

* Descrição do cenário

		Descrição do jogo a implementar

* Objectivos do trabalho

		Trabalho feito no âmbito da cadeira de AIAD e tal e coisa

* Resultados esperados e forma de avaliação

		


### Plataforma/Ferramenta (Jade)

* Para que serve

		Jade é uma ferramenta de software implementada em Java, que facilita o desenvolvimento de sistemas multi-agente. É completamente escrita em Java, o que possibilita que os programadores apenhas tenham de utilizar essa linguagem para desenvolver os seus agentes. Está também implementada de forma a que os programadores não tenham de saber como o seu ambiente de execução funciona, apenas precisam de o iniciar antes de executar os agentes.

* Descrição das características principais

		Possui as seguintes características:
		
		- Um ambiente de execução onde os agentes JADE podem existir e que deve estar ativo num determinado host de forma a que os agentes possam ser executados nesse host.
		- Uma biblioteca de classes que os programadores podem e/ou devem utilizar para desenvolverem os seus próprios agentes.
		- Um conjunto de ferramentas gráficas que permite a administração e monotorização da atividade de agentes em execução, permitindo, por exemplo, parar e reiniciar agentes.
		- Uma das características mais importantes é a sua arquitectura de comunicação que permite que os agentes troquem mensagens entre si. Esta arquitectura utiliza um paradigma de comunicação asíncrono. Cada agente tem uma fila privada de mensagens onde o JADE coloca mensagens enviadas por outros agentes. Cada agente é notificado quando uma nova mensagem é adicionada à sua fila, podendo o agente aceder e ler as mensagens de várias formas: blocking, polling, timeout and pattern matching. Se e quando as mensagens são lidas está completamente dependente do programador que criou o agente. Estas mensagens têm um formato próprio, especificado pela linguagem ACL, possuindo um certo número de campos.
		
* Realce das funcionalidades relevantes para o trabalho

### Especificação

* Identificação e caracterização dos agentes (arquitectura, comportamento, estratégias)

* Protocolos de interacção

	* Inicia o agente de controlo, que vai criar a interface e os restantes jogadores/agentes.
	* O agente de controlo recebe como argumento o número de jogadores que vai ter
	* Cria os jogadores e passa-lhes logo as cartas de cada um como argumentos
	* Informa quem é o primeiro jogador
	



* Faseamento do projecto

### Recursos

* Bibliografia
* Software