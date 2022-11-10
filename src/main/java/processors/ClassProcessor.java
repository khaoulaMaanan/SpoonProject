package processors;


import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import clusters.AbstractCluster;
import clusters.ComplexCluster;
import clusters.SimpleCluster;

import org.javatuples.Pair;
import org.paukov.combinatorics3.Generator;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ClassProcessor {

    CtModel ctModel;
    double couplageMetrique;
    double sommeMethodInvocation;
    HashMap<Pair<String, String>, Double> mapCouplageClasses;
    List<String > couplageClassesString;

    public ClassProcessor(CtModel ctModel) {
        this.ctModel = ctModel;
        this.couplageMetrique = 0;
        this.sommeMethodInvocation=0;
        this.mapCouplageClasses = new HashMap<>();
        this.couplageClassesString = new ArrayList<>();

    }
    public double getNbreMethodInvocation() {
        sommeMethodInvocation=0;
        for (CtType<?> node : ctModel.getAllTypes()) {
            for (CtMethod<?> nodeMethod : node.getAllMethods()) {
                sommeMethodInvocation+= Query.getElements(nodeMethod, new TypeFilter<>(CtInvocation.class)).size();
            }
        }
        return sommeMethodInvocation;
    }

    public double getCouplageMetrique(String classA, String classB) {
        couplageMetrique = 0;
        List<CtType<?>> typesStream = ctModel.getAllTypes().stream()
                .filter(t -> t.getQualifiedName().equals(classA) || t.getQualifiedName().equals(classB))
                .collect(Collectors.toList());
        for (int i = 0; i < typesStream.size(); i++) {

            for (CtMethod<?> nodeMethod : typesStream.get(i).getAllMethods()) {
                for (CtInvocation ctInvocation : Query.getElements(nodeMethod, new TypeFilter<>(CtInvocation.class))) {
                    int j = i == 0 ? i + 1 : i - 1;
                    if (ctInvocation.getTarget() != null) {
                        if (ctInvocation.getTarget().getType() != null) {
                            if(ctInvocation.getTarget().getType().getQualifiedName().equals(typesStream.get(j).getQualifiedName())){
                                couplageMetrique++;
                            }
                        }
                    }
                }
            }

        }
        return (couplageMetrique/getNbreMethodInvocation()) ;
    }

    public Stream<List<String>> generate(List<String> list) {
        return Generator.combination(list).simple(2).stream();
    }

    public HashMap<Pair<String, String>, Double> getCouplageOfAllClasses(List<String> classes){

        generate(classes).forEach(c->{
            mapCouplageClasses.put(new Pair<>(c.get(0), c.get(1)), getCouplageMetrique(c.get(0), c.get(1)));

            int index0 = c.get(0).lastIndexOf('.');
            String name0 = c.get(0).substring(index0 +1);
            int index1 = c.get(1).lastIndexOf('.');
            String name1 = c.get(1).substring(index1 +1);


            if (getCouplageMetrique(c.get(0),c.get(1)) != 0){
            couplageClassesString.add("\t"+name0+" -- "+name1+"[label  =\""+getCouplageMetrique(c.get(0),c.get(1))+"\"];\n");}
        });

        return mapCouplageClasses;
    }

    public void graphToDotFile(String fileGraphPath) throws IOException {

        FileWriter fW = new FileWriter(fileGraphPath);
        fW.write("graph G {\n");
        for (String className :
                couplageClassesString) {

            fW.write(className);
        }
        fW.write("}");
        fW.close();
    }

    public  void dotToPng(String fileGraphPath) {
        try {

            MutableGraph mutableGraph = new guru.nidi.graphviz.parse.Parser().read(new File(fileGraphPath));
            Graphviz.fromGraph(mutableGraph).render(Format.PNG).toFile(new File("graph.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<CtType<?>> extractionInformation() throws IOException {
        return (List<CtType<?>>) ctModel.getAllTypes();
    }

    public Pair<AbstractCluster,AbstractCluster> clusterProche(List<AbstractCluster> clusters){
        Map<Pair<AbstractCluster,AbstractCluster>,Double> mapCouplageClusters=new HashMap<>();
        final double[] couplage = new double[1];
        Pair<AbstractCluster,AbstractCluster> key = null;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i+1; j <clusters.size(); j++) {
                int finalJ = j;
                couplage[0] =0;
                clusters.get(i).getClusterClasses().forEach(cl->{
                    clusters.get(finalJ).getClusterClasses().forEach(cl2->{
                        couplage[0] +=getCouplageMetrique(cl,cl2);
                    });
                });
                 mapCouplageClusters.put(new Pair<>(clusters.get(i),clusters.get(j)), couplage[0]);
            }
        }
        if(mapCouplageClusters.size()>=1){
            Map<Pair<AbstractCluster,AbstractCluster>, Double> result = mapCouplageClusters.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            Map.Entry<Pair<AbstractCluster,AbstractCluster>,Double> entry = result.entrySet().iterator().next();
            key=entry.getKey();
            key.getValue0().setMetriqueCouplage(entry.getValue());
            key.getValue1().setMetriqueCouplage(entry.getValue());
        }

        return key;

    }

    public AbstractCluster clusteringHierarchique() throws IOException {

        List<CtType<?>> classes=extractionInformation();

        ComplexCluster complexCluster = new ComplexCluster();
        //initialization
        for(CtType<?> type:classes){
            SimpleCluster c=new SimpleCluster(type.getQualifiedName());
            complexCluster.addCluster(c);
        }

        while (complexCluster.getClusters().size()>1 && clusterProche(complexCluster.getClusters())!=null){

            Pair<AbstractCluster,AbstractCluster> pairProche =  clusterProche(complexCluster.getClusters());
            ComplexCluster complexCluster1 = new ComplexCluster();
            complexCluster1.addCluster(pairProche.getValue0());
            complexCluster1.addCluster(pairProche.getValue1());
            complexCluster1.setMetriqueCouplage(pairProche.getValue0().getMetriqueCouplage());
            complexCluster1.setAdded(true);

            complexCluster.getClusters().remove(pairProche.getValue0());
            complexCluster.getClusters().remove(pairProche.getValue1());

            complexCluster.addCluster(complexCluster1);
        }
        List<AbstractCluster> complexClusterList=complexCluster.getClusters().stream().filter(c->c.isAdded()).collect(Collectors.toList());
        return complexClusterList.get(0);
    }

    public List<AbstractCluster> modulesIdentification(AbstractCluster abstractCluster ,double cp) throws IOException {

        List<AbstractCluster> modules = new ArrayList<>();
        double size= extractionInformation().size()/2;
        final double[] sommeCouplage = {0};


            if (abstractCluster.getClusters().size()>1){
                generate(abstractCluster.getClusterClasses()).collect(Collectors.toList()).forEach(c -> {
                    sommeCouplage[0] += getCouplageMetrique(c.get(0), c.get(1));
                });
                double average = 0;
                average = sommeCouplage[0] / abstractCluster.getClusterClasses().size();
                if (average >= cp) {
                    if (modules.size() < size) {
                        modules.add(abstractCluster);
                        modules.addAll(modulesIdentification(abstractCluster.getClusters().get(0),cp));
                        modules.addAll(modulesIdentification(abstractCluster.getClusters().get(1), cp));
                    }
                }

            }

       return modules;
    }

    public void displayGraph(){
        List<String> classes = new ArrayList<>();
        for (CtType<?> type : ctModel.getAllTypes() ){
            classes.add(type.getQualifiedName());
        }
        getCouplageOfAllClasses(classes);
    }
    public List<AbstractCluster> displayIdentificationModules(double cp) throws IOException {
        AbstractCluster dendrogramme = clusteringHierarchique();
        System.out.println(modulesIdentification(dendrogramme, cp));
        return modulesIdentification(dendrogramme, cp);
    }
}
