package br.com.conde.rainhas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A definição e as simetrias.
 *
 * <p>{@code valido()} é o juiz de tudo aqui, então ele precisa reprovar o que deve: duas na
 * mesma linha, duas na mesma diagonal, rainha fora do tabuleiro.
 */
class TabuleiroTest {

    @Test
    @DisplayName("duas na mesma linha ou na mesma diagonal se atacam")
    void seAtacam() {
        assertTrue(Tabuleiro.seAtacam(0, 3, 5, 3), "mesma linha");
        assertTrue(Tabuleiro.seAtacam(0, 0, 3, 3), "diagonal descendo");
        assertTrue(Tabuleiro.seAtacam(0, 3, 3, 0), "diagonal subindo");

        assertFalse(Tabuleiro.seAtacam(0, 0, 1, 2), "salto de cavalo nao ataca");
        assertFalse(Tabuleiro.seAtacam(0, 0, 2, 1));
    }

    @Test
    @DisplayName("a representacao impede duas na mesma coluna, sem precisar conferir")
    void umaPorColuna() {
        /* Nao existe maneira de representar duas rainhas na coluna 0: o vetor tem uma
         * posicao por coluna. A restricao sumiu do codigo porque sumiu da representacao. */
        Tabuleiro t = new Tabuleiro(new int[] {1, 3, 0, 2});

        assertEquals(4, t.tamanho());
        assertTrue(t.valido());
    }

    @Test
    @DisplayName("O JUIZ REPROVA linha repetida, diagonal e rainha fora")
    void juizReprova() {
        assertFalse(new Tabuleiro(new int[] {0, 0, 2, 3}).valido(), "mesma linha");
        assertFalse(new Tabuleiro(new int[] {0, 1, 2, 3}).valido(), "diagonal inteira");
        assertFalse(new Tabuleiro(new int[] {0, 2, 1, 3}).valido(), "diagonal curta");
        assertFalse(new Tabuleiro(new int[] {0, 4, 2, 3}).valido(), "linha fora do tabuleiro");
        assertFalse(new Tabuleiro(new int[] {-1, 1, 2, 3}).valido(), "linha negativa");
    }

    @Test
    @DisplayName("as duas solucoes de quatro rainhas, escritas a mao")
    void asDuasDeQuatro() {
        assertTrue(new Tabuleiro(new int[] {1, 3, 0, 2}).valido());
        assertTrue(new Tabuleiro(new int[] {2, 0, 3, 1}).valido());

        /* E sao as unicas: qualquer outra das 256 combinacoes falha. */
        int validas = 0;

        for (int a = 0; a < 4; a++)
            for (int b = 0; b < 4; b++)
                for (int c = 0; c < 4; c++)
                    for (int d = 0; d < 4; d++)
                        if (new Tabuleiro(new int[] {a, b, c, d}).valido()) validas++;

        assertEquals(2, validas);
    }

    @Test
    @DisplayName("girar quatro vezes volta ao mesmo, e espelhar duas tambem")
    void simetriasSaoInvolucoes() {
        Tabuleiro t = new Tabuleiro(new int[] {1, 3, 0, 2});

        assertEquals(t, t.girado().girado().girado().girado());
        assertEquals(t, t.espelhado().espelhado());
    }

    @Test
    @DisplayName("TODA FORMA de uma solucao continua sendo solucao")
    void formasContinuamValidas() {
        for (int n = 4; n <= 8; n++)
            for (Tabuleiro solucao : Busca.comPoda(n))
                for (Tabuleiro forma : solucao.todasAsFormas())
                    assertTrue(forma.valido(), n + ": " + forma);
    }

    @Test
    @DisplayName("a canonica e a mesma para todas as formas de uma solucao")
    void canonicaCompartilhada() {
        for (Tabuleiro solucao : Busca.comPoda(6)) {
            String canonica = solucao.canonico().chave();

            for (Tabuleiro forma : solucao.todasAsFormas())
                assertEquals(canonica, forma.canonico().chave(), forma.toString());
        }
    }

    @Test
    @DisplayName("solucao simetrica tem menos de oito formas distintas")
    void simetricaTemMenosFormas() {
        Set<Integer> quantidades = new HashSet<>();

        for (Tabuleiro solucao : Busca.comPoda(8))
            quantidades.add(solucao.quantasFormasDistintas());

        /* Com oito rainhas so aparecem solucoes de 8 formas e de 4 formas. */
        assertEquals(Set.of(4, 8), quantidades);
    }
}
