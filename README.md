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
