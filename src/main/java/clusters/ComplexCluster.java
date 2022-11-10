package clusters;

import java.util.ArrayList;
import java.util.List;

public class ComplexCluster extends AbstractCluster{

    List<AbstractCluster> clusters;

    public ComplexCluster(){
        this.clusters = new ArrayList<>();
    }

    public List<AbstractCluster> getClusters() {
        return clusters;
    }

    public void addCluster(AbstractCluster cluster){
        clusters.add(cluster);
    }

    @Override
    public String toString() {
        StringBuilder clusterStrBuilder = new StringBuilder();
        clusterStrBuilder.append("( ");
        int clusterSize = this.getClusters().size();
        for (int i = 0; i < clusterSize; i++) {
            clusterStrBuilder.append((i != 0 ? ", " : "") + this.getClusters().get(i).toString());
        }
        clusterStrBuilder.append(" )");
        return  clusterStrBuilder.toString();
    }

    public List<String> getClusterClasses(){
        List<String> listClasses = new ArrayList<>();
        for (AbstractCluster abstractCluster: clusters) {
            listClasses.addAll(abstractCluster.getClusterClasses());
        }
        return  listClasses;
    }

}
