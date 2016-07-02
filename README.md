Sobre este projeto
================

Esta aplicação busca atender a atividade 3 da disciplina de manutenção de software (2016/1).

## Como executar

Para executar a aplicação, depois de compilados os módulos maven, deve-se executar a seguinte linha de comando:
```java

java -cp executable.jar ExtrairAtividades arquivo.pdf
             |                 |            |----- arquivo para extracao
             |                 |------------------ classe principal
             |------------------------------------ executavel desta aplicação

```

## Sobre as pastas (desenvolvimento)
- Os requisitos para a aplicação está na pasta extra/requisitos
- Os exemplos de arquivo RADOC para testes estão em extra/exemplos/radoc
- O projeto gera o artefato final, depois de build maven, na pasta executable/target/executable.jar
- A execução pode ser feita por meio do comando:
