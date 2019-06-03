# Octo Events em Kotlin

Octo Events é uma API REST criada utilizando a linguagem [Kotlin](https://github.com/JetBrains/kotlin) e os frameworks [Javalin](https://github.com/tipsy/javalin), [Koin](https://github.com/InsertKoinIO/koin) e [Exposed](https://github.com/JetBrains/Exposed) .

Ela foi feita para servir de demonstração de como seria uma API REST básica, porém funcional, completamente feita em Kotlin.

## Tech stack

O stack completo utilizado na construção da API foi: 

  - [Kotlin](https://github.com/JetBrains/kotlin) como linguagem de programação
  - [Javalin](https://github.com/tipsy/javalin) como framework web
  - [Koin](https://github.com/InsertKoinIO/koin) para gestão e injeção de dependências
  - [Jackson](https://github.com/FasterXML/jackson-module-kotlin) para serializar/deserializar a comunicação com a API
  - [HikariCP](https://github.com/brettwooldridge/HikariCP) como gerenciador do pool de conexões
  - [H2](https://github.com/h2database/h2database) como banco de dados
  - [Exposed](https://github.com/JetBrains/Exposed) como framework da camada de persistência de dados
  
Testes:

  - [junit](https://github.com/junit-team/junit4)
  - [Unirest](https://github.com/Kong/unirest-java) para invocar endpoints nos testes de integração
  
E o código apresentado nesta demonstração levou em consideração os guias de estilo da comunidade Kotlin e as melhores práticas que puderam ser observadas em projetos open-source de outras APIs similares.

#### Estrutura do Projeto
	  + controller/
		  Classes que representam os Endpoints da aplicação.
      + config/
          Classes de configuração dos Frameworks, Rotas dos endpoints e Mapeamento de Exceções.
      + domain/
          Classe do modelo de domínio (Event).
      + domain/helper 
		  Classes de apoio na serialização/deserialização das classes de domínio.
	  + i18n
		  Classes que implementam a localização das mensagens da API.
	  + persistence
		  Camada contendo a lógica de persistência no banco de dados e definição das tabelas.
      + service/
          Camada contendo a lógica do negócio.
      + utils/
          Classes utilitárias para a aplicação.
          
      - App.kt <- Classe main da aplicação.
	  
#### Database

A database escolhida para este exemplo foi a H2 (database in-memory), porém o código pode ser rapidamente refatorado para utilizar uma permanente como MySQL. Os detalhes de conexão podem ser configurados no arquivo `koin.properties`, porém será necessário adicionar novos drivers no `pom.xml` para suportar a conexão a outras bases de dados.

## Iniciando a aplicação

Build:
> mvn package

Inicie o servidor:
> java -jar octo-events-1.0.jar

## API

### Webhook Endpoint

Este endpoint recebe e valida os eventos do tipo IssueEvent postados pelo Github, gravando-os no banco de dados para serem consultados posteriormente.

**Endpoint:** *POST* http://localhost:7000/api/webhooks

**Format:**

Este é um exemplo de JSON enviado pelo Github:

```
{
  "action": "edited",
  "issue": {
    "url": "https://api.github.com/repos/Codertocat/Hello-World/issues/1",
    "repository_url": "https://api.github.com/repos/Codertocat/Hello-World",
    "labels_url": "https://api.github.com/repos/Codertocat/Hello-World/issues/1/labels{/name}",
    "comments_url": "https://api.github.com/repos/Codertocat/Hello-World/issues/1/comments",
    "events_url": "https://api.github.com/repos/Codertocat/Hello-World/issues/1/events",
    "html_url": "https://github.com/Codertocat/Hello-World/issues/1",
    "id": 444500041,
    "node_id": "MDU6SXNzdWU0NDQ1MDAwNDE=",
    "number": 1,
    "title": "Spelling error in the README file",
    "user": {
      "login": "Codertocat",
      "id": 21031067,
      "node_id": "MDQ6VXNlcjIxMDMxMDY3",
      "avatar_url": "https://avatars1.githubusercontent.com/u/21031067?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/Codertocat",
      "html_url": "https://github.com/Codertocat",
      "followers_url": "https://api.github.com/users/Codertocat/followers",
      "following_url": "https://api.github.com/users/Codertocat/following{/other_user}",
      "gists_url": "https://api.github.com/users/Codertocat/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/Codertocat/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/Codertocat/subscriptions",
      "organizations_url": "https://api.github.com/users/Codertocat/orgs",
      "repos_url": "https://api.github.com/users/Codertocat/repos",
      "events_url": "https://api.github.com/users/Codertocat/events{/privacy}",
      "received_events_url": "https://api.github.com/users/Codertocat/received_events",
      "type": "User",
      "site_admin": false
    },
    "labels": [
      {
        "id": 1362934389,
        "node_id": "MDU6TGFiZWwxMzYyOTM0Mzg5",
        "url": "https://api.github.com/repos/Codertocat/Hello-World/labels/bug",
        "name": "bug",
        "color": "d73a4a",
        "default": true
      }
    ],
    "state": "open",
    "locked": false,
    "assignee": {
      "login": "Codertocat",
      "id": 21031067,
      "node_id": "MDQ6VXNlcjIxMDMxMDY3",
      "avatar_url": "https://avatars1.githubusercontent.com/u/21031067?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/Codertocat",
      "html_url": "https://github.com/Codertocat",
      "followers_url": "https://api.github.com/users/Codertocat/followers",
      "following_url": "https://api.github.com/users/Codertocat/following{/other_user}",
      "gists_url": "https://api.github.com/users/Codertocat/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/Codertocat/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/Codertocat/subscriptions",
      "organizations_url": "https://api.github.com/users/Codertocat/orgs",
      "repos_url": "https://api.github.com/users/Codertocat/repos",
      "events_url": "https://api.github.com/users/Codertocat/events{/privacy}",
      "received_events_url": "https://api.github.com/users/Codertocat/received_events",
      "type": "User",
      "site_admin": false
    },
    "assignees": [
      {
        "login": "Codertocat",
        "id": 21031067,
        "node_id": "MDQ6VXNlcjIxMDMxMDY3",
        "avatar_url": "https://avatars1.githubusercontent.com/u/21031067?v=4",
        "gravatar_id": "",
        "url": "https://api.github.com/users/Codertocat",
        "html_url": "https://github.com/Codertocat",
        "followers_url": "https://api.github.com/users/Codertocat/followers",
        "following_url": "https://api.github.com/users/Codertocat/following{/other_user}",
        "gists_url": "https://api.github.com/users/Codertocat/gists{/gist_id}",
        "starred_url": "https://api.github.com/users/Codertocat/starred{/owner}{/repo}",
        "subscriptions_url": "https://api.github.com/users/Codertocat/subscriptions",
        "organizations_url": "https://api.github.com/users/Codertocat/orgs",
        "repos_url": "https://api.github.com/users/Codertocat/repos",
        "events_url": "https://api.github.com/users/Codertocat/events{/privacy}",
        "received_events_url": "https://api.github.com/users/Codertocat/received_events",
        "type": "User",
        "site_admin": false
      }
    ],
    "milestone": {
      "url": "https://api.github.com/repos/Codertocat/Hello-World/milestones/1",
      "html_url": "https://github.com/Codertocat/Hello-World/milestone/1",
      "labels_url": "https://api.github.com/repos/Codertocat/Hello-World/milestones/1/labels",
      "id": 4317517,
      "node_id": "MDk6TWlsZXN0b25lNDMxNzUxNw==",
      "number": 1,
      "title": "v1.0",
      "description": "Add new space flight simulator",
      "creator": {
        "login": "Codertocat",
        "id": 21031067,
        "node_id": "MDQ6VXNlcjIxMDMxMDY3",
        "avatar_url": "https://avatars1.githubusercontent.com/u/21031067?v=4",
        "gravatar_id": "",
        "url": "https://api.github.com/users/Codertocat",
        "html_url": "https://github.com/Codertocat",
        "followers_url": "https://api.github.com/users/Codertocat/followers",
        "following_url": "https://api.github.com/users/Codertocat/following{/other_user}",
        "gists_url": "https://api.github.com/users/Codertocat/gists{/gist_id}",
        "starred_url": "https://api.github.com/users/Codertocat/starred{/owner}{/repo}",
        "subscriptions_url": "https://api.github.com/users/Codertocat/subscriptions",
        "organizations_url": "https://api.github.com/users/Codertocat/orgs",
        "repos_url": "https://api.github.com/users/Codertocat/repos",
        "events_url": "https://api.github.com/users/Codertocat/events{/privacy}",
        "received_events_url": "https://api.github.com/users/Codertocat/received_events",
        "type": "User",
        "site_admin": false
      },
      "open_issues": 1,
      "closed_issues": 0,
      "state": "closed",
      "created_at": "2019-05-15T15:20:17Z",
      "updated_at": "2019-05-15T15:20:18Z",
      "due_on": "2019-05-23T07:00:00Z",
      "closed_at": "2019-05-15T15:20:18Z"
    },
    "comments": 0,
    "created_at": "2019-05-15T15:20:18Z",
    "updated_at": "2019-05-15T15:20:18Z",
    "closed_at": null,
    "author_association": "OWNER",
    "body": "It looks like you accidently spelled 'commit' with two 't's."
  },
  "changes": {
  },
  "repository": {
    "id": 186853002,
    "node_id": "MDEwOlJlcG9zaXRvcnkxODY4NTMwMDI=",
    "name": "Hello-World",
    "full_name": "Codertocat/Hello-World",
    "private": false,
    "owner": {
      "login": "Codertocat",
      "id": 21031067,
      "node_id": "MDQ6VXNlcjIxMDMxMDY3",
      "avatar_url": "https://avatars1.githubusercontent.com/u/21031067?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/Codertocat",
      "html_url": "https://github.com/Codertocat",
      "followers_url": "https://api.github.com/users/Codertocat/followers",
      "following_url": "https://api.github.com/users/Codertocat/following{/other_user}",
      "gists_url": "https://api.github.com/users/Codertocat/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/Codertocat/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/Codertocat/subscriptions",
      "organizations_url": "https://api.github.com/users/Codertocat/orgs",
      "repos_url": "https://api.github.com/users/Codertocat/repos",
      "events_url": "https://api.github.com/users/Codertocat/events{/privacy}",
      "received_events_url": "https://api.github.com/users/Codertocat/received_events",
      "type": "User",
      "site_admin": false
    },
    "html_url": "https://github.com/Codertocat/Hello-World",
    "description": null,
    "fork": false,
    "url": "https://api.github.com/repos/Codertocat/Hello-World",
    "forks_url": "https://api.github.com/repos/Codertocat/Hello-World/forks",
    "keys_url": "https://api.github.com/repos/Codertocat/Hello-World/keys{/key_id}",
    "collaborators_url": "https://api.github.com/repos/Codertocat/Hello-World/collaborators{/collaborator}",
    "teams_url": "https://api.github.com/repos/Codertocat/Hello-World/teams",
    "hooks_url": "https://api.github.com/repos/Codertocat/Hello-World/hooks",
    "issue_events_url": "https://api.github.com/repos/Codertocat/Hello-World/issues/events{/number}",
    "events_url": "https://api.github.com/repos/Codertocat/Hello-World/events",
    "assignees_url": "https://api.github.com/repos/Codertocat/Hello-World/assignees{/user}",
    "branches_url": "https://api.github.com/repos/Codertocat/Hello-World/branches{/branch}",
    "tags_url": "https://api.github.com/repos/Codertocat/Hello-World/tags",
    "blobs_url": "https://api.github.com/repos/Codertocat/Hello-World/git/blobs{/sha}",
    "git_tags_url": "https://api.github.com/repos/Codertocat/Hello-World/git/tags{/sha}",
    "git_refs_url": "https://api.github.com/repos/Codertocat/Hello-World/git/refs{/sha}",
    "trees_url": "https://api.github.com/repos/Codertocat/Hello-World/git/trees{/sha}",
    "statuses_url": "https://api.github.com/repos/Codertocat/Hello-World/statuses/{sha}",
    "languages_url": "https://api.github.com/repos/Codertocat/Hello-World/languages",
    "stargazers_url": "https://api.github.com/repos/Codertocat/Hello-World/stargazers",
    "contributors_url": "https://api.github.com/repos/Codertocat/Hello-World/contributors",
    "subscribers_url": "https://api.github.com/repos/Codertocat/Hello-World/subscribers",
    "subscription_url": "https://api.github.com/repos/Codertocat/Hello-World/subscription",
    "commits_url": "https://api.github.com/repos/Codertocat/Hello-World/commits{/sha}",
    "git_commits_url": "https://api.github.com/repos/Codertocat/Hello-World/git/commits{/sha}",
    "comments_url": "https://api.github.com/repos/Codertocat/Hello-World/comments{/number}",
    "issue_comment_url": "https://api.github.com/repos/Codertocat/Hello-World/issues/comments{/number}",
    "contents_url": "https://api.github.com/repos/Codertocat/Hello-World/contents/{+path}",
    "compare_url": "https://api.github.com/repos/Codertocat/Hello-World/compare/{base}...{head}",
    "merges_url": "https://api.github.com/repos/Codertocat/Hello-World/merges",
    "archive_url": "https://api.github.com/repos/Codertocat/Hello-World/{archive_format}{/ref}",
    "downloads_url": "https://api.github.com/repos/Codertocat/Hello-World/downloads",
    "issues_url": "https://api.github.com/repos/Codertocat/Hello-World/issues{/number}",
    "pulls_url": "https://api.github.com/repos/Codertocat/Hello-World/pulls{/number}",
    "milestones_url": "https://api.github.com/repos/Codertocat/Hello-World/milestones{/number}",
    "notifications_url": "https://api.github.com/repos/Codertocat/Hello-World/notifications{?since,all,participating}",
    "labels_url": "https://api.github.com/repos/Codertocat/Hello-World/labels{/name}",
    "releases_url": "https://api.github.com/repos/Codertocat/Hello-World/releases{/id}",
    "deployments_url": "https://api.github.com/repos/Codertocat/Hello-World/deployments",
    "created_at": "2019-05-15T15:19:25Z",
    "updated_at": "2019-05-15T15:19:27Z",
    "pushed_at": "2019-05-15T15:20:13Z",
    "git_url": "git://github.com/Codertocat/Hello-World.git",
    "ssh_url": "git@github.com:Codertocat/Hello-World.git",
    "clone_url": "https://github.com/Codertocat/Hello-World.git",
    "svn_url": "https://github.com/Codertocat/Hello-World",
    "homepage": null,
    "size": 0,
    "stargazers_count": 0,
    "watchers_count": 0,
    "language": null,
    "has_issues": true,
    "has_projects": true,
    "has_downloads": true,
    "has_wiki": true,
    "has_pages": true,
    "forks_count": 0,
    "mirror_url": null,
    "archived": false,
    "disabled": false,
    "open_issues_count": 1,
    "license": null,
    "forks": 0,
    "open_issues": 1,
    "watchers": 0,
    "default_branch": "master"
  },
  "sender": {
    "login": "Codertocat",
    "id": 21031067,
    "node_id": "MDQ6VXNlcjIxMDMxMDY3",
    "avatar_url": "https://avatars1.githubusercontent.com/u/21031067?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/Codertocat",
    "html_url": "https://github.com/Codertocat",
    "followers_url": "https://api.github.com/users/Codertocat/followers",
    "following_url": "https://api.github.com/users/Codertocat/following{/other_user}",
    "gists_url": "https://api.github.com/users/Codertocat/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/Codertocat/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/Codertocat/subscriptions",
    "organizations_url": "https://api.github.com/users/Codertocat/orgs",
    "repos_url": "https://api.github.com/users/Codertocat/repos",
    "events_url": "https://api.github.com/users/Codertocat/events{/privacy}",
    "received_events_url": "https://api.github.com/users/Codertocat/received_events",
    "type": "User",
    "site_admin": false
  }
}
```
**Tipos de Response:**

| Código | Resultado                                             |
| :---:  | ----------------------------------------------------- |
| 201    | Evento validado e persistido.                         |
| 400    | Evento inválido, não foi inserido no banco de dados.  |
| 500    | Erro inesperado do servidor.                          |

### Listar Eventos Endpoint

Este endpoint lista todos os eventos associados a uma Issue.

**Endpoint:** *GET* http://localhost:7000/api/issues/_PARAM_/events

**Format:**

Este é um exemplo de response deste endpoint:

```
{
    "status": "200 OK",
    "results": [
        {
            "action": "edited",
            "issueNumber": 1,
            "title": "Spelling error in the README file",
            "body": "It looks like you accidently spelled 'commit' with two 't's.",
            "user": "Codertocat",
            "repositoryUrl": "https://api.github.com/repos/Codertocat/Hello-World",
            "createdAt": "2018-05-30T20:18:32Z",
            "updatedAt": "2018-05-30T20:18:32Z"
        }
    ]
}
```
**Tipos de Response:**

| Código | Resultado                                               |
| :---:  | ------------------------------------------------------- |
| 200    | Ao menos um Evento foi encontrado.                      |
| 400    | Parâmetro (PARAM) inválido. Deve ser um número positivo.|
| 404    | Nenhum evento encontrado.                               |
| 500    | Erro inesperado do servidor.                            |