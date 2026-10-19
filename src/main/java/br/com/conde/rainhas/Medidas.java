package br.com.conde.rainhas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * AS MEDIDAS: todas as soluções de cada tamanho, achadas e conferidas, não citadas.
 */
public final class Medidas {

    private Medidas() {}

    /** Até onde a busca com poda vai nas tabelas. */
    public static final int ATE = 11;

    private static final Map<Integer, List<Tabuleiro>> guardadas = new TreeMap<>();

    /** As soluções de um tamanho, achadas uma vez só. */
    public static List<Tabuleiro> solucoes(int n) {
        return guardadas.computeIfAbsent(n, tamanho -> List.copyOf(Busca.comPoda(tamanho)));
    }

    /* ===================== 1. correcao ===================== */

    public record Correcao(int n, int porColunas, int comPoda, int invalidas, boolean iguais) {}

    private static List<Correcao> correcao;

    /**
     * AS BUSCAS CONCORDAM, E TUDO PASSA PELA DEFINIÇÃO.
     *
     * <p>A busca com poda descarta posições pelo caminho, e descartar cedo é onde mora o risco
     * de perder solução sem avisar. Por isso ela é comparada com a busca sem poda nenhuma, que
     * monta toda posição possível e só pergunta no fim.
     *
     * <p>E cada solução achada é reconferida pela definição par a par, que compara todas as
     * rainhas com todas as outras.
     */
    public static List<Correcao> correcao() {
        if (correcao != null) return correcao;

        List<Correcao> saida = new ArrayList<>();

        for (int n = 1; n <= 7; n++) {
            Set<String> semPoda = new HashSet<>();

            for (Tabuleiro t : Busca.porColunas(n)) semPoda.add(t.chave());

            Set<String> comPoda = new HashSet<>();

            int invalidas = 0;

            for (Tabuleiro t : solucoes(n)) {
                comPoda.add(t.chave());

                if (!t.valido()) invalidas++;
            }

            saida.add(new Correcao(n, semPoda.size(), comPoda.size(), invalidas,
                    semPoda.equals(comPoda)));
        }

        return correcao = List.copyOf(saida);
    }

    public static Correcao correcao(int n) {
        return correcao().stream().filter(x -> x.n() == n).findFirst().orElseThrow();
    }

    /* ===================== 2. contagem ===================== */

    public record Contagem(int n, int solucoes, int fundamentais, long tentativas) {}

    private static List<Contagem> contagem;

    /**
     * QUANTAS SOLUÇÕES EXISTEM, POR TAMANHO.
     *
     * <p>A sequência é 1, 0, 0, 2, 10, 4, 40, 92, e a parte que incomoda é o 4 depois do 10: o
     * tabuleiro de 6 tem MENOS soluções que o de 5. Não há nada de monótono aqui, e quem
     * esperar que mais espaço signifique mais soluções erra no segundo passo.
     */
    public static List<Contagem> contagem() {
        if (contagem != null) return contagem;

        List<Contagem> saida = new ArrayList<>();

        for (int n = 1; n <= ATE; n++) {
            List<Tabuleiro> todas = solucoes(n);

            long tentativas = Busca.contar(n) >= 0 ? Busca.tentativas : 0;

            Set<String> canonicas = new HashSet<>();

            for (Tabuleiro t : todas) canonicas.add(t.canonico().chave());

            saida.add(new Contagem(n, todas.size(), canonicas.size(), tentativas));
        }

        return contagem = List.copyOf(saida);
    }

    public static Contagem contagem(int n) {
        return contagem().stream().filter(x -> x.n() == n).findFirst().orElseThrow();
    }

    /** A sequência tabelada desde 1850, para comparar. */
    public static int[] tabeladas() {
        return new int[] {1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680};
    }

    /* ===================== 3. fundamentais ===================== */

    public record Formas(int n, int solucoes, int fundamentais, Map<Integer, Integer> porFormas,
            double dividindoPorOito) {}

    private static List<Formas> fundamentais;

