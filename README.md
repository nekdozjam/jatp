# Java Automated Theorem Prover

Simple Resolution based Automated Theorem Prover for first-order logic with equality. 
This prover supports [TPTP](http://www.tptp.org/) format formulas.

Bachelors Thesis project

## Getting Started



### Prerequisites

* [JDK 8+](https://jdk.java.net/)
* [Maven 3](https://maven.apache.org/)

### Installation

At first make sure you have installed all the prerequisites.

Open root folder in terminal and run

```
mvn clean install
```

Application jar will be generated in the `target` folder.


## Running the application

You can execute the application jar located in the `target` folder directly using following command

```
java -jar target/jatp-1.0-SNAPSHOT.jar problemfile.p
```

### Configuration


To set parent directory for included files

```
-includes includefiles
```

To set parent directory for problem files

```
-basedir problemfiles
```

To set maximum solving time

```
-maxtime 150
```

To enable verbose output

```
-verbose
```

To enable profiling output

```
-profile
```

To enable debug output

```
-debug
```
Note: debug option outputs a lot of data and heavily affects the performance

To dump active list at the end of execution:

```
-dumpActive
```

To dump units list at the end of execution:

```
-dumpUnits
```

To dump rewrite rules at the end of execution:

```
-dumpRewrites
```

To set the number of oldest clauses to be selected to N:

```
-selectAge N
```

To set the number of smallest clauses to be selected to N:

```
-selectSmallest N
```

To limit the size of newly generated clauses to N:

```
-maxClauseSize N
```

To limit the number of variables of newly generated clauses to N:

```
-maxClauseVariables N
```

Example usage:

```
java -jar target/jatp-1.0-Final.jar -maxtime 30 -basedir problems p1.p
```

## Authors

* **Martin Mazánek** - [nekdozjam](https://github.com/nekdozjam)

## License
* This project is licensed under the GPL License - see the [LICENSE](LICENSE) file for details
