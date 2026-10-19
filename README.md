# Oito rainhas

O problema das oito rainhas do zero em Java 21, com três buscas de custos muito diferentes e todas as soluções de cada tamanho contadas e conferidas.

São 92 soluções com oito rainhas, e todo mundo sabe que girar e espelhar não cria solução nova. Então dividir 92 por 8 deveria dar as soluções de verdade. Dá **11,5**.

```
mvn -q exec:java -Dexec.args=fundamentais
```

## As medidas

### 1. As buscas concordam, e tudo passa pela definição

| n | sem poda | com poda | inválidas | conjuntos iguais |
|---:|---:|---:|---:|---|
| 4 | 2 | 2 | 0 | sim |
| 5 | 10 | 10 | 0 | sim |
| 6 | 4 | 4 | 0 | sim |
| 7 | 40 | 40 | 0 | sim |

Podar é descartar sem olhar o resto, e é exatamente aí que se perde resposta sem perceber. Por isso a busca com poda é comparada com a que monta toda posição possível e só pergunta no fim, e cada solução é reconferida pela definição par a par.

### 2. Quantas soluções existem, por tamanho

| n | soluções | tabelado | fundamentais |
|---:|---:|---:|---:|
| 1 | 1 | 1 | 1 |
| 2 | **0** | 0 | 0 |
| 3 | **0** | 0 | 0 |
| 4 | 2 | 2 | 1 |
| 5 | 10 | 10 | 2 |
| 6 | **4** | 4 | 1 |
| 7 | 40 | 40 | 6 |
| 8 | 92 | 92 | 12 |
| 9 | 352 | 352 | 46 |
| 10 | 724 | 724 | 92 |
| 11 | 2.680 | 2.680 | 341 |

Repare no 4 logo depois do 10: o tabuleiro de **seis** tem menos soluções que o de cinco. Mais espaço não significa mais solução, e quem esperar que a sequência cresça erra no segundo passo.

### 3. Por que dividir por oito não dá a resposta

| n | soluções | dividido por 8 | fundamentais | quantas têm cada número de formas |
|---:|---:|---:|---:|---|
| 5 | 10 | 1,25 | 2 | 8 com 8 formas, 2 com 2 |
| 6 | 4 | 0,50 | 1 | 4 com 4 formas |
| 7 | 40 | 5,00 | 6 | 32 com 8, 8 com 4 |
| 8 | 92 | **11,50** | **12** | 88 com 8, 4 com 4 |
| 9 | 352 | 44,00 | 46 | 336 com 8, 16 com 4 |
| 10 | 724 | 90,50 | 92 | 712 com 8, 12 com 4 |

O 11,5 é o aviso, e ele está à vista.

Nem toda solução tem oito formas diferentes. Uma solução **simétrica** é igual a si mesma girada, e aí o grupo dela tem quatro membros em vez de oito. Com oito rainhas são 88 soluções de grupo cheio e 4 simétricas: `88/8 + 4/4 = 12`.

O caso mais extremo é o tabuleiro de seis, onde **todas** as quatro soluções são simétricas, e as quatro juntas são uma fundamental só.

### 4. O preço de perguntar tarde

| n | por casas | por colunas | com poda | ganho da poda |
|---:|---:|---:|---:|---:|
| 4 | 1.820 | 256 | 60 | 4,3x |
| 5 | 53.130 | 3.125 | 220 | 14,2x |
| 6 | | 46.656 | 894 | 52,2x |
| 7 | | 823.543 | 3.584 | 229,8x |
| 8 | | 16.777.216 | 15.720 | **1.067,3x** |

Três buscas, mesmo problema, mesmo resultado.

A primeira escolhe n casas quaisquer entre as n², que é a tradução literal do enunciado e morre no tabuleiro 6. A segunda usa uma rainha por coluna, o que já elimina uma restrição inteira só pela forma de guardar o dado.

Entre a segunda e a terceira **não há ideia nova**: há a mesma verificação, feita a cada rainha posta em vez de no fim. Isso vale mil vezes com oito rainhas, e o fator cresce com o tamanho.

### 5. As soluções simétricas

| n | soluções | com 8 formas | com 4 formas | com 2 formas | formas inválidas |
|---:|---:|---:|---:|---:|---:|
| 4 | 2 | 0 | 0 | 2 | 0 |
| 5 | 10 | 8 | 0 | 2 | 0 |
| 6 | 4 | 0 | 4 | 0 | 0 |
| 8 | 92 | 88 | 4 | 0 | 0 |
| 10 | 724 | 712 | 12 | 0 | 0 |

A última coluna é zero em toda linha: girar ou espelhar uma solução sempre dá outra solução. Se não desse, contar por classes não faria sentido nenhum e as 12 fundamentais seriam invenção.

## O que as medidas me corrigiram

**"92 soluções, mas só 12 de verdade" esconde o passo que importa.** A conta não é 92 dividido por 8. É 88 dividido por 8 mais 4 dividido por 4, e saber disso exige descobrir que existem soluções simétricas, que é a parte interessante do problema e a que some no resumo.

**A sequência não cresce.** Eu esperava que mais tabuleiro desse mais solução. O de seis tem 4 e o de cinco tem 10, e nenhuma intuição sobre espaço disponível prevê isso.

**A poda não é uma otimização, é a mesma conta mais cedo.** Ela não usa estrutura nova nem ideia nova, e mesmo assim separa 16 milhões de tentativas de 15 mil. Quase todo o custo do problema estava em terminar de montar posições que já tinham dado errado na terceira rainha.

## Rodar

```bash
mvn -q verify                                 # 20 testes
mvn -q exec:java                              # todas as medidas, menos de um segundo
mvn -q exec:java -Dexec.args=correcao         # 1
mvn -q exec:java -Dexec.args=contagem         # 2
mvn -q exec:java -Dexec.args=fundamentais     # 3
mvn -q exec:java -Dexec.args=poda             # 4
mvn -q exec:java -Dexec.args=simetria         # 5
```

Java 21, sem dependência fora do JUnit. Nenhum número deste README foi digitado à mão: todos saem de `Medidas`, e os testes conferem os que aparecem escritos aqui.
