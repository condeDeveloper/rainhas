package br.com.conde.rainhas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os números que aparecem escritos no README, travados em teste. */
class MedidasTest {

    /* ===================== 1. correcao ===================== */

    @Test
    @DisplayName("a busca com poda acha exatamente o mesmo conjunto que a sem poda")
    void buscasConcordam() {
        for (Medidas.Correcao linha : Medidas.correcao()) {
            assertTrue(linha.iguais(), "n = " + linha.n());
            assertEquals(0, linha.invalidas(), "n = " + linha.n());
            assertEquals(linha.porColunas(), linha.comPoda(), "n = " + linha.n());
        }
    }

    /* ===================== 2. contagem ===================== */

    @Test
    @DisplayName("a contagem bate com a sequencia tabelada desde 1850")
    void contagemBateComATabela() {
        int[] tabeladas = Medidas.tabeladas();

        for (Medidas.Contagem linha : Medidas.contagem())
            assertEquals(tabeladas[linha.n() - 1], linha.solucoes(), "n = " + linha.n());

        assertEquals(92, Medidas.contagem(8).solucoes());
        assertEquals(2680, Medidas.contagem(11).solucoes());
    }

    @Test
    @DisplayName("O TABULEIRO DE SEIS TEM MENOS SOLUCOES QUE O DE CINCO")
    void seisTemMenosQueCinco() {
        assertEquals(10, Medidas.contagem(5).solucoes());
        assertEquals(4, Medidas.contagem(6).solucoes());

        assertTrue(Medidas.contagem(6).solucoes() < Medidas.contagem(5).solucoes());
    }

    @Test
    @DisplayName("com duas e com tres rainhas nao existe nenhuma")
    void duasETresNaoTem() {
        assertEquals(0, Medidas.contagem(2).solucoes());
        assertEquals(0, Medidas.contagem(3).solucoes());

        assertEquals(1, Medidas.contagem(1).solucoes());
    }

    /* ===================== 3. fundamentais ===================== */

    @Test
    @DisplayName("DIVIDIR 92 POR 8 DA 11,5, que nao e um numero de solucoes")
    void dividirPorOitoNaoDa() {
        Medidas.Formas oito = Medidas.formas(8);

        assertEquals(11.5, oito.dividindoPorOito(), 1e-12);
        assertEquals(12, oito.fundamentais());

        /* O motivo: 88 solucoes tem oito formas, e 4 tem so quatro. */
        assertEquals(88, oito.porFormas().get(8));
        assertEquals(4, oito.porFormas().get(4));

        assertEquals(88 / 8 + 4 / 4, oito.fundamentais());
    }

    @Test
    @DisplayName("e a conta por classes fecha em todo tamanho medido")
    void contaPorClassesFecha() {
        for (Medidas.Formas linha : Medidas.fundamentais()) {
            int soma = 0;

            for (var par : linha.porFormas().entrySet()) soma += par.getValue() / par.getKey();

            assertEquals(linha.fundamentais(), soma, "n = " + linha.n());

            int total = 0;

            for (int quantas : linha.porFormas().values()) total += quantas;

            assertEquals(linha.solucoes(), total, "n = " + linha.n());
        }
    }

    @Test
    @DisplayName("as fundamentais conhecidas: 1, 2, 1, 6, 12, 46, 92")
    void fundamentaisConhecidas() {
        assertEquals(1, Medidas.formas(4).fundamentais());
        assertEquals(2, Medidas.formas(5).fundamentais());
        assertEquals(1, Medidas.formas(6).fundamentais());
        assertEquals(6, Medidas.formas(7).fundamentais());
        assertEquals(12, Medidas.formas(8).fundamentais());
        assertEquals(46, Medidas.formas(9).fundamentais());
        assertEquals(92, Medidas.formas(10).fundamentais());
    }

    /* ===================== 4. poda ===================== */

    @Test
    @DisplayName("PERGUNTAR CEDO vale mil vezes com oito rainhas")
    void podaValeMil() {
        Medidas.Custo oito = Medidas.poda(8);

        assertEquals(16_777_216L, oito.porColunas());
        assertEquals(15_720L, oito.comPoda());

        assertTrue(oito.ganhoDaPoda() > 1000, "ganho " + oito.ganhoDaPoda());
    }

    @Test
    @DisplayName("e o ganho CRESCE com o tamanho, a partir de tres")
    void ganhoCresce() {
        double anterior = 0;

        for (Medidas.Custo linha : Medidas.poda()) {
            if (linha.n() < 3) continue;

            assertTrue(linha.ganhoDaPoda() > anterior,
                    "n = " + linha.n() + ": " + linha.ganhoDaPoda());

            anterior = linha.ganhoDaPoda();
        }
    }

    @Test
    @DisplayName("por casas e inviavel depressa: 53 mil escolhas com cinco rainhas")
    void porCasasEhInviavel() {
        assertEquals(53_130L, Medidas.poda(5).porCasas());

        /* Que e exatamente 25 escolhe 5. */
        long combinacoes = 1;

        for (int passo = 0; passo < 5; passo++) combinacoes = combinacoes * (25 - passo) / (passo + 1);

        assertEquals(combinacoes, Medidas.poda(5).porCasas());
    }

    /* ===================== 5. simetria ===================== */

    @Test
    @DisplayName("girar ou espelhar NUNCA estraga uma solucao")
    void formasNuncaEstragam() {
        for (Medidas.Simetria linha : Medidas.simetria())
            assertEquals(0, linha.naoMagicas(), "n = " + linha.n());
    }

    @Test
    @DisplayName("com seis rainhas TODAS as solucoes sao simetricas")
    void seisEhTodoSimetrico() {
        Medidas.Simetria seis = Medidas.simetria(6);

        assertEquals(4, seis.solucoes());
        assertEquals(0, seis.comOitoFormas());
        assertEquals(4, seis.comQuatroFormas());

        /* Por isso as quatro solucoes de seis sao UMA fundamental so. */
        assertEquals(1, Medidas.formas(6).fundamentais());
    }
}
