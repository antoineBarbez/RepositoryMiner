# RepositoryMiner

RepositoryMiner is a project/API that allows to extract the history of source code metrics.
The RepositoryMiner walks through history of revisions (commits) of a project and allows compute the values of several software metrics at each revision.

## API usage example
Here we give a simple example showing how to extract the history of three metrics:
* LOC (Lines Of Code)
* LCOM5 (Lack of COhesion in Methods)
* WMC (Weighted Method Count)

In the example below, we extract the history of these metrics for each class of the system under investigation.
```Java
String repoPath = "~/my-repository";
String commitId = "c241cad754ecf27c96b09f1e585b8be341dfcb71";
String outputDir = "~/history";

MetricsExtractor metricsExtractor = new MetricsExtractor(git, new UnaryMetricFileBuilder() {
  @Override
  public List<String> getComponents() {
    SystemObject system = SystemObject.getInstance();
    
    List<String> classes = new ArrayList<String>();
    for(ClassObject c: system.getClasses()) {
      classes.add(c.getName());
    }
    return classes;
  }

  @Override
  public String getHeader() {
    return "ClassName;LOC;LCOM5;WMC";
  }
  
  @Override
  public List<String> getMetricsValues(String name) {
    ClassObject c = SystemObject.getInstance().getClassByName(name);
		
    List<String> metricValues = new ArrayList<String>();
    metricValues.add(String.valueOf(LOC.compute(c)));
    metricValues.add(String.valueOf(LCOM5.compute(c)));
    metricValues.add(String.valueOf(WMC.compute(c)));
    return metricValues;
  }
});

metricsExtractor.extractFromCommit(commitId, new String[]{""}, outputDir, 100);
```
