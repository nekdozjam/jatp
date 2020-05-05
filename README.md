# Java Automated Theorem Prover

Simple Resolution based Automated Theorem Prover for first-order logic with equality. 
This prover supports [TPTP](http://www.tptp.org/) format formulas.

Bachelors Thesis project

## Getting Started



### Prerequisites

* [JDK 8+](https://jdk.java.net/)
* [Maven 3](https://maven.apache.org/)

### Installation

TODO: git download/clone
At first make sure you have installed all the prerequisites.

Open root folder in terminal and run

```
mvn clean install
```

Application jar will be generated in the `target` folder.

```
until finished
```

### Build using GraalVM

* TBD


## Running the application

You can execute the application jar located in the `target` folder directly using following command

```
java -jar target/jatp-1.0-SNAPSHOT.jar problemfile.p
```

### Configuration

* TBD

To set parent directory for included files

```
-i includefiles
--includes includefiles
```

To set parent directory for problem files

```
-b problemfiles
-- basedir problemfiles
```

To set maximum solving time

```
-t 150
--time 150
```

Example usage:

```
java -jar target/jatp-1.0-SNAPSHOT.jar -t 150 -i includes testi.txt
```

## Authors

* **Martin Mazánek** - [nekdozjam](https://github.com/nekdozjam)

## License
* TBD
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
