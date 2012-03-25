#### ECJ - Psh Connector

Project used for introducing [PushGP](http://hampshire.edu/lspector/push.html) language features for [ECJ](http://cs.gmu.edu/~eclab/projects/ecj/).

Current structure of the project heavly depends on maven build system, packaging the standalone elements into jar libraries. 

* ECJ - project that wrapps the ECJ library with Maven build system.
* ECJ-Psh - project that enhances the capabilities of ECJ with PushGP langauge interpreter based on [Psh](https://github.com/jonklein/Psh) implementation.

#### How to Use

````bash
cd ECJ
mvn install 

cd ../ECJ-Psh
mvn install
````

#### License

TODO: put information about the license of ECJ and the license of the Psh interpreter

#### Authors

 * Tomasz Kamiński
 * Piotr Jessa

