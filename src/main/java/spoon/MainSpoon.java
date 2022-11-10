package spoon;

import clusters.AbstractCluster;
import processors.ClassProcessor;
import spoon.reflect.CtModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import static java.lang.Integer.parseInt;

public class MainSpoon {
    public static String projectPath;
    public static void main(String[] args) throws IOException, URISyntaxException {

        System.out.println("************* WELCOME, BIENVENUE DANS NOTRE APPLICATION DE COMPREHENSION DE LOGICIELS EN UTILISANT SPOON:) ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("VEUILLEZ ENTRER LE CHEMIN DU PROJET QUE VOUS SOUHAITEZ ANALYSER.");
        projectPath = reader.readLine();
        SpoonParser spoonParser = new SpoonParser(projectPath);
        CtModel model = spoonParser.getModel();
        ClassProcessor classProcessor = new ClassProcessor(model);
        printMenu(classProcessor);

    }

    public static void printMenu(ClassProcessor processor) throws IOException, URISyntaxException {
        System.out.println("************************ MENU ************************");
        System.out.println("1.           COUPLAGE ENTRE DEUX CLASSES DE VOTRE PROJET.");
        System.out.println("2.           GENERER LE GRAPHE DE COUPLAGE PONDERE ENTRE LES CLASSES.");
        System.out.println("3.           AFFICHER LE DENDROGRAMME SOUS FORME D'UNE LISTE.");
        System.out.println("4.           AFFICHER LA LISTE DES MODULES.");
        System.out.println("0.           ARRETER L'APPLICATION.");
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
        switch (parseInt(reader1.readLine())) {
            case 1:
                System.out.println("************************ COUPLAGE ************************");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("VEUILLEZ ENTRER LE NOM COMPLET DE VOTRE PREMIERE CLASSE.");
                String classA = reader.readLine();
                System.out.println("VEUILLEZ ENTRER LE NOM COMPLET DE VOTRE DEUXIEME CLASSE.");
                String classB = reader.readLine();
                System.out.println(" LE COUPLAGE ENTRE CES DEUX CLASSES EST : "+processor.getCouplageMetrique(classA,classB));
                printMenu(processor);
                break;
            case 2:
                System.out.println("************************ GRAPH DE COUPLAGE PONDERE ************************");
                processor.displayGraph();
                processor.graphToDotFile("graph.dot");
                processor.dotToPng("graph.dot");
                System.out.println("VERIFIEZ SUR LE FICHIER GRAPH.PNG QUE VOUS AVEZ BIEN LE GRAPHE :)");
                printMenu(processor);
                break;
            case 3:
                System.out.println("************************ DENDROGRAMME ************************");
                System.out.println(processor.clusteringHierarchique());
                printMenu(processor);
                break;
            case 4:
                System.out.println("************************ IDENTIFICATION DE MODULES ************************");
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("VEUILLEZ ENTRER LE PARAMETER DE COUPLAGE CP.");
                String cp = reader2.readLine();
                processor.displayIdentificationModules(Double.parseDouble(cp));
                printMenu(processor);
                break;
            case 0:
                System.exit(0);
                break;
            default:
                System.out.println("VEUILLEZ SAISIR UN CHIFFRE VALIDE (1, 2, 3, 4)");
                printMenu(processor);
                break;
        }

    }
}
