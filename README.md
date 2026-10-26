# DuoCode — Trabalho 3

App Android de quiz para programadores, no estilo Duolingo, feito em Kotlin com Jetpack Compose.

Neste trabalho o app passou a guardar os dados num banco local com **Room**: as linguagens e as questões que você adiciona, edita ou remove continuam lá mesmo depois de fechar o app.

## Integrantes

- André Caires
- Gustavo Cáceres
- Brenno Soares

## Como rodar

1. Requisitos:
   - Android Studio atualizado (o projeto usa Android Gradle Plugin 9.4 e Gradle 9.6)
   - Android SDK 37 instalado (Tools → SDK Manager)
   - Emulador ou celular com Android 7.0 (API 24) ou superior
2. Clone o repositório:
   ```
   git clone https://github.com/Gustavo-Caceres/Duo_Code_Parte_Tres.git
   ```
3. Abra a pasta `Duo_Code_Parte_Tres` no Android Studio e espere o Gradle Sync terminar. Na primeira vez ele baixa o Room, o KSP e o JDK usado pelo Gradle, então pode demorar alguns minutos.
4. Escolha um emulador (ou conecte um celular com depuração USB ativada) e clique em **Run ▶** (`Shift+F10`).

Na primeira vez que o app abre, o banco é criado com 4 linguagens e 40 questões de exemplo.

### Se o app abrir sem nenhum dado

Se o celular já tinha uma versão antiga do app instalada, o banco antigo é recriado automaticamente (versão 2 com `fallbackToDestructiveMigration`). Se mesmo assim as listas aparecerem vazias, desinstale o app do celular e rode de novo.

## Documentação do processo e das decisões

> Os prints ficam na pasta `docs/`. Os vídeos estão linkados em cada seção.

### Teste de persistência

O teste que prova que o Room está funcionando: adicionar um item, fechar o app completamente (tirar da lista de apps recentes) e abrir de novo. O item continua lá.

> 🎥 **[VÍDEO: adicionar uma linguagem → fechar o app pelos recentes → reabrir → a linguagem continua na lista]**
>
> Link do vídeo: _colar aqui_

| Antes de fechar: linguagem "TEste" adicionada | Depois de reabrir o app: a questão #41, criada para a linguagem "TEste", continua salva |
|---|---|
| <img src="docs/09-antes-de-fechar.png" width="260"> | <img src="docs/10-depois-de-reabrir.png" width="380"> |

> 🎥 **[VÍDEO: editar um item → fechar e reabrir → a edição continua salva]**
>
> 🎥 **[VÍDEO: remover um item → fechar e reabrir → o item não volta]**

### Como estava o projeto no Trabalho 2, e o que mudou

No Trabalho 2 o app já tinha as mesmas telas, a navegação e os formulários. Só que todos os dados viviam em memória:

- As linguagens e as questões eram duas listas fixas, criadas com `mutableStateListOf(...)` direto no `AppNavigation.kt`. Eram 4 linguagens e 7 questões escritas à mão no código.
- Cada item tinha o `id` escrito à mão (`id = 1`, `id = 2`...). Ao adicionar um item novo, o `id` era calculado na tela pegando o maior e somando 1.
- Remover uma linguagem era `linguagens.remove(...)`, e as questões dela tinham que ser removidas na mão com `questoes.removeAll { ... }`.
- Não existia edição de item, só adicionar e remover.
- As telas de Detalhes recebiam a lista inteira e procuravam o item com `linguagens.find { it.id == id }`.
- As alternativas do quiz estavam fixas dentro da `PerguntaScreen`.

Bastava fechar o app para tudo voltar ao estado inicial: o que era adicionado ou removido se perdia.

> 📸 **[PRINT: app do Trabalho 2, se tiverem]**

No Trabalho 3 a mudança foi feita em etapas, cada uma num commit ou pull request:

