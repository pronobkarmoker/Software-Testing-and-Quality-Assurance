# Testing Arithmetic Operations

## Purpose of this module

This module is here to let you see how basic arithmetic operations are tested.

## Getting Started

First you need to clone the repository in a folder of your preference. Then through the command line head to the root directory of your project and 
execute the command:
```
mvn clean package jacoco:report
```
This will let you package the project, see how the classes are being tested,  and generate a code coverall report in the folder :
```
target/site/jacoco
```

### Arithmetic Operations Testing

The current module uses certain dependencies and plugins to test common Arithmetic Operations between numbers or arrays. 
**Mockito Dependency**
```
	<dependency>
		<groupId>org.mockito</groupId>
		<artifactId>mockito-core</artifactId>
		<version>2.27.0</version>
		<scope>test</scope>
	</dependency>
```

**Jacoco plugin**
```
	<plugin>
		<groupId>org.jacoco</groupId>
		<artifactId>jacoco-maven-plugin</artifactId>
		<version>0.8.3</version>
		<executions>
			<execution>
				<id>prepare-agent</id>
				<goals>
					<goal>prepare-agent</goal>
				</goals>
			</execution>
		</executions>
	</plugin>

```

**Coveralls plugin**
```
	<plugin>
		<groupId>org.eluder.coveralls</groupId>
		<artifactId>coveralls-maven-plugin</artifactId>
		<version>4.3.0</version>
	</plugin>
```

In order to see the usage of this module, after building it, you need to will be able to see how the 
classes are tested and the following Jacoco report.

## Built With

* [Mockito](https://mvnrepository.com/artifact/org.mockito/mockito-all) - Repository used for mock testing
* [JUnit 4.12](https://mvnrepository.com/artifact/junit/junit/4.12) - Repository used for testing
* [Maven](https://maven.apache.org/) - Dependency Management
