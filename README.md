# Sistema Monolítico Banco | Projeto de Bloco: Engenharia de Softwares Escaláveis

### Apresentação e Documentação:
Esta é a aplicação de um banco fictício, que contém um CRUD feito em Java com Spring Boot e outras tecnologias. A seguir são os comportamentos esperados do software:

* C -> Adicionar nova conta dentro do banco de dados do banco.
* R -> Obter dados de uma ou várias contas. 
* U -> Editar saldo de uma conta já existente.
* D -> Remover uma conta via ID.

Padrão utilizado: Controller-Service-Repository.

Princípio de projeto: DDD.

### Diagrama do Domínio Atual do Banco (com potencial para expansões):

<img width="722" height="441" alt="Domínio_Banco" src="https://github.com/user-attachments/assets/758b41e2-778b-4e7d-a906-70842cee929f" />

### Vídeo de Demonstração através de Testes de Uso com JUnit:

[Vídeo demonstrando testes das funcionalidades da aplicação](https://github.com/user-attachments/assets/a36116af-0593-45f7-8e15-e37a4cb56149)

---

## Segunda Entrega: Camada de Persistência Real e Histórico de Dados

### Endpoints do Histórico
* **Listar Todo o Histórico**: `GET /contas-banco/historico`
* **Listar Histórico por Conta (Ordenado por data decrescente)**: `GET /contas-banco/historico/{contaId}`