1. **Configurar Room e KSP.** Primeiro sincronizamos o plugin `com.google.devtools.ksp` e só depois adicionamos `room-runtime`, `room-ktx` e `ksp(room-compiler)`, na ordem vista em aula.
2. **Transformar as data classes em `@Entity`.** Aproveitamos as duas data classes do Trabalho 2 (`Linguagem` e `Questao`). Cada uma ganhou `@Entity(tableName = ...)` e `@PrimaryKey(autoGenerate = true) val id: Int = 0`.
3. **Criar os DAOs e o `AppDatabase`.** Um DAO para cada entidade e o banco em padrão Singleton.
4. **Ligar as listas ao banco.** As listas fixas saíram do código. Agora, ao abrir o app, os dados de exemplo são inseridos só se o banco estiver vazio (`DadosIniciais.kt`). Cada tela de lista busca os dados com `LaunchedEffect(Unit)` + `dao.buscarTodos()`.
5. **Implementar a edição.** Criamos os botões e os diálogos de editar linguagem e questão, salvando com `dao.atualizar(...)`.
6. **Telas de Detalhes lendo do banco.** Em vez de procurar na lista, as duas telas de Detalhes recebem só o `id` pela navegação e buscam o item com `buscarPorId(id)`.

> 🎥 **[VÍDEO: tour pelo app — listas, adicionar, editar, remover, abrir os detalhes]**

#### O app funcionando com o banco

| Linguagens (lidas do banco) | Adicionar questão | Editar linguagem (`atualizar`) |
|---|---|---|
| <img src="docs/02-linguagens.png" width="240"> | <img src="docs/03-questoes-adicionar.png" width="240"> | <img src="docs/05-editar.png" width="240"> |

| Detalhe da linguagem (`buscarPorId` + `buscarPorLinguagem`) | Detalhe da questão (`buscarPorId`) | Remover item (`deletar`) |
|---|---|---|
| <img src="docs/07-detalhe-linguagem.png" width="240"> | <img src="docs/08-detalhe-questao.png" width="240"> | 📸 _[PRINT: confirmação de remover — salvar como `docs/06-remover.png`]_ |

| Trilha | Escolher questões para praticar | Quiz com as alternativas do banco | Perfil |
|---|---|---|---|
| <img src="docs/01-trilha.png" width="180"> | <img src="docs/12-praticar-escolha.png" width="180"> | <img src="docs/13-pergunta.png" width="180"> | <img src="docs/14-perfil.png" width="180"> |

### Entity, DAO e Database criados, e por que esses campos

**`Linguagem`** (tabela `linguagens`): `id`, `nome`, `sigla`, `descricao`.
- `nome` e `sigla` são o que aparece na tela. A sigla vira o "ícone" do card (JS, PY, KT, SQL).
- `descricao` é opcional e aparece na tela de detalhes.

**`Questao`** (tabela `questoes`): `id`, `enunciado`, `resposta`, `linguagemId`, `dificuldade`, `resolvida`, `alternativasErradas`.
- `enunciado` e `resposta` são a pergunta e a alternativa certa.
- `linguagemId` liga a questão à linguagem dela. Virou uma **chave estrangeira** com `onDelete = CASCADE`: ao remover uma linguagem, o próprio banco apaga as questões dela, sem precisar do `removeAll` que a gente fazia no Trabalho 2.
- `dificuldade` (fácil, médio, difícil) é usada para mostrar a etiqueta no card e no quiz.
- `resolvida` marca se o usuário já acertou a questão. Agora isso também fica salvo depois de fechar o app.
- `alternativasErradas` foi um campo novo do Trabalho 3. Antes as alternativas do quiz eram fixas no código. Agora cada questão guarda as suas. Como o Room não sabe salvar `List<String>`, criamos um `@TypeConverter` (`Conversores.kt`) que junta a lista num texto só e separa de novo ao ler.

**`AppDatabase`**: reúne as duas entidades, expõe `linguagemDao()` e `questaoDao()` e segue o padrão Singleton (`companion object` + `@Volatile` + `synchronized`). Assim o app inteiro usa uma única conexão com o banco `duocode_database`.

