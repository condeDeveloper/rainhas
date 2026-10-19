package br.com.conde.rainhas;

/**
 * UMA POSIÇÃO COM UMA RAINHA POR COLUNA.
 *
 * <p>A representação já carrega metade do problema resolvido, e vale dizer isso em voz alta:
 * guardar "em que linha está a rainha da coluna c" torna impossível pôr duas rainhas na mesma
 * coluna. Essa restrição some do código porque sumiu da representação, não porque alguém a
 * verificou.
 *
 * <p>É uma escolha com consequência medida: a quarta medida compara esta representação com a
 * ingênua, que espalha rainhas por casas livres, e a diferença de trabalho é de várias ordens
 * de grandeza.
 *
 * <p>Sobram duas restrições para conferir: mesma linha e mesma diagonal. Duas rainhas estão na
 * mesma diagonal quando a diferença das colunas é igual, em valor absoluto, à diferença das
 * linhas.
 */
public record Tabuleiro(int[] linhaDaColuna) {

    public Tabuleiro {
        linhaDaColuna = linhaDaColuna.clone();
    }

    public int tamanho() {
        return linhaDaColuna.length;
    }

    public int linha(int coluna) {
        return linhaDaColuna[coluna];
    }

    /** Duas rainhas se atacam: mesma linha ou mesma diagonal. */
    public static boolean seAtacam(int colunaA, int linhaA, int colunaB, int linhaB) {
        return linhaA == linhaB || Math.abs(colunaA - colunaB) == Math.abs(linhaA - linhaB);
    }

    /**
     * A DEFINIÇÃO, conferida par a par.
     *
     * <p>Escrita do jeito mais burro possível, comparando todos os pares, de propósito: é ela
     * que julga o que a busca produz, e um juiz esperto pode errar junto com quem ele julga.
     */
    public boolean valido() {
        for (int a = 0; a < tamanho(); a++) {
            if (linhaDaColuna[a] < 0 || linhaDaColuna[a] >= tamanho()) return false;

            for (int b = a + 1; b < tamanho(); b++)
                if (seAtacam(a, linhaDaColuna[a], b, linhaDaColuna[b])) return false;
        }

        return true;
    }

    /* ===================== as oito simetrias ===================== */

    /** Girar o tabuleiro um quarto de volta. */
    public Tabuleiro girado() {
        int n = tamanho();

        int[] novo = new int[n];

        for (int coluna = 0; coluna < n; coluna++) novo[linhaDaColuna[coluna]] = n - 1 - coluna;

        return new Tabuleiro(novo);
    }

    /** Espelhar da esquerda para a direita. */
    public Tabuleiro espelhado() {
        int n = tamanho();

        int[] novo = new int[n];

        for (int coluna = 0; coluna < n; coluna++) novo[n - 1 - coluna] = linhaDaColuna[coluna];

        return new Tabuleiro(novo);
    }

    /** As oito formas: quatro giros, e os mesmos quatro espelhados. */
    public java.util.List<Tabuleiro> todasAsFormas() {
        java.util.List<Tabuleiro> formas = new java.util.ArrayList<>();

        Tabuleiro atual = this;

        for (int giro = 0; giro < 4; giro++) {
            formas.add(atual);
            formas.add(atual.espelhado());

            atual = atual.girado();
        }

        return formas;
    }

    /**
     * A forma canônica: a menor das oito, na ordem de leitura.
     *
     * <p>É o que separa as 92 soluções do tabuleiro de 8 das 12 soluções FUNDAMENTAIS. Duas
     * posições são a mesma solução exatamente quando têm a mesma canônica.
     */
    public Tabuleiro canonico() {
        Tabuleiro menor = null;

        for (Tabuleiro forma : todasAsFormas())
            if (menor == null || menorQue(forma, menor)) menor = forma;

        return menor;
    }

    private static boolean menorQue(Tabuleiro a, Tabuleiro b) {
        for (int coluna = 0; coluna < a.tamanho(); coluna++) {
            if (a.linha(coluna) < b.linha(coluna)) return true;
            if (a.linha(coluna) > b.linha(coluna)) return false;
        }

        return false;
    }

    /** Quantas das oito formas são diferentes entre si. */
    public int quantasFormasDistintas() {
        java.util.Set<String> distintas = new java.util.HashSet<>();

        for (Tabuleiro forma : todasAsFormas()) distintas.add(forma.chave());

        return distintas.size();
    }

    public String chave() {
        StringBuilder saida = new StringBuilder();

        for (int linha : linhaDaColuna) saida.append(linha).append(',');

        return saida.toString();
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Tabuleiro t
                && java.util.Arrays.equals(t.linhaDaColuna(), linhaDaColuna);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(linhaDaColuna);
    }

    @Override
    public String toString() {
        StringBuilder saida = new StringBuilder();

        for (int linha = 0; linha < tamanho(); linha++) {
            for (int coluna = 0; coluna < tamanho(); coluna++)
                saida.append(linhaDaColuna[coluna] == linha ? "Q " : ". ");

            saida.append('\n');
        }

        return saida.toString();
    }
}
