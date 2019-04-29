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
   "action": "labeled",
   "issue": {
      "url": "https://api.github.com/repos/acmattos/octoevents/issues/1",
      "repository_url": "https://api.github.com/repos/acmattos/octoevents",
      "labels_url": "https://api.github.com/repos/acmattos/octoevents/issues/1/labels{/name}",
      "comments_url": "https://api.github.com/repos/acmattos/octoevents/issues/1/comments",
      "events_url": "https://api.github.com/repos/acmattos/octoevents/issues/1/events",
      "html_url": "https://github.com/acmattos/octoevents/issues/1",
      "id": 372231455,
      "node_id": "MDU6SXNzdWUzNzIyMzE0NTU=",
      "number": 1,
      "title": "Test 1",
      "user": {
         "login": "acmattos",
         "id": 5035530,
         "node_id": "MDQ6VXNlcjUwMzU1MzA=",
         "avatar_url": "https://avatars1.githubusercontent.com/u/5035530?v=4",
         "gravatar_id": "",
         "url": "https://api.github.com/users/acmattos",
         "html_url": "https://github.com/acmattos",
         "followers_url": "https://api.github.com/users/acmattos/followers",
         "following_url": "https://api.github.com/users/acmattos/following{/other_user}",
         "gists_url": "https://api.github.com/users/acmattos/gists{/gist_id}",
         "starred_url": "https://api.github.com/users/acmattos/starred{/owner}{/repo}",
         "subscriptions_url": "https://api.github.com/users/acmattos/subscriptions",
         "organizations_url": "https://api.github.com/users/acmattos/orgs",
         "repos_url": "https://api.github.com/users/acmattos/repos",
         "events_url": "https://api.github.com/users/acmattos/events{/privacy}",
         "received_events_url": "https://api.github.com/users/acmattos/received_events",
         "type": "User",
         "site_admin": false
      },
      "labels": [
         {
            "id": 1098250310,
            "node_id": "MDU6TGFiZWwxMDk4MjUwMzEw",
            "url": "https://api.github.com/repos/acmattos/octoevents/labels/bug",
            "name": "bug",
            "color": "d73a4a",
            "default": true
         }
      ],
      "state": "open",
      "locked": false,
      "assignee": null,
      "assignees": [],
      "milestone": null,
      "comments": 2,
      "created_at": "2018-10-20T16:59:47Z",
      "updated_at": "2018-10-20T17:03:12Z",
      "closed_at": null,
      "author_association": "OWNER",
      "body": "First push event"
   },
   "label": {
      "id": 1098250310,
      "node_id": "MDU6TGFiZWwxMDk4MjUwMzEw",
      "url": "https://api.github.com/repos/acmattos/octoevents/labels/bug",
      "name": "bug",
      "color": "d73a4a",
      "default": true
   },
   "repository": {
      "id": 153927626,
      "node_id": "MDEwOlJlcG9zaXRvcnkxNTM5Mjc2MjY=",
      "name": "octoevents",
      "full_name": "acmattos/octoevents",
      "private": false,
      "owner": {
         "login": "acmattos",
         "id": 5035530,
         "node_id": "MDQ6VXNlcjUwMzU1MzA=",
         "avatar_url": "https://avatars1.githubusercontent.com/u/5035530?v=4",
         "gravatar_id": "",
         "url": "https://api.github.com/users/acmattos",
         "html_url": "https://github.com/acmattos",
         "followers_url": "https://api.github.com/users/acmattos/followers",
         "following_url": "https://api.github.com/users/acmattos/following{/other_user}",
         "gists_url": "https://api.github.com/users/acmattos/gists{/gist_id}",
         "starred_url": "https://api.github.com/users/acmattos/starred{/owner}{/repo}",
         "subscriptions_url": "https://api.github.com/users/acmattos/subscriptions",
         "organizations_url": "https://api.github.com/users/acmattos/orgs",
         "repos_url": "https://api.github.com/users/acmattos/repos",
         "events_url": "https://api.github.com/users/acmattos/events{/privacy}",
         "received_events_url": "https://api.github.com/users/acmattos/received_events",
         "type": "User",
         "site_admin": false
      },
      "html_url": "https://github.com/acmattos/octoevents",
      "description": " Octo Events is an application that listens to Github Events via webhooks and expose by an api for later use.",
      "fork": false,
      "url": "https://api.github.com/repos/acmattos/octoevents",
      "forks_url": "https://api.github.com/repos/acmattos/octoevents/forks",
      "keys_url": "https://api.github.com/repos/acmattos/octoevents/keys{/key_id}",
      "collaborators_url": "https://api.github.com/repos/acmattos/octoevents/collaborators{/collaborator}",
      "teams_url": "https://api.github.com/repos/acmattos/octoevents/teams",
      "hooks_url": "https://api.github.com/repos/acmattos/octoevents/hooks",
      "issue_events_url": "https://api.github.com/repos/acmattos/octoevents/issues/events{/number}",
      "events_url": "https://api.github.com/repos/acmattos/octoevents/events",
      "assignees_url": "https://api.github.com/repos/acmattos/octoevents/assignees{/user}",
      "branches_url": "https://api.github.com/repos/acmattos/octoevents/branches{/branch}",
      "tags_url": "https://api.github.com/repos/acmattos/octoevents/tags",
      "blobs_url": "https://api.github.com/repos/acmattos/octoevents/git/blobs{/sha}",
      "git_tags_url": "https://api.github.com/repos/acmattos/octoevents/git/tags{/sha}",
      "git_refs_url": "https://api.github.com/repos/acmattos/octoevents/git/refs{/sha}",
      "trees_url": "https://api.github.com/repos/acmattos/octoevents/git/trees{/sha}",
      "statuses_url": "https://api.github.com/repos/acmattos/octoevents/statuses/{sha}",
      "languages_url": "https://api.github.com/repos/acmattos/octoevents/languages",
      "stargazers_url": "https://api.github.com/repos/acmattos/octoevents/stargazers",
      "contributors_url": "https://api.github.com/repos/acmattos/octoevents/contributors",
      "subscribers_url": "https://api.github.com/repos/acmattos/octoevents/subscribers",
      "subscription_url": "https://api.github.com/repos/acmattos/octoevents/subscription",
      "commits_url": "https://api.github.com/repos/acmattos/octoevents/commits{/sha}",
      "git_commits_url": "https://api.github.com/repos/acmattos/octoevents/git/commits{/sha}",
      "comments_url": "https://api.github.com/repos/acmattos/octoevents/comments{/number}",
      "issue_comment_url": "https://api.github.com/repos/acmattos/octoevents/issues/comments{/number}",
      "contents_url": "https://api.github.com/repos/acmattos/octoevents/contents/{+path}",
      "compare_url": "https://api.github.com/repos/acmattos/octoevents/compare/{base}...{head}",
      "merges_url": "https://api.github.com/repos/acmattos/octoevents/merges",
      "archive_url": "https://api.github.com/repos/acmattos/octoevents/{archive_format}{/ref}",
      "downloads_url": "https://api.github.com/repos/acmattos/octoevents/downloads",
      "issues_url": "https://api.github.com/repos/acmattos/octoevents/issues{/number}",
      "pulls_url": "https://api.github.com/repos/acmattos/octoevents/pulls{/number}",
      "milestones_url": "https://api.github.com/repos/acmattos/octoevents/milestones{/number}",
      "notifications_url": "https://api.github.com/repos/acmattos/octoevents/notifications{?since,all,participating}",
      "labels_url": "https://api.github.com/repos/acmattos/octoevents/labels{/name}",
      "releases_url": "https://api.github.com/repos/acmattos/octoevents/releases{/id}",
      "deployments_url": "https://api.github.com/repos/acmattos/octoevents/deployments",
      "created_at": "2018-10-20T16:54:31Z",
      "updated_at": "2018-10-20T16:54:33Z",
      "pushed_at": "2018-10-20T16:54:32Z",
      "git_url": "git://github.com/acmattos/octoevents.git",
      "ssh_url": "git@github.com:acmattos/octoevents.git",
      "clone_url": "https://github.com/acmattos/octoevents.git",
      "svn_url": "https://github.com/acmattos/octoevents",
      "homepage": null,
      "size": 0,
      "stargazers_count": 0,
      "watchers_count": 0,
      "language": null,
      "has_issues": true,
      "has_projects": true,
      "has_downloads": true,
      "has_wiki": true,
      "has_pages": false,
      "forks_count": 0,
      "mirror_url": null,
      "archived": false,
      "open_issues_count": 1,
      "license": {
         "key": "mit",
         "name": "MIT License",
         "spdx_id": "MIT",
         "url": "https://api.github.com/licenses/mit",
         "node_id": "MDc6TGljZW5zZTEz"
      },
      "forks": 0,
      "open_issues": 1,
      "watchers": 0,
      "default_branch": "master"
   },
   "sender": {
      "login": "acmattos",
      "id": 5035530,
      "node_id": "MDQ6VXNlcjUwMzU1MzA=",
      "avatar_url": "https://avatars1.githubusercontent.com/u/5035530?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/acmattos",
      "html_url": "https://github.com/acmattos",
      "followers_url": "https://api.github.com/users/acmattos/followers",
      "following_url": "https://api.github.com/users/acmattos/following{/other_user}",
      "gists_url": "https://api.github.com/users/acmattos/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/acmattos/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/acmattos/subscriptions",
      "organizations_url": "https://api.github.com/users/acmattos/orgs",
      "repos_url": "https://api.github.com/users/acmattos/repos",
      "events_url": "https://api.github.com/users/acmattos/events{/privacy}",
      "received_events_url": "https://api.github.com/users/acmattos/received_events",
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

**Endpoint:** *GET* http://localhost:7000/issues/PARAM/events

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