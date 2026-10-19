package br.com.conde.rainhas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * AS TRÊS MANEIRAS DE PROCURAR, da mais burra à que se usa.
 *
 * <p>Elas existem juntas porque o assunto deste repositório não é "quantas soluções há", que é
 * um número tabelado desde 1850. É quanto custa achá-las, e o custo muda por fatores de
 * milhares entre as três, resolvendo exatamente o mesmo problema.
 */
public final class Busca {

    private Busca() {}

    /** Quantas posições a busca encostou, que é o trabalho de verdade. */
    public static long tentativas;

    /**
     * FORÇA BRUTA SOBRE CASAS: escolhe n casas quaisquer entre as n² e testa.
     *
     * <p>É a tradução literal do enunciado, e é inviável depressa: com 8 rainhas em 64 casas
     * são 4,4 bilhões de escolhas. Esta implementação para no tabuleiro 5, e o ponto dela é
     * dar uma referência para as outras duas.
     */
    public static List<Tabuleiro> porCasas(int n) {
        tentativas = 0;

        List<Tabuleiro> achados = new ArrayList<>();

        escolherCasas(n, 0, 0, new int[n], achados);

        return achados;
    }

    private static void escolherCasas(int n, int casa, int postas, int[] onde,
            List<Tabuleiro> achados) {
        if (postas == n) {
            tentativas++;

            int[] linhaDaColuna = new int[n];

            java.util.Arrays.fill(linhaDaColuna, -1);

            for (int indice = 0; indice < n; indice++) {
                int coluna = onde[indice] % n;

                /* Duas na mesma coluna: esta posicao nao cabe na representacao, e tambem
                 * nao seria valida, entao descartar aqui nao perde solucao. */
                if (linhaDaColuna[coluna] >= 0) return;

                linhaDaColuna[coluna] = onde[indice] / n;
            }

            Tabuleiro candidato = new Tabuleiro(linhaDaColuna);

            if (candidato.valido()) achados.add(candidato);

            return;
        }

        if (casa >= n * n) return;

        /* Nao cabe mais: faltam mais rainhas do que casas restantes. */
        if (n * n - casa < n - postas) return;

        onde[postas] = casa;

        escolherCasas(n, casa + 1, postas + 1, onde, achados);
        escolherCasas(n, casa + 1, postas, onde, achados);
    }

    /**
     * UMA POR COLUNA, TESTANDO SÓ NO FIM.
     *
     * <p>Com a representação de uma rainha por coluna, são {@code n^n} posições. Melhor que as
     * casas, e ainda sem corte nenhum: ela monta a posição inteira e só então pergunta se
     * presta.
     */
    public static List<Tabuleiro> porColunas(int n) {
        tentativas = 0;

        List<Tabuleiro> achados = new ArrayList<>();

        montarTudo(n, new int[n], 0, achados);

        return achados;
    }

    private static void montarTudo(int n, int[] atual, int coluna, List<Tabuleiro> achados) {
        if (coluna == n) {
            tentativas++;

            Tabuleiro candidato = new Tabuleiro(atual);

            if (candidato.valido()) achados.add(candidato);

            return;
        }

        for (int linha = 0; linha < n; linha++) {
            atual[coluna] = linha;

            montarTudo(n, atual, coluna + 1, achados);
        }
    }

    /**
     * COM PODA: desiste assim que duas rainhas se atacam.
     *
     * <p>A única diferença para a anterior é perguntar mais cedo. Não há ideia nova, não há
     * estrutura nova: é a mesma verificação, feita antes de continuar em vez de depois de
     * terminar. A quarta medida mostra que isso vale fatores de milhares.
     */
    public static List<Tabuleiro> comPoda(int n) {
        tentativas = 0;

        List<Tabuleiro> achados = new ArrayList<>();

        cada(n, achados::add);

        return achados;
    }

    /** O mesmo, sem guardar, para tabuleiros grandes. */
    public static void cada(int n, Consumer<Tabuleiro> aoAchar) {
        tentativas = 0;

        podar(n, new int[n], 0, aoAchar);
    }

    private static void podar(int n, int[] atual, int coluna, Consumer<Tabuleiro> aoAchar) {
        if (coluna == n) {
            aoAchar.accept(new Tabuleiro(atual));

            return;
        }

        for (int linha = 0; linha < n; linha++) {
            tentativas++;

            boolean cabe = true;

            for (int anterior = 0; anterior < coluna && cabe; anterior++)
                if (Tabuleiro.seAtacam(anterior, atual[anterior], coluna, linha)) cabe = false;

            if (!cabe) continue;

            atual[coluna] = linha;

            podar(n, atual, coluna + 1, aoAchar);
        }
    }

    /** Só a contagem, sem guardar nada. */
    public static int contar(int n) {
        int[] quantas = {0};

        cada(n, t -> quantas[0]++);

        return quantas[0];
    }
}
