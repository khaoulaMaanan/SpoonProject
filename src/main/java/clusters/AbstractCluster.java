package clusters;


import java.util.List;


public abstract class AbstractCluster {
    public boolean added=false;
    public double metriqueCouplage=0;

    public double getMetriqueCouplage() {
        return metriqueCouplage;
    }

    public void setMetriqueCouplage(double metriqueCouplage) {
        this.metriqueCouplage = metriqueCouplage;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public abstract List<String> getClusterClasses();
    public abstract List<AbstractCluster> getClusters();
}
