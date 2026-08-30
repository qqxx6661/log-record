# log-record

[简体中文](README.md) | [English](README-EN.md) | [日本語](README-JA.md) | **Português (Brasil)**

[![CI](https://img.shields.io/github/actions/workflow/status/qqxx6661/log-record/ci.yml?branch=master&logo=github&logoColor=white)](https://github.com/qqxx6661/log-record/actions/workflows/ci.yml)
[![Codecov](https://img.shields.io/codecov/c/github/qqxx6661/log-record?logo=codecov&logoColor=white)](https://codecov.io/gh/qqxx6661/log-record/branch/master)
[![Maven Central](https://img.shields.io/maven-central/v/cn.monitor4all/log-record-starter?logo=apache-maven&logoColor=white)](https://search.maven.org/artifact/cn.monitor4all/log-record-starter)
[![License](https://img.shields.io/github/license/qqxx6661/log-record?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub stars](https://img.shields.io/github/stars/qqxx6661/log-record)](https://github.com/qqxx6661/log-record/stargazers)
[![GitHub issues](https://img.shields.io/github/issues/qqxx6661/log-record)](https://github.com/qqxx6661/log-record/issues)
[![Pull requests](https://img.shields.io/github/issues-pr/qqxx6661/log-record)](https://github.com/qqxx6661/log-record/pulls)

> **Observação**
> Este repositório foi originalmente inspirado pelo [artigo do Meituan Tech Blog sobre logs de operações](https://tech.meituan.com/2021/09/16/operational-logbook.html). Se você procura o código escrito pelo autor do artigo, consulte o [mzt-biz-log](https://github.com/mouzt/mzt-biz-log/). Este projeto implementa de forma independente a maior parte das ideias apresentadas no artigo e continua evoluindo com base em experiências de produção e no feedback da comunidade.

O `log-record` permite registrar logs de operações de forma elegante por meio de anotações Java. Ele oferece suporte a expressões SpEL, contexto e funções personalizados, comparação de objetos e muito mais. Os logs gerados podem ser processados pela própria aplicação ou enviados a uma fila de mensagens previamente configurada. São suportados Spring Boot 1, 2 e 3, do JDK 8 ao JDK 21.

Como o projeto é fornecido na forma de um Spring Boot Starter, basta adicionar uma dependência e uma anotação. A lógica de negócio permanece dedicada ao negócio:

```java
@OperationLog(
    bizType = "'followerChange'",
    bizId = "#request.orderId",
    msg = "'O usuário ' + #queryUserName(#request.userId)"
        + " + ' alterou o responsável pelo pedido de '"
        + " + #queryOldFollower(#request.orderId)"
        + " + ' para ' + #request.newFollower"
)
public Response<T> function(Request request) {
    // Lógica de negócio
}
```

Para Spring Boot 1 ou 2 (JDK 8+), adicione:

```xml
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-starter</artifactId>
    <version>{latest-version}</version>
</dependency>
```

Para Spring Boot 3 (JDK 17+), adicione:

```xml
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-springboot3-starter</artifactId>
    <version>{latest-version}</version>
</dependency>
```

Consulte o [Maven Central](https://mvnrepository.com/artifact/cn.monitor4all/log-record-starter) para encontrar a versão mais recente.

## Contexto

Você provavelmente já viu logs de operações como estes:

![](pic/sample1.png)

![](pic/sample2.png)

Como podemos registrar esses logs de forma clara no código?

A solução mais direta é chamar uma classe utilitária de logging:

```java
String template = "O usuário %s alterou o responsável pelo pedido de %s para %s";
LogUtil.log(orderNo, String.format(template, "Alice", "Bob", "Carol"), "Alice");
```

Essa abordagem mistura os detalhes do log ao código de negócio e prejudica rapidamente a legibilidade e a manutenção.

Mover a definição do log para uma anotação é um bom começo:

```java
@OperationLog(
    bizType = "'followerChange'",
    bizId = "'20211102001'",
    msg = "'A usuária Alice alterou o responsável pelo pedido de Bob para Carol'"
)
public Response<T> function(Request request) {
    // Lógica de negócio
}
```

A definição do log fica separada do método, mas os valores ainda estão fixos. Precisamos fornecer à anotação o ID do pedido, os dados do usuário, o valor anterior do banco de dados e o novo valor da requisição.

A [Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/docs/3.0.x/reference/expressions.html) permite ler os argumentos do método:

- ID do pedido: `#request.orderId`
- Novo responsável: `#request.newFollower`

```java
@OperationLog(
    bizType = "'followerChange'",
    bizId = "#request.orderId",
    msg = "'Responsável alterado de Bob para ' + #request.newFollower"
)
public Response<T> function(Request request) {
    // Lógica de negócio
}
```

Valores como o usuário atual e o responsável anterior geralmente precisam ser consultados dentro do método e não fazem parte dos argumentos. O `LogRecordContext` pode expor ao SpEL os valores calculados pela lógica de negócio:

```java
@OperationLog(
    bizType = "'followerChange'",
    bizId = "#request.orderId",
    msg = "'O usuário ' + #userName + ' alterou o responsável de '"
        + " + #oldFollower + ' para ' + #request.newFollower"
)
public Response<T> function(Request request) {
    LogRecordContext.putVariable("userName", queryUserName(request.getUserId()));
    LogRecordContext.putVariable("oldFollower", queryOldFollower(request.getOrderId()));
}
```

Essa opção é simples, porém ainda insere código relacionado a logs no método. Funções SpEL personalizadas removem essa última interferência. Registre `queryUserName` e `queryOldFollower` no avaliador e deixe as expressões chamá-las durante a criação do log:

```java
@OperationLog(
    bizType = "'followerChange'",
    bizId = "#request.orderId",
    msg = "'O usuário ' + #queryUserName(#request.userId)"
        + " + ' alterou o responsável de ' + #queryOldFollower(#request.orderId)"
        + " + ' para ' + #request.newFollower"
)
public Response<T> function(Request request) {
    // Lógica de negócio
}
```

Essa é a ideia central da biblioteca.

## Visão geral do projeto

O `log-record` usa anotações para registrar operações sem acoplar a implementação do log à lógica de negócio.

### Principais recursos

- **Integração rápida:** adicione um Spring Boot Starter ao `pom.xml`.
- **Não intrusivo:** baseado em anotações; falhas no aspecto de logging não afetam o método original.
- **Suporte a SpEL:** crie campos dinâmicos com argumentos de métodos e funções.
- **Comparação de objetos:** compare objetos da mesma classe ou até de classes diferentes.
- **Logging condicional:** use uma `condition` SpEL para decidir se o log será criado.
- **Contexto personalizado:** exponha pares arbitrários de chave e valor ao SpEL.
- **Funções personalizadas:** registre funções que podem ser chamadas pelo SpEL.
- **ID global do operador:** defina uma estratégia comum para localizar o operador atual.
- **Pipeline de dados substituível:** grave em banco, envie ao TLog, a filas ou implemente outro destino.
- **Anotações repetíveis:** defina vários logs no mesmo método.
- **Repetição e fallback:** configure novas tentativas e um SPI para falhas permanentes.
- **Momento de execução configurável:** avalie as expressões antes ou depois do método.
- **Avaliação de sucesso personalizada:** defina quando uma operação de negócio foi bem-sucedida.
- **Logging manual:** crie logs sem anotações quando necessário.
- **Pool de threads personalizado:** forneça seu próprio executor assíncrono.

### Dados do log

O `LogDTO` contém os seguintes campos:

```text
logId: UUID gerado automaticamente
bizId: ID exclusivo do negócio
bizType: tipo de negócio
exception: informações da exceção quando o método falha
operateDate: data e hora da operação
success: indica se a operação foi bem-sucedida
msg: mensagem do log
tag: etiqueta personalizada
returnStr: retorno do método serializado como texto ou JSON
executionTime: tempo de execução em milissegundos
extra: informações adicionais
operatorId: ID do operador
diffDTOList: diferenças de objetos, incluindo campos, valores e classes
```

Exemplo:

```json
{
  "bizId": "1",
  "bizType": "testObjectDiff",
  "executionTime": 0,
  "extra": "[ID do funcionário] mudou de [1] para [2]; [nome] mudou de [Alice] para [Bob]",
  "logId": "38f7f417-2cc3-40ed-8c98-2fe3ee057518",
  "msg": "[ID do funcionário] mudou de [1] para [2]; [nome] mudou de [Alice] para [Bob]",
  "operateDate": 1651116932299,
  "operatorId": "operador",
  "returnStr": "{\"id\":1,\"name\":\"Alice\"}",
  "success": true,
  "exception": null,
  "tag": "operation"
}
```

## Como usar

A integração requer apenas três passos.

### Passo 1: adicione a dependência

Spring Boot 1 ou 2 (JDK 8+):

```xml
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-starter</artifactId>
    <version>{latest-version}</version>
</dependency>
```

Spring Boot 3 (JDK 17+):

```xml
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-springboot3-starter</artifactId>
    <version>{latest-version}</version>
</dependency>
```

Encontre a versão atual no [Maven Central](https://search.maven.org/artifact/cn.monitor4all/log-record-starter). Recomenda-se a versão 1.6.x ou posterior.

### Passo 2: escolha como processar os logs

As seguintes opções são suportadas:

1. Processar os logs na própria aplicação.
2. Enviar diretamente ao RabbitMQ.
3. Enviar diretamente ao RocketMQ.
4. Enviar por meio do Spring Cloud Stream.

#### Processamento na aplicação

Implemente `IOperationLogGetService` quando o log precisar ser processado somente na mesma aplicação:

```java
@Component
public class CustomOperationLogGetService implements IOperationLogGetService {
    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("logDTO: [{}]", JSON.toJSONString(logDTO));
        return true;
    }
}
```

#### RabbitMQ

```properties
log-record.data-pipeline=rabbitMq
log-record.rabbit-mq-properties.host=localhost
log-record.rabbit-mq-properties.port=5672
log-record.rabbit-mq-properties.username=admin
log-record.rabbit-mq-properties.password=xxxxxx
log-record.rabbit-mq-properties.queue-name=logRecord
log-record.rabbit-mq-properties.routing-key=
log-record.rabbit-mq-properties.exchange-name=logRecord
```

#### RocketMQ

```properties
log-record.data-pipeline=rocketMq
log-record.rocket-mq-properties.topic=logRecord
log-record.rocket-mq-properties.tag=
log-record.rocket-mq-properties.group-name=logRecord
log-record.rocket-mq-properties.namesrv-addr=localhost:9876
```

#### Spring Cloud Stream

```properties
log-record.data-pipeline=stream
log-record.stream.destination=logRecord
log-record.stream.group=logRecord
# Quando vazio, usa o Binder definido por spring.cloud.stream.default-binder.
log-record.stream.binder=
# Exemplo com o Binder do RocketMQ
spring.cloud.stream.rocketmq.binder.name-server=127.0.0.1:9876
spring.cloud.stream.rocketmq.binder.enable-msg-trace=false
```

### Passo 3: anote a operação

```java
@OperationLog(
    bizType = "'followerChange'",
    bizId = "#request.orderId",
    msg = "'Responsável alterado de Bob para ' + #request.newFollower"
)
public Response<T> function(Request request) {
    // Lógica de negócio
}
```

## Recursos avançados

- [Uso do SpEL](#uso-do-spel)
- [Momento de avaliação do SpEL](#momento-de-avaliação-do-spel)
- [Parâmetros e funções integrados](#parâmetros-e-funções-integrados)
- [Logging condicional](#logging-condicional)
- [Informações globais do operador](#informações-globais-do-operador)
- [Contexto personalizado](#contexto-personalizado)
- [Funções personalizadas](#funções-personalizadas)
- [Avaliação de sucesso personalizada](#avaliação-de-sucesso-personalizada)
- [Comparação de objetos](#comparação-de-objetos)
- [Novas tentativas e fallback](#novas-tentativas-e-fallback)
- [Anotações repetíveis](#anotações-repetíveis)
- [Pool de threads de mensagens](#pool-de-threads-de-mensagens)
- [Registro do valor de retorno](#registro-do-valor-de-retorno)
- [Logging manual](#logging-manual)
- [Esquema de banco recomendado](#esquema-de-banco-recomendado)
- [Preenchimento de SpEL no IntelliJ IDEA](#preenchimento-de-spel-no-intellij-idea)

### Uso do SpEL

SpEL é a linguagem de expressões padrão do Spring. Consulte a [documentação do Spring Framework](https://docs.spring.io/spring-framework/docs/3.0.x/reference/expressions.html) para aprender a sintaxe.

Com exceção dos atributos booleanos `executeBeforeFunc` e `recordReturnValue`, todos os atributos de `@OperationLog` devem ser expressões SpEL válidas.

Por exemplo, ao escrever `bizType = "orderCreate"`, o SpEL interpreta o texto como uma propriedade ou um método. Use aspas internas para criar uma string literal:

```java
@OperationLog(bizType = "'orderCreate'")
```

Também é possível consultar constantes e enums:

```java
@OperationLog(bizId = "'1'", bizType = "T(cn.monitor4all.logRecord.test.bean.TestConstant).TYPE1")
@OperationLog(bizId = "'2'", bizType = "T(cn.monitor4all.logRecord.test.bean.TestEnum).TYPE1.key")
```

> Desde a versão 1.2.0, `bizType` e `tag` também exigem SpEL válido. Na versão 1.1.x e anteriores, esses campos eram strings simples.

### Momento de avaliação do SpEL

Por padrão, o aspecto de logging é executado depois do método anotado. Se o método alterar um argumento, o SpEL verá o novo valor.

Defina `executeBeforeFunc = true` para avaliar o SpEL antes do método. Nesse caso, valores criados dentro do método, incluindo o contexto personalizado e o retorno, ainda não estarão disponíveis.

```java
@OperationLog(bizId = "#keyInBiz", bizType = "'before'", executeBeforeFunc = true)
@OperationLog(bizId = "#keyInBiz", bizType = "'after'")
public void testExecuteBeforeFunc() {
    LogRecordContext.putVariable("keyInBiz", "valueInBiz");
}
```

### Parâmetros e funções integrados

Os seguintes parâmetros podem ser usados sem registro:

- `_return`: valor retornado pelo método original.
- `_errorMsg`: mensagem da exceção do método original (`throwable.getMessage()`).

```java
@OperationLog(bizId = "'1'", bizType = "'testReturn'", msg = "#_return")
```

Ambos são preenchidos depois da execução do método e, portanto, são `null` quando `executeBeforeFunc = true`. A função integrada `_DIFF` é explicada em “Comparação de objetos”.

### Logging condicional

Use o atributo `condition` para registrar o log somente quando uma expressão for verdadeira:

```java
@OperationLog(bizId = "'1'", bizType = "'notNull'", condition = "#testUser != null")
@OperationLog(bizId = "'2'", bizType = "'idIs1'", condition = "#testUser.id == 1")
@OperationLog(bizId = "'3'", bizType = "'idIs2'", condition = "#testUser.id == 2")
public void testCondition(TestUser testUser) {
}
```

Ao chamar o método com `new TestUser(1, "Alice")`, apenas os dois primeiros logs são criados.

### Informações globais do operador

O ID do operador geralmente vem de um serviço corporativo de identidade, de uma API ou de um banco de dados. Implemente `IOperatorIdGetService` para definir uma estratégia global:

```java
@Component
public class OperatorIdGetServiceImpl implements IOperatorIdGetService {
    @Override
    public String getOperatorId() {
        return queryCurrentOperatorId();
    }
}
```

Um ID informado explicitamente pela anotação tem prioridade sobre o valor global.

### Contexto personalizado

Adicione valores ao `LogRecordContext` e consulte-os pelo SpEL:

```java
LogRecordContext.putVariable("userName", queryUserName(request.getUserId()));
LogRecordContext.putVariable("oldFollower", queryOldFollower(request.getOrderId()));
```

Internamente, o `LogRecordContext` usa `TransmittableThreadLocal` para propagar o contexto da thread principal.

### Funções personalizadas

Anote tanto a classe quanto seus métodos com `@LogRecordFunc`. O atributo `value` fornece um alias opcional. Desde a versão 1.6.x, apenas métodos estáticos são suportados.

```java
@LogRecordFunc("CustomFunction")
public class CustomFunction {
    @LogRecordFunc("withAlias")
    public static String methodWithAlias() {
        return "withAlias";
    }

    @LogRecordFunc
    public static String methodWithoutAlias() {
        return "withoutAlias";
    }
}
```

Os nomes registrados são `CustomFunction_withAlias` e `CustomFunction_methodWithoutAlias`. As funções aparecem no log de inicialização da aplicação.

```java
@OperationLog(
    bizId = "#CustomFunction_withAlias()",
    bizType = "'testCustomFunction'"
)
public void testCustomFunction() {
}
```

Algumas versões 1.5.x aceitavam métodos não estáticos por adaptação reflexiva. Esse suporte foi removido na 1.6.x por ser frágil, especialmente com as restrições do JDK 11 e posteriores.

### Avaliação de sucesso personalizada

O atributo `success` permite derivar o estado do log a partir do retorno ou de outra condição de negócio. Sem uma expressão personalizada, o método é considerado bem-sucedido quando termina sem lançar uma exceção.

```java
@OperationLog(
    success = "#isSuccess",
    bizId = "#request.trade.id",
    bizType = "'createOrder'"
)
public Result<Void> createOrder(Request request) {
    Response response = tradeCreateService.create(request);
    LogRecordContext.putVariable("isSuccess", response.getIsSuccess());
    return response.getIsSuccess() ? Result.ofSuccess() : Result.ofSysError();
}
```

### Comparação de objetos

Objetos da mesma classe ou de classes diferentes podem ser comparados.

- `@LogRecordDiffField`: anote um campo e defina opcionalmente `alias` ou `ignored = true`.
- `@LogRecordDiffObject`: anote a classe e defina opcionalmente `alias`. Por padrão, todos os campos participam da comparação.

```java
@LogRecordDiffObject(alias = "Usuário")
public class TestUser {
    @LogRecordDiffField(alias = "ID do funcionário")
    private Integer id;
    private String name;
    private String job;
}
```

Chame a função integrada `_DIFF` com os objetos antigo e novo:

```java
@OperationLog(
    bizId = "'1'",
    bizType = "'testObjectDiff'",
    msg = "#_DIFF(#oldObject, #testUser)",
    extra = "#_DIFF(#oldObject, #testUser)"
)
public void testObjectDiff(TestUser testUser) {
    LogRecordContext.putVariable("oldObject", new TestUser(1, "Alice"));
}
```

Ao chamar `testObjectDiff(new TestUser(2, "Bob"))`, o resultado é salvo em `diffDTOList` e formatado nos campos `msg` e `extra`.

Campos nulos podem ser ignorados separadamente nos objetos novo e antigo:

```properties
# O padrão é false
log-record.diff-ignore-new-object-null-value=true
log-record.diff-ignore-old-object-null-value=true
```

O formato da mensagem e o separador também são configuráveis:

```properties
log-record.diff-msg-format=[${_fieldName}] mudou de [${_oldValue}] para [${_newValue}]
log-record.diff-msg-separator=;
```

`_DIFF` pode ser chamado mais de uma vez na mesma anotação. Valores simples são comparados com `equals`; valores complexos são convertidos em `JSONObject` do Fastjson e comparados como mapas.

### Novas tentativas e fallback

Tanto processadores locais quanto pipelines de mensagens podem repetir o processamento em caso de falha:

```properties
# O padrão é 0 novas tentativas, ou seja, uma execução no total.
log-record.retry.retry-times=5
```

Quando todas as tentativas falharem, implemente `cn.monitor4all.logRecord.service.LogRecordErrorHandlerService` para fornecer o fallback:

```java
@Component
public class LogRecordErrorHandlerServiceImpl implements LogRecordErrorHandlerService {
    @Override
    public void operationLogGetErrorHandler() {
        log.error("O processador local excedeu o limite de tentativas");
    }

    @Override
    public void dataPipelineErrorHandler() {
        log.error("O pipeline de dados excedeu o limite de tentativas");
    }
}
```

### Anotações repetíveis

```java
@OperationLog(bizId = "#testClass.testId", bizType = "'type1'", msg = "#testFunc(#testClass.testId)")
@OperationLog(bizId = "#testClass.testId", bizType = "'type2'", msg = "#testFunc(#testClass.testId)")
@OperationLog(bizId = "#testClass.testId", bizType = "'type3'", msg = "'Valor alterado'")
```

Várias anotações `@OperationLog` podem ser usadas no mesmo método. Os logs são emitidos na ordem das anotações, de cima para baixo.

### Pool de threads de mensagens

```properties
# Número de threads principais; padrão: 4
log-record.thread-pool.pool-size=4
# Habilitado por padrão. Quando false, usa a thread de negócio.
log-record.thread-pool.enabled=true
```

Depois de montar o `LogDTO`, o framework normalmente usa o executor para chamar o listener local ou o emissor da fila. A montagem do `LogDTO` ocorre na thread do método original.

O executor padrão equivale a:

```java
return new ThreadPoolExecutor(
    poolSize,
    poolSize,
    0L,
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1024),
    THREAD_FACTORY,
    new ThreadPoolExecutor.AbortPolicy()
);
```

Para fornecer um executor próprio, implemente `cn.monitor4all.logRecord.thread.ThreadPoolProvider`:

```java
public class CustomThreadPoolProvider implements ThreadPoolProvider {
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
        3, 3, 0L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(100),
        new CustomizableThreadFactory("custom-log-record-"),
        new ThreadPoolExecutor.AbortPolicy()
    );

    @Override
    public ThreadPoolExecutor buildLogRecordThreadPool() {
        return EXECUTOR;
    }
}
```

### Registro do valor de retorno

`@OperationLog.recordReturnValue()` controla se o retorno do método será registrado. A opção é desativada por padrão para evitar o custo de serialização de respostas muito grandes.

### Logging manual

Quando anotações não forem adequadas, use `cn.monitor4all.logRecord.util.OperationLogUtil` para registrar diretamente:

```java
LogRequest logRequest = LogRequest.builder()
    .bizId("testBizId")
    .bizType("testBuildLogRequest")
    .success(true)
    .msg("testMsg")
    .tag("testTag")
    .returnStr("testReturnStr")
    .extra("testExtra")
    .build();

OperationLogUtil.log(logRequest);
```

Recursos específicos das anotações, como SpEL e funções personalizadas, não estão disponíveis no logging manual.

### Esquema de banco recomendado

O esquema MySQL abaixo pode ser adaptado à aplicação:

```sql
CREATE TABLE `operation_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Chave primária',
  `gmt_create` datetime NOT NULL COMMENT 'Data de criação',
  `gmt_modified` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Última alteração',
  `biz_id` varchar(128) NOT NULL COMMENT 'ID do negócio',
  `biz_type` varchar(64) DEFAULT NULL COMMENT 'Tipo de negócio',
  `tag` varchar(64) DEFAULT NULL COMMENT 'Etiqueta',
  `operation_date` datetime DEFAULT NULL COMMENT 'Data da operação',
  `msg` varchar(512) DEFAULT NULL COMMENT 'Detalhes da operação',
  `extra` varchar(512) DEFAULT NULL COMMENT 'Informações adicionais',
  `operation_status` tinyint(4) DEFAULT NULL COMMENT 'Status da operação',
  `operation_time` int(11) DEFAULT NULL COMMENT 'Tempo de execução',
  `content_return` varchar(512) COMMENT 'Retorno do método',
  `content_exception` varchar(512) COMMENT 'Exceção do método',
  `operator_id` varchar(32) DEFAULT NULL COMMENT 'ID do operador',
  `operator_name` varchar(32) DEFAULT NULL COMMENT 'Nome do operador',
  PRIMARY KEY (`id`),
  KEY `idx_biz_id` (`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Log de operações';
```

### Preenchimento de SpEL no IntelliJ IDEA

O preenchimento de anotações como `@Cacheable` é fornecido pela IDE. Adicione a anotação desta biblioteca às configurações de anotações SpEL do IntelliJ IDEA para habilitar o preenchimento e a validação das expressões.

![](pic/IDEA_SpEL.png)

## Diferenças no Spring Boot 3 (JDK 17+)

O framework procura oferecer o mesmo comportamento entre as versões do Spring Boot, mas as restrições de reflexão do JDK criam uma diferença importante.

### Nomes dos parâmetros do método

No módulo do Spring Boot 3, use nomes posicionais como `p0` e `p1` nas expressões SpEL, em vez dos nomes Java dos parâmetros.

Spring Boot 1 e 2:

```java
@OperationLog(bizId = "#bizId", bizType = "'testBizIdWithSpEL'")
public void testBizIdWithSpEL(String bizId) {
}
```

Spring Boot 3:

```java
@OperationLog(bizId = "#p0", bizType = "'testBizIdWithSpEL'")
public void testBizIdWithSpEL(String bizId) {
}
```

## Casos de uso

### Logs de operações

Depois que um usuário edita um registro em um CRM, capture os valores alterados e armazene um log de auditoria legível.

### Logs de sistema

As mesmas anotações podem registrar tempo de execução, argumentos, valores de retorno e outras informações do sistema.

### Eventos analíticos do backend

Registre ações do usuário como eventos de backend, de modo semelhante aos logs de sistema.

### Notificações

As aplicações podem notificar umas às outras publicando mensagens de log para operações importantes.

## Demonstração

Os testes unitários contêm exemplos detalhados da maioria dos recursos.

Projetos completos de demonstração para Spring Boot 2 e 3 estão disponíveis em [qqxx6661/systemLog](https://github.com/qqxx6661/systemLog).

## Notas de versão

Consulte as [Releases do GitHub](https://github.com/qqxx6661/log-record/releases).

## Apêndice

### Compilação do projeto

Como o projeto está dividido em módulos pai e filho, recompile `log-record-core` no JDK desejado antes de compilar o `log-record-starter` correspondente. Caso contrário, a compilação ou os testes podem falhar.

### Publicação de uma versão

Publique `log-record-core`, `log-record-starter` e `log-record-springboot3-starter` juntos no Maven Central.

### Artigos relacionados

- [Como registrar logs de operações de forma elegante com anotações](https://mp.weixin.qq.com/s/q2qmffH8t-ou2apOa6BiPQ) (em chinês)
- [Como publicar seu projeto no Maven Central](https://mp.weixin.qq.com/s/B9LA6be_cPAKACbZot_Nrg) (em chinês)

### Acompanhe o autor

- Conta oficial no WeChat: 后端技术漫谈
- Nome do blog: 蛮三刀酱

Se este projeto for útil para você, considere deixar uma estrela. Obrigado!

### Histórico de estrelas

[![Star History Chart](https://api.star-history.com/svg?repos=qqxx6661/log-record&type=Date)](https://star-history.com/#qqxx6661/log-record&Date)