> 📸 **[PRINT: Models.kt com as anotações @Entity]**
>
> 📸 **[PRINT: App Inspection → Database Inspector do Android Studio mostrando as tabelas `linguagens` e `questoes` com dados]**

### Como estruturamos as queries do DAO

Os dois DAOs têm os 4 métodos básicos, todos `suspend fun`: `inserir` (`@Insert`), `buscarTodos` (`@Query("SELECT * FROM ...")`), `atualizar` (`@Update`) e `deletar` (`@Delete`).

Além deles, criamos:

| Método | Query | Para que serve |
|---|---|---|
| `LinguagemDao.buscarPorId(id)` | `SELECT * FROM linguagens WHERE id = :id` | Tela de detalhes da linguagem e tela de detalhes da questão (para mostrar o nome da linguagem) |
| `QuestaoDao.buscarPorId(id)` | `SELECT * FROM questoes WHERE id = :id` | Tela de detalhes da questão |
| `QuestaoDao.buscarPorLinguagem(linguagemId)` | `SELECT * FROM questoes WHERE linguagemId = :linguagemId` | Lista de questões dentro do detalhe de uma linguagem |

O `buscarPorId` retorna `Linguagem?` / `Questao?` (pode ser nulo). Se o item tiver sido apagado, a tela mostra "não encontrada" em vez de fechar o app.

Toda chamada ao DAO fica dentro de `CoroutineScope(Dispatchers.IO).launch { ... }` com `try/catch` e `Log.e(...)`. Depois de inserir, editar ou remover, a tela chama `recarregarListas(...)`, que busca tudo de novo no banco. Essa função atualiza as listas da tela no `Dispatchers.Main`, porque o Compose espera que o estado da interface seja alterado na thread principal.

### Dificuldades em migrar de `mutableStateListOf` para o Room

- **O `id` deixou de ser nosso.** Com `autoGenerate = true` quem cria o `id` é o banco, então não dava mais para escrever as questões de exemplo com `linguagemId = 1`. A solução foi inserir as linguagens primeiro, buscar de volta e montar um mapa `sigla → id`. As questões de exemplo usam esse mapa.
- **Os dados de exemplo duplicavam.** Se o seed rodasse toda vez que o app abria, as linguagens apareciam repetidas. Colocamos um `if (linguagemDao.buscarTodos().isNotEmpty()) return` no começo do `inserirDadosIniciais`.
- **Mudar uma Entity quebra o banco que já existe.** Quando adicionamos o campo `alternativasErradas` na `Questao`, o celular que já tinha o banco da versão 1 não abria mais o app, porque o Room percebe que a tabela mudou. Primeiro tentamos uma `Migration(1, 2)` com `ALTER TABLE`. Depois voltamos para a versão 1 enquanto o modelo ainda mudava. No fim deixamos a **versão 2** com `fallbackToDestructiveMigration(true)`, que recria o banco quando a versão muda. Como os dados são só de exemplo, perder o banco antigo nessa troca não tem problema.
- **As listas não atualizavam sozinhas.** Antes, `linguagens.add(...)` já mudava a tela na hora. Com o banco, inserir no Room não muda a lista da tela. Por isso toda operação termina com `recarregarListas(...)`, que busca do banco de novo.
- **Detalhes sem o objeto pronto.** Como a tela de Detalhes passou a buscar o item pelo `id`, ela precisa lidar com o tempo de carregamento e com o caso de não encontrar o item. Adicionamos um estado `carregando` e uma mensagem para quando o item não existe.

> ✏️ _Completar com algum erro específico que apareceu (mensagem do Logcat, print do app fechando etc.)._

### Dificuldades de cada integrante

Divisão do trabalho, pelo histórico de commits:

