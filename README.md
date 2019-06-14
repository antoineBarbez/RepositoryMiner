# RepositoryMiner

RepositoryMiner is a project/API that allows to extract the history of source code metrics.
The RepositoryMiner walks through the history of revisions (i.e., commits) of a project and allows compute the values of several software metrics at each revision. Note that the RepositoryMiner can also be used to simply compute the values the metrics at a specific revision.

The RepositoryMiner produces a set of *.csv* files. Each file contains the values of the chosen metrics (e.g., Lines Of Code) computed for a set of predefined code components (e.g. classes or methods), **at a specific revision of the system**.

## API usage example
As it is currently implemented, the main class (RepositoryMiner.java) extracts the history of some metrics related to the God Class and the Feature Envy anti-patterns.

However, if you want to make your own use of the RepositoryMiner, you can find below a simple example showing how to extract the history of three metrics:
* LOC (Lines Of Code)
* LCOM5 (Lack of COhesion in Methods)
* WMC (Weighted Method Count)

In this example, we extract the history of these three metrics for each class of the system under investigation.
```Java
String repoPath = "~/my-repository";
String commitId = "c241cad754ecf27c96b09f1e585b8be341dfcb71";
String outputDir = "~/history";

// For the example suppose that here you create an
// org.eclipse.jgit.api.Git object that represents your repository
Git git = openRepository(repoPath);

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
  public List<String> getMetricValues(String name) {
    ClassObject c = SystemObject.getInstance().getClassByName(name);

    // The class does not exist anymore in this revision
    if (c == null) {
      return Arrays.asList("NA", "NA", "NA");
    }

    List<String> metricValues = new ArrayList<String>();
    metricValues.add(String.valueOf(LOC.compute(c)));
    metricValues.add(String.valueOf(LCOM5.compute(c)));
    metricValues.add(String.valueOf(WMC.compute(c)));
    return metricValues;
  }
});

// To limit the number of revisions to be considered (100 here)
metricsExtractor.extractFromCommit(commitId, new String[]{""}, outputDir, 100);

// To extract the history between two commits
metricsExtractor.extractBetweenCommits(commitId1, commitId2, new String[]{""}, outputDir);

// To simply compute the metrics at a specific commit
metricsExtractor.extractAtCommit(commitId, new String[]{""}, outputDir);
```

### Good to know
Note that the RepositoryMiner creates a new *.csv* file only if it is different from the previously created one, i.e., only if at least one metric has been changed for at least one component. You can modify this behaviour by implementing your own IMetricFileBuilder.

Also, the RepositoryMiner currently implements 12 class and method related software metrics but you can easily define new ones. 
