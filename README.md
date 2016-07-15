Sobre este projeto
================

Esta aplicação busca atender a atividade 3 da disciplina de manutenção de software (2016/1).

## Como executar

Para executar a aplicação, depois de compilados os módulos maven, deve-se executar a seguinte linha de comando:
```

java -jar executable.jar arquivo.pdf
             |                 |            |----- arquivo para extracao
             |                 |------------------ classe principal
             |------------------------------------ executavel desta aplicacao

```


No caso de estar usando o .jar expandido, a partir da raiz do compilado, use a seguinte chamada:
```

java  ExtrairAtividades arquivo.pdf
             |            |----- arquivo para extracao
             |------------------ classe principal, executável

```
## Sobre as pastas (desenvolvimento)
- Os requisitos para a aplicação está na pasta extra/requisitos
- Os exemplos de arquivo RADOC para testes estão em extra/exemplos
- O projeto gera o artefato final, depois de build maven, na pasta executable/target/executable.jar
- A execução pode ser feita por meio do comando:
