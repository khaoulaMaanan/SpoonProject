package clusters;

import java.util.ArrayList;
import java.util.List;

public class SimpleCluster extends AbstractCluster{

    String classe;

    public SimpleCluster(String classe){
        this.classe = classe;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    @Override
    public String toString() {
        return classe;
    }


    @Override
    public List<String> getClusterClasses() {
        List<String> listClasses = new ArrayList<>();
        listClasses.add(getClasse());
        return listClasses;
    }

    @Override
    public List<AbstractCluster> getClusters() {
        List<AbstractCluster> clusters=new ArrayList<>();
        SimpleCluster simpleCluster=new SimpleCluster(getClasse());
        clusters.add(simpleCluster);
        return clusters;
    }
}
