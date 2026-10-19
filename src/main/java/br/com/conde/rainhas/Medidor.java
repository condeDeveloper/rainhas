package br.com.conde.rainhas;

import java.util.Locale;

/** O MEDIDOR: roda as buscas e imprime as tabelas do README. */
public final class Medidor {

    private Medidor() {}

    public static void main(String[] argumentos) {
        Locale.setDefault(Locale.US);

        String passo = argumentos.length > 0 ? argumentos[0] : "tudo";

        switch (passo) {
            case "correcao" -> correcao();
            case "contagem" -> contagem();
            case "fundamentais" -> fundamentais();
            case "poda" -> poda();
            case "simetria" -> simetria();
            default -> {
                correcao();
                contagem();
                fundamentais();
                poda();
                simetria();
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("=".repeat(texto.length()));
        System.out.println();
    }

    static void correcao() {
        titulo("1. as buscas concordam, e tudo passa pela definicao");

        System.out.println("  n  sem poda  com poda  invalidas  conjuntos iguais");

        for (Medidas.Correcao linha : Medidas.correcao())
            System.out.printf("%3d  %8d  %8d  %9d  %16s%n", linha.n(), linha.porColunas(),
                    linha.comPoda(), linha.invalidas(), linha.iguais() ? "sim" : "NAO");

        System.out.println();
        System.out.println("podar e descartar sem olhar o resto, e e por isso que a busca com");
        System.out.println("poda precisa ser comparada com a que monta tudo antes de perguntar.");
    }

    static void contagem() {
        titulo("2. quantas solucoes existem, por tamanho");

        System.out.println("  n  solucoes  tabelado  fundamentais");

        int[] tab = Medidas.tabeladas();

        for (Medidas.Contagem linha : Medidas.contagem())
            System.out.printf("%3d  %8d  %8d  %12d%s%n", linha.n(), linha.solucoes(),
                    tab[linha.n() - 1], linha.fundamentais(),
                    linha.solucoes() == tab[linha.n() - 1] ? "" : "   <<< DIFERE");

        System.out.println();
        System.out.println("repare no 4 logo depois do 10: o tabuleiro de SEIS tem MENOS");
        System.out.println("solucoes que o de cinco. mais espaco nao significa mais solucao.");
        System.out.println();
        System.out.println("e nao ha nenhuma com 2 nem com 3 rainhas: tres rainhas nao cabem");
        System.out.println("num tabuleiro 3 por 3 sem se atacarem, e isso tambem e resultado.");
    }

    static void fundamentais() {
        titulo("3. por que dividir por oito nao da a resposta");

        System.out.println("  n  solucoes  dividido por 8  fundamentais  quantas tem cada numero de formas");

        for (Medidas.Formas linha : Medidas.fundamentais())
            System.out.printf("%3d  %8d  %14.2f  %12d  %s%n", linha.n(), linha.solucoes(),
                    linha.dividindoPorOito(), linha.fundamentais(), linha.porFormas());

        Medidas.Formas oito = Medidas.formas(8);

        System.out.println();
        System.out.printf("com 8 rainhas: %d solucoes, e %.1f nao e um numero inteiro.%n",
                oito.solucoes(), oito.dividindoPorOito());
        System.out.println("o aviso esta ai. nem toda solucao tem oito formas diferentes:");
        System.out.printf("  %d delas tem so quatro, porque sao iguais a si mesmas giradas.%n",
                oito.porFormas().getOrDefault(4, 0));
        System.out.printf("  88 dividido por 8, mais 4 dividido por 4, da %d.%n",
                oito.fundamentais());
    }

    static void poda() {
        titulo("4. o preco de perguntar tarde");

        System.out.println("  n  por casas  por colunas  com poda  ganho da poda");

        for (Medidas.Custo linha : Medidas.poda())
            System.out.printf("%3d  %9s  %11d  %8d  %12.1fx%n", linha.n(),
                    linha.porCasas() < 0 ? "-" : String.valueOf(linha.porCasas()),
                    linha.porColunas(), linha.comPoda(), linha.ganhoDaPoda());

        System.out.println();
        System.out.println("as tres resolvem o mesmo problema e dao o mesmo resultado. entre a");
        System.out.println("segunda e a terceira nao ha ideia nova: ha uma pergunta feita mais");
        System.out.println("cedo, e ela vale fatores que crescem com o tamanho.");
    }

    static void simetria() {
        titulo("5. as solucoes simetricas");

        System.out.println("  n  solucoes  com 8 formas  com 4 formas  com 2 formas  formas invalidas");

        for (Medidas.Simetria linha : Medidas.simetria())
            System.out.printf("%3d  %8d  %12d  %12d  %12d  %16d%n", linha.n(),
                    linha.solucoes(), linha.comOitoFormas(), linha.comQuatroFormas(),
                    linha.comDuasFormas(), linha.naoMagicas());

        System.out.println();
        System.out.println("a ultima coluna e zero em toda linha: girar ou espelhar uma solucao");
        System.out.println("sempre da outra solucao. se nao desse, contar por classes nao faria");
        System.out.println("sentido nenhum, e as 12 fundamentais seriam invencao.");
    }
}