- **Gustavo Cáceres:** configurou Room e KSP, criou as entidades, os DAOs e o `AppDatabase`, ligou as listas ao banco, subiu a versão do banco para 2 e escreveu o README.
- **André Caires:** criou a edição de linguagens e questões (`atualizar`), as alternativas erradas guardadas no banco, o seed com as 40 questões e as melhorias de UX.
- **Brenno Soares:** fez as telas de Detalhes buscarem a linguagem e a questão pelo `id` direto no banco.

> ✏️ _Cada um escreve aqui, com suas palavras, onde teve dificuldade e como resolveu._
>
> - **Gustavo:** _..._
> - **André:** _..._
> - **Brenno:** _..._

## Banco de dados (Room)

### Entidades

| Entidade | Tabela | Campos |
|---|---|---|
| `Linguagem` | `linguagens` | `id`, `nome`, `sigla`, `descricao` |
| `Questao` | `questoes` | `id`, `enunciado`, `resposta`, `linguagemId`, `dificuldade`, `resolvida`, `alternativasErradas` |

- O `id` das duas é gerado pelo banco (`@PrimaryKey(autoGenerate = true)`).
- `Questao.linguagemId` é uma chave estrangeira para `Linguagem` com `onDelete = CASCADE`: ao remover uma linguagem, as questões dela são removidas junto.
- `dificuldade` é um enum (`FACIL`, `MEDIO`, `DIFICIL`).
- `alternativasErradas` é uma lista de textos, guardada com um `@TypeConverter` (`Conversores.kt`).

### DAOs

| DAO | Métodos |
|---|---|
| `LinguagemDao` | `inserir`, `buscarTodos`, `buscarPorId`, `atualizar`, `deletar` |
| `QuestaoDao` | `inserir`, `buscarTodos`, `buscarPorId`, `buscarPorLinguagem`, `atualizar`, `deletar` |

Todos são `suspend fun` e são chamados dentro de `CoroutineScope(Dispatchers.IO).launch { ... }`, com `try/catch` e `Log.e` em caso de erro.

### AppDatabase

`AppDatabase` (`data/AppDatabase.kt`) junta as duas entidades e segue o padrão Singleton (`companion object` + `@Volatile` + `synchronized`), para o app inteiro usar a mesma instância do banco `duocode_database`.

## Telas

O app tem 8 telas, todas navegáveis pelo `NavHost` em `AppNavigation.kt`:

| Tela | Rota | O que faz com o banco |
|---|---|---|
| Trilha | `trilha` | Abre o quiz com questões de todas as linguagens |
| Linguagens | `linguagens` | Lista, adiciona, edita e remove linguagens |
| Questões | `questoes` | Lista, adiciona, edita e remove questões |
| Perfil | `perfil` | — |
| Detalhe da linguagem | `detalhe_linguagem/{id}` | Busca a linguagem e as questões dela pelo id; marca questões como resolvidas e cria questões |
| Detalhe da questão | `detalhe_questao/{id}` | Busca a questão e a linguagem dela pelo id; marca como resolvida |
| Pergunta (quiz) | `pergunta/{linguagemId}` | Monta o quiz com as questões do banco e marca as acertadas como resolvidas |
| Resultado | `resultado/{acertos}/{total}/{linguagens}` | Mostra acertos e XP da lição |

A navegação principal (Trilha, Linguagens, Questões, Perfil) fica num `BottomNavigation`.

## Estrutura do projeto

```
app/src/main/java/com/example/duocode/
├── MainActivity.kt
├── data/
│   ├── AppDatabase.kt
│   ├── Conversores.kt
│   ├── DadosIniciais.kt
│   ├── LinguagemDao.kt
│   └── QuestaoDao.kt
├── model/Models.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── Rotas.kt
└── ui/
    ├── screens/
    └── theme/
```

## Tecnologias

- Kotlin 2.2.10
- Jetpack Compose (BOM 2026.02.01) com Material 3
- Navigation Compose 2.10.0
- Room 2.8.5 com KSP 2.3.12
- minSdk 24 · targetSdk/compileSdk 37
