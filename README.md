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

![Domínio](diagrama_ddd.jpg)

### Vídeo de Demonstração através de Testes de Uso com JUnit:

[Vídeo demonstrando testes das funcionalidades da aplicação](https://github.com/user-attachments/assets/a36116af-0593-45f7-8e15-e37a4cb56149)

---

## Segunda Entrega: Camada de Persistência Real e Histórico de Dados

### Exemplos de Uso dos Endpoints REST
A API expõe os seguintes novos endpoints para auditoria e histórico no [ContaController](file:///C:/Users/the5l/repos/ESE_PB_TP1/Back-end/src/main/java/org/example/banco/controller/ContaController.java):
* **Listar Todo o Histórico**: `GET /contas-banco/historico`
* **Listar Histórico por Conta (Ordenado por data decrescente)**: `GET /contas-banco/historico/{contaId}`

---

### Cobertura de Testes Automatizados
* **Testes de Integração de Persistência ([ContaPersistenceIntegrationTests.java](file:///C:/Users/the5l/repos/ESE_PB_TP1/Back-end/src/test/java/org/example/banco/ContaPersistenceIntegrationTests.java))**:
  * Utilizam `@SpringBootTest` e `@ActiveProfiles("test")` para subir o contexto do Spring e executar queries no banco in-memory H2.
  * Testam a persistência real dos dados das contas e confirmam se o histórico de mudanças (`CRIACAO`, `ATUALIZACAO` e `EXCLUSAO`) é gerado corretamente a cada operação.
* **Testes Baseados em Propriedades ([ContaServiceTests.java](file:///C:/Users/the5l/repos/ESE_PB_TP1/Back-end/src/test/java/org/example/banco/ContaServiceTests.java))**:
  * Adaptados com JQwik para suportar os novos mocks de histórico e validar regras de negócio sobre um amplo escopo de inputs gerados dinamicamente.
* **Testes de Fuzzing ([FuzzTests.java](file:///C:/Users/the5l/repos/ESE_PB_TP1/Back-end/src/test/java/org/example/banco/FuzzTests.java) e [NetworkTests.java](file:///C:/Users/the5l/repos/ESE_PB_TP1/Back-end/src/test/java/org/example/banco/NetworkTests.java))**:
  * Atualizados com suporte a mocks de auditoria para garantir robustez contra inputs inesperados sem quebrar por exceções nulas.