    /**
     * POR QUE DIVIDIR POR OITO NÃO DÁ A RESPOSTA.
     *
     * <p>Girar e espelhar não cria solução nova, então é tentador dividir 92 por 8 e dizer que
     * são 11,5 soluções de verdade. O número não é inteiro, e isso é o aviso.
     *
     * <p>O motivo é que nem toda solução tem oito formas diferentes. Uma solução simétrica é
     * igual a si mesma girada, e aí o grupo dela tem quatro membros em vez de oito. A tabela
     * mostra exatamente quantas de cada tipo existem.
     */
    public static List<Formas> fundamentais() {
        if (fundamentais != null) return fundamentais;

        List<Formas> saida = new ArrayList<>();

        for (int n = 4; n <= 10; n++) {
            List<Tabuleiro> todas = solucoes(n);

            if (todas.isEmpty()) continue;

            Map<Integer, Integer> porFormas = new TreeMap<>();

            Set<String> canonicas = new HashSet<>();

            for (Tabuleiro t : todas) {
                porFormas.merge(t.quantasFormasDistintas(), 1, Integer::sum);

                canonicas.add(t.canonico().chave());
            }

            saida.add(new Formas(n, todas.size(), canonicas.size(), Map.copyOf(porFormas),
                    todas.size() / 8.0));
        }

        return fundamentais = List.copyOf(saida);
    }

    public static Formas formas(int n) {
        return fundamentais().stream().filter(x -> x.n() == n).findFirst().orElseThrow();
    }

    /* ===================== 4. poda ===================== */

    public record Custo(int n, long porCasas, long porColunas, long comPoda) {

        public double ganhoDaPoda() {
            return comPoda == 0 ? 0 : porColunas / (double) comPoda;
        }
    }

    private static List<Custo> poda;

    /**
     * O PREÇO DE PERGUNTAR TARDE.
     *
     * <p>As três buscas resolvem o mesmo problema e dão o mesmo resultado. A diferença entre a
     * segunda e a terceira é só QUANDO a verificação acontece: no fim, depois de montar tudo,
     * ou a cada rainha posta.
     *
     * <p>Não há ideia nova na terceira. Há uma pergunta feita mais cedo, e ela vale fatores
     * que crescem com o tamanho.
     */
    public static List<Custo> poda() {
        if (poda != null) return poda;

        List<Custo> saida = new ArrayList<>();

        for (int n = 1; n <= 8; n++) {
            long casas = -1;

            if (n <= 5) {
                Busca.porCasas(n);
                casas = Busca.tentativas;
            }

            Busca.porColunas(n);

            long colunas = Busca.tentativas;

            Busca.comPoda(n);

            saida.add(new Custo(n, casas, colunas, Busca.tentativas));
        }

        return poda = List.copyOf(saida);
    }

    public static Custo poda(int n) {
        return poda().stream().filter(x -> x.n() == n).findFirst().orElseThrow();
    }

    /* ===================== 5. simetria ===================== */

    public record Simetria(int n, int solucoes, int comOitoFormas, int comQuatroFormas,
            int comDuasFormas, int naoMagicas) {}

    private static List<Simetria> simetria;

    /**
     * AS SOLUÇÕES SIMÉTRICAS, que são a exceção que estraga a divisão.
     *
     * <p>Também confere o que não pode falhar: girar ou espelhar uma solução tem de dar outra
     * solução. Se alguma forma deixasse de ser válida, a ideia inteira de contar por classes
     * estaria errada.
     */
    public static List<Simetria> simetria() {
        if (simetria != null) return simetria;

        List<Simetria> saida = new ArrayList<>();

        for (int n = 4; n <= 10; n++) {
            List<Tabuleiro> todas = solucoes(n);

            if (todas.isEmpty()) continue;

            int oito = 0;
            int quatro = 0;
            int duas = 0;
            int invalidas = 0;

            for (Tabuleiro t : todas) {
                int formas = t.quantasFormasDistintas();

                if (formas == 8) oito++;
                else if (formas == 4) quatro++;
                else if (formas == 2) duas++;

                for (Tabuleiro forma : t.todasAsFormas())
                    if (!forma.valido()) invalidas++;
            }

            saida.add(new Simetria(n, todas.size(), oito, quatro, duas, invalidas));
        }

        return simetria = List.copyOf(saida);
    }

    public static Simetria simetria(int n) {
        return simetria().stream().filter(x -> x.n() == n).findFirst().orElseThrow();
    }
}
